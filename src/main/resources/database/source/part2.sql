-- Creating periods_view
DROP VIEW IF EXISTS periods CASCADE;
CREATE OR REPLACE VIEW periods AS
    WITH
    periods_data AS (
        SELECT  cards.customer_id,
                sku.group_id,
                transactions.transaction_id,
                transactions.transaction_datetime,
                sku.sku_id,
                checks.sku_discount,
                checks.sku_summ,
                dense_rank() over (PARTITION BY customer_id, group_id ORDER BY transactions.transaction_id) +
                dense_rank() over (PARTITION BY customer_id, group_id ORDER BY transactions.transaction_id DESC) - 1
                                                                                AS uniq_transactions,
                min(checks.sku_discount) OVER (PARTITION BY transactions.transaction_id, group_id) /
                                                                                checks.sku_summ AS min_discount
                --min(checks.SKU_discount) OVER (PARTITION BY customer_id, group_id) / checks.SKU_summ AS min_discount
        FROM transactions
        INNER JOIN cards ON cards.customer_card_id = transactions.customer_card_id
        INNER JOIN checks ON checks.transaction_id = transactions.transaction_id
        INNER JOIN sku ON sku.sku_id = checks.sku_id
        ORDER BY cards.customer_id, sku.group_id
    )
    SELECT DISTINCT customer_id,
            group_id,
            min(transaction_datetime) OVER (PARTITION BY customer_id, group_id) AS First_Group_Purchase_Date,
            max(transaction_datetime) OVER (PARTITION BY customer_id, group_id) AS Last_Group_Purchase_Date,
            uniq_transactions AS Group_Purchase,
            (1 + extract(epoch from max(transaction_datetime) OVER (PARTITION BY customer_id, group_id) -
                                    min(transaction_datetime) OVER (PARTITION BY customer_id, group_id)) / 60 / 60 / 24)
                        / uniq_transactions AS Group_Frequency,
            min(min_discount) OVER (PARTITION BY customer_id, group_id) AS Group_Min_Discount       -- min_discount==0?
    FROM periods_data
    ORDER BY customer_id, group_id;

--- Checking ---
SELECT * FROM periods;


-- Creating purchase_history_view
DROP VIEW IF EXISTS purchase_history;
CREATE OR REPLACE VIEW purchase_history AS
    SELECT  DISTINCT cards.customer_id,
            transactions.transaction_id,
            transactions.transaction_datetime,
            sku.group_id,
            sum(stores.sku_purchase_price * checks.sku_amount) AS Group_Cost,
            sum(checks.sku_summ) AS Group_Summ,
            sum(checks.sku_summ_paid) AS Group_Summ_Paid
    FROM transactions
    INNER JOIN cards ON cards.customer_card_id = transactions.customer_card_id
    INNER JOIN checks ON checks.transaction_id = transactions.transaction_id
    INNER JOIN sku ON sku.sku_id = checks.sku_id
    INNER JOIN stores ON stores.transaction_store_id = transactions.transaction_store_id
                     AND stores.sku_id = sku.sku_id
    GROUP BY cards.customer_id,
             transactions.transaction_id,
             transactions.transaction_datetime,
             sku.group_id
    ORDER BY cards.customer_id, sku.group_id, transactions.transaction_datetime;

--- Checking ---
SELECT * FROM purchase_history;


-- Creating customers_view
DROP TABLE IF EXISTS customer_segmentation;
CREATE TABLE IF NOT EXISTS customer_segmentation(
    Segment_ID serial primary key,
    Average_Check varchar not null,
    Frequency_Purchases varchar not null,
    Churn_Probability varchar not null
);

CALL import_table('Customer_Segmentation.tsv', 'customer_segmentation');

SELECT * FROM customer_segmentation;


DROP FUNCTION IF EXISTS fnc_customer_segment(varchar, varchar, varchar) CASCADE;
CREATE OR REPLACE FUNCTION fnc_customer_segment(average_ varchar, frequency_ varchar, churn_ varchar) RETURNS int
AS $BODY$
    BEGIN
        RETURN (
            SELECT Segment_ID
            FROM customer_segmentation
            WHERE Average_Check = average_
                AND Frequency_Purchases = frequency_
                AND Churn_Probability = churn_
            );
    END;
$BODY$ LANGUAGE plpgsql;

--SELECT * FROM fnc_customer_segment('Low', 'Often', 'Low');  --  '7'
--SELECT * FROM fnc_customer_segment('Low', 'High', 'Low');   --  <null>


DROP VIEW IF EXISTS customers;
CREATE OR REPLACE VIEW customers AS
    WITH data_ AS (
        SELECT  DISTINCT cards.customer_id AS Customer_ID,
                avg(transactions.transaction_summ) OVER (PARTITION BY customer_id) AS Customer_Average_Check,
                (extract(epoch from max(transaction_datetime) OVER (PARTITION BY customer_id) -
                                    min(transaction_datetime) OVER (PARTITION BY customer_id)) / 60 / 60 / 24)
                        / count(transaction_id) OVER (PARTITION BY customer_id) AS Customer_Frequency,
                extract(epoch from now() - max(transaction_datetime) OVER (PARTITION BY customer_id)) / 60 / 60 / 24
                                    AS Customer_Inactive_Period,
                transaction_store_id
        FROM transactions
        INNER JOIN cards ON cards.customer_card_id = transactions.customer_card_id
    ),
    customer_data_ AS (
        SELECT  customer_id,
                customer_average_check,
                CASE WHEN ROW_NUMBER() OVER (ORDER BY customer_average_check DESC) <=
                          (SELECT count(DISTINCT customer_id) FROM data_) * 0.1 THEN 'High'
                     WHEN ROW_NUMBER() OVER (ORDER BY customer_average_check DESC) <=
                          (SELECT count(DISTINCT customer_id) FROM data_) * 0.35 THEN 'Medium'
                     ELSE 'Low'
                END AS Customer_Average_Check_Segment,
                customer_frequency,
                CASE WHEN ROW_NUMBER() OVER (ORDER BY customer_frequency) <=
                          (SELECT count(DISTINCT customer_id) FROM data_) * 0.1 THEN 'Often'
                     WHEN ROW_NUMBER() OVER (ORDER BY customer_frequency) <=
                          (SELECT count(DISTINCT customer_id) FROM data_) * 0.35 THEN 'Occasionally'
                     ELSE 'Rarely'
                END AS Customer_Frequency_Segment,
                customer_inactive_period,
                customer_inactive_period / customer_frequency AS Customer_Churn_Rate,
                CASE WHEN customer_inactive_period / customer_frequency <= 2 THEN 'Low'
                     WHEN customer_inactive_period / customer_frequency <= 5 THEN 'Medium'
                     ELSE 'High'
                END AS Customer_Churn_Segment

        FROM data_
        GROUP BY customer_id,
                 customer_average_check,
                 customer_frequency,
                 customer_inactive_period
    ),
    data_stores_ AS (
        SELECT DISTINCT cards.customer_id,
            transaction_store_id,
            count(transactions.transaction_store_id) OVER (PARTITION BY customer_id, transaction_store_id) AS num_visits,
            count(transactions.transaction_store_id) OVER (PARTITION BY customer_id) as all_visits,
            count(transactions.transaction_store_id) OVER (PARTITION BY customer_id, transaction_store_id)::numeric
            / count(transactions.transaction_store_id) OVER (PARTITION BY customer_id) AS favorite_store,
            NTH_VALUE(transaction_store_id, 1) OVER (PARTITION BY customer_id
                ORDER BY transaction_datetime DESC RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING)
                AS visit_store_1,
            NTH_VALUE(transaction_store_id, 2) OVER (PARTITION BY customer_id
                ORDER BY transaction_datetime DESC RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING)
                AS visit_store_2,
            NTH_VALUE(transaction_store_id, 3) OVER (PARTITION BY customer_id
                ORDER BY transaction_datetime DESC RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING)
                AS visit_store_3,
            NTH_VALUE(transaction_datetime, 1) OVER (PARTITION BY customer_id, transaction_store_id
                                                    ORDER BY transaction_datetime DESC) AS last_visit
        FROM transactions
        INNER JOIN cards ON cards.customer_card_id = transactions.customer_card_id
        ORDER BY customer_id, num_visits DESC
    ),
    customer_store_ AS (
        SELECT  DISTINCT customer_id,
            CASE WHEN (visit_store_1 = visit_store_2) AND (visit_store_1 = visit_store_3) THEN visit_store_1
                 ELSE NTH_VALUE(transaction_store_id, 1)
                                    OVER (PARTITION BY customer_id ORDER BY num_visits DESC, last_visit DESC)
            END AS Customer_Primary_Store
        FROM data_stores_
        ORDER BY customer_id
    )

    SELECT customer_data_.*,
           fnc_customer_segment(
               customer_average_check_segment,
               customer_frequency_segment,
               customer_churn_segment) AS Customer_Segment,
           customer_primary_store
    FROM customer_data_
    INNER JOIN customer_store_ ON customer_store_.customer_id = customer_data_.customer_id;

--- Checking ---
SELECT * FROM customers;



-- Creating groups_view
DROP TABLE IF EXISTS margin_type CASCADE;
CREATE TABLE IF NOT EXISTS margin_type (
    type_ varchar not null default 'default',
    value_ int not null default 0,
    CHECK (type_ IN ('default', 'days', 'transactions'))
);

CREATE OR REPLACE PROCEDURE set_margin_type(type varchar, value int default 0)
LANGUAGE plpgsql
AS $BODY$
    BEGIN
        TRUNCATE TABLE margin_type CASCADE;
        IF type = 'default' THEN
            INSERT INTO margin_type(type_, value_) VALUES (type, 0);
        ELSE
            INSERT INTO margin_type(type_, value_) VALUES (type, value);
        END IF;
    END
$BODY$;

-- Выбор метода расчета маржи:
-- 'default' по всем данным
-- 'days::n' за посленние n дней
-- 'transactions::n' по последним n транзакциям
CALL set_margin_type('default');
CALL set_margin_type('days', 700);
CALL set_margin_type('transactions', 100);
SELECT * FROM margin_type;


DROP FUNCTION IF EXISTS fnc_max_day();
CREATE OR REPLACE FUNCTION fnc_max_day() RETURNS numeric
AS $BODY$
    BEGIN
        RETURN (
            SELECT extract(epoch from current_date - min(transaction_datetime)) / 60 / 60 / 24
            FROM transactions
        );
    END;
$BODY$ LANGUAGE plpgsql ;

SELECT * FROM fnc_max_day();


---------------------------
DROP VIEW IF EXISTS groups;
CREATE OR REPLACE VIEW groups AS
    WITH
    affinity_data AS (
        SELECT customer_id, group_id, transactions.transaction_id, transaction_datetime
        FROM transactions
        INNER JOIN cards ON cards.customer_card_id = transactions.customer_card_id
        INNER JOIN checks ON checks.transaction_id = transactions.transaction_id
        INNER JOIN sku ON sku.sku_id = checks.sku_id
        ORDER BY customer_id, group_id
    ),
    affinity_data_periods AS(
        SELECT affinity_data.customer_id, affinity_data.group_id,
            First_Group_Purchase_Date,
            Last_Group_Purchase_Date,
            Group_Purchase

        FROM affinity_data
        INNER JOIN periods ON periods.customer_id = affinity_data.customer_id AND
                              periods.group_id = affinity_data.group_id
        GROUP BY affinity_data.customer_id,
                 affinity_data.group_id,
                 First_Group_Purchase_Date,
                 Last_Group_Purchase_Date,
                 Group_Purchase
        ORDER BY affinity_data.customer_id, affinity_data.group_id
    ),
    affinity AS(
        SELECT affinity_data_periods.customer_id, affinity_data_periods.group_id,
               count(DISTINCT transaction_id) AS num_transactions,
               Group_Purchase::numeric / count(DISTINCT transaction_id) AS Group_Affinity_Index

        FROM affinity_data_periods
        INNER JOIN affinity_data ON affinity_data.customer_id = affinity_data_periods.customer_id
                    and First_Group_Purchase_Date <= affinity_data.transaction_datetime
                    and Last_Group_Purchase_Date >= affinity_data.transaction_datetime
        GROUP BY affinity_data_periods.customer_id, affinity_data_periods.group_id, Group_Purchase
    )
    --TABLE affinity
    ,
    churn_rate_data AS (
        SELECT purchase_history.customer_id, purchase_history.group_id,
            max(transaction_datetime) AS last_transaction,
            extract(epoch from current_date - max(transaction_datetime)) / 60 / 60 / 24 AS days_after_last_transaction

        FROM purchase_history
        INNER JOIN periods ON periods.customer_id = purchase_history.customer_id AND
                              periods.group_id = purchase_history.group_id
        GROUP BY purchase_history.customer_id, purchase_history.group_id
        ORDER BY customer_id, group_id
    ),
    churn_rate AS (
        SELECT churn_rate_data.customer_id, churn_rate_data.group_id,
            days_after_last_transaction / Group_Frequency AS Group_Churn_Rate

        FROM churn_rate_data
        INNER JOIN periods ON periods.customer_id = churn_rate_data.customer_id AND
                              periods.group_id = churn_rate_data.group_id
    )
    --TABLE churn_rate
    ,
    stability_data AS (
        SELECT customer_id, group_id,
            transaction_id,
            transaction_datetime,
            abs(extract(epoch from transaction_datetime  -
                    lag(transaction_datetime, 1) OVER (PARTITION BY customer_id, group_id
                    ORDER BY customer_id, group_id, transaction_datetime)) / 60 / 60 / 24
                    - Group_Frequency) / Group_Frequency AS stability_index_
        FROM purchase_history
        INNER JOIN periods USING (customer_id, group_id)
        ORDER BY customer_id, group_id
    ),
    stability AS (
        SELECT customer_id, group_id,
               avg(stability_index_) AS Group_Stability_Index
        FROM stability_data
        GROUP BY customer_id, group_id
    )
    --TABLE stability
    ,
    margin_data AS (
        SELECT cards.customer_id,
            sku.group_id,
            sku.sku_id,
            transactions.transaction_id,
            transactions.transaction_datetime,
            stores.sku_purchase_price * checks.sku_amount AS sku_cost,
            checks.sku_summ_paid
        FROM transactions
        INNER JOIN cards ON cards.customer_card_id = transactions.customer_card_id
        INNER JOIN checks ON checks.transaction_id = transactions.transaction_id
        INNER JOIN sku ON sku.sku_id = checks.sku_id
        INNER JOIN stores ON stores.transaction_store_id = transactions.transaction_store_id
                         AND stores.sku_id = sku.sku_id
        ORDER BY cards.customer_id, group_id, transactions.transaction_datetime
    ),
    margin_data_filtered AS (
        SELECT *
        FROM margin_data
        WHERE extract(epoch from current_date - transaction_datetime) / 60 / 60 / 24 <=
              (SELECT CASE WHEN type_ = 'days' THEN value_
                           ELSE fnc_max_day()
                      END
              FROM margin_type)
        ORDER BY transaction_datetime DESC
        LIMIT (
                SELECT CASE WHEN type_ = 'transactions' THEN value_
                       END
                FROM margin_type
              )
    )
    --TABLE margin_data_filtered
    ,
    margin AS (
        SELECT DISTINCT customer_id, group_id,
            SUM(sku_summ_paid - sku_cost) OVER (PARTITION BY customer_id, group_id) AS Group_Margin
        FROM margin_data_filtered
        ORDER BY customer_id, group_id
    )
    --TABLE margin
    ,
    discount_data AS (
        SELECT cards.customer_id,
            sku.group_id,
            sku.sku_id,
            transactions.transaction_id,
            transactions.transaction_datetime,
            checks.sku_discount,
            CASE WHEN checks.sku_discount = 0 THEN 0
                 ELSE 1
            END AS with_discount,
            periods.group_purchase,
            periods.group_min_discount,
            purchase_history.group_summ,
            purchase_history.group_summ_paid
        FROM transactions
        INNER JOIN cards ON cards.customer_card_id = transactions.customer_card_id
        INNER JOIN checks ON checks.transaction_id = transactions.transaction_id
        INNER JOIN sku ON sku.sku_id = checks.sku_id
        INNER JOIN stores ON stores.transaction_store_id = transactions.transaction_store_id
                         AND stores.sku_id = sku.sku_id
        INNER JOIN periods ON periods.customer_id = cards.customer_id AND periods.group_id = sku.group_id
        INNER JOIN purchase_history ON purchase_history.customer_id = cards.customer_id
                                    AND purchase_history.group_id = sku.group_id
        ORDER BY cards.customer_id, group_id, transactions.transaction_datetime
    ),
    discount AS (
        SELECT DISTINCT customer_id, group_id,
            sum(with_discount)::numeric / group_purchase AS Group_Discount_Share,
            group_min_discount AS Group_Minimum_Discount,
            sum(group_summ_paid) OVER (PARTITION BY customer_id, group_id) /
            sum(group_summ) OVER (PARTITION BY customer_id, group_id) AS Group_Average_Discount

        FROM discount_data
        GROUP BY customer_id, group_id, group_purchase, group_min_discount, group_summ, group_summ_paid
        ORDER BY customer_id, group_id
    )
    --TABLE discount

    SELECT affinity.customer_id, affinity.group_id,
        affinity.Group_Affinity_Index,
        churn_rate.Group_Churn_Rate,
        stability.Group_Stability_Index,
        margin.Group_Margin,
        discount.Group_Discount_Share,
        discount.Group_Minimum_Discount,
        discount.Group_Average_Discount
    FROM affinity
    INNER JOIN churn_rate USING (customer_id, group_id)
    INNER JOIN stability USING (customer_id, group_id)
    INNER JOIN margin USING (customer_id, group_id)
    INNER JOIN discount USING (customer_id, group_id);

--- Checking ---
SELECT * FROM groups;


----------------------------
----------------------------
SELECT * FROM personal_data;
SELECT * FROM cards;
SELECT * FROM sku_groups;
SELECT * FROM sku;
SELECT * FROM stores;
SELECT * FROM transactions;
SELECT * FROM checks;

SELECT * FROM periods;
SELECT * FROM purchase_history;
SELECT * FROM customers;
SELECT * FROM groups;
