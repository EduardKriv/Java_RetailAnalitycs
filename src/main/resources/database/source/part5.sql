-- первая и последняя даты периода
-- добавляемое число транзакций
-- максимальный индекс оттока
-- максимальная доля транзакций со скидкой
-- допустимая доля маржи (в процентах)

DROP FUNCTION IF EXISTS fnc_personal_offers_visits_frequency(date, date, integer, numeric, numeric, numeric);
CREATE OR REPLACE FUNCTION fnc_personal_offers_visits_frequency(
        first_date date,
        last_date date,
        added_number_transactions int,
        max_churn_rate numeric,
        max_discount_share  numeric,
        margin_share  numeric   -- in percent
    ) RETURNS TABLE (
        Customer_ID_ int,
        Start_Date_ timestamp,
        End_Date_ timestamp,
        Required_Transactions_Count_ numeric,
        Group_Name_ varchar,
        Offer_Discount_Depth_ numeric
    )
AS $BODY$
    BEGIN
        RETURN QUERY (
            WITH
            transactions_data AS (
                SELECT customer_id, group_id,
                    group_affinity_index,
                    group_churn_rate,
                    group_discount_share
                    , round((last_date - first_date) / customer_frequency) + added_number_transactions
                            AS Required_Transactions_Count
                    , group_margin
                    , group_margin * margin_share * 0.01 AS margin
                    , group_minimum_discount
                    , ceil((group_minimum_discount * 100) * 0.2) * 5 AS group_minimum_discount_proc
                FROM transactions
                INNER JOIN cards USING (customer_card_id)
                INNER JOIN customers USING (customer_id)
                INNER JOIN groups USING (customer_id)
                WHERE date_trunc('day', transaction_datetime) BETWEEN first_date AND last_date AND
                      group_churn_rate <= max_churn_rate AND       --
                      group_discount_share <= max_discount_share
                ORDER BY customer_id, group_affinity_index DESC, group_id
            ),
            filtered_transactions_data AS (
                SELECT DISTINCT ON (transactions_data.customer_id)
                    *
                FROM transactions_data
                INNER JOIN sku_groups USING (group_id)
                WHERE
                    group_minimum_discount_proc <= margin AND
                    group_minimum_discount_proc > 0
            )
            SELECT
                customer_id AS Customer_ID,
                first_date::timestamp AS Start_Date,
                last_date::timestamp AS End_Date,
                required_transactions_count AS Required_Transactions_Count,
                group_name AS Group_Name,
                group_minimum_discount_proc AS Offer_Discount_Depth
            FROM filtered_transactions_data
        );
    END
$BODY$ LANGUAGE plpgsql;

SHOW datestyle;
SET datestyle TO DMY;
SHOW datestyle;

SELECT * FROM fnc_personal_offers_visits_frequency('01-01-2020', '14-09-2022',
    10,
    200,
    1,
    10);

SELECT * FROM fnc_personal_offers_visits_frequency('01-01-2021', '01-12-2022',
    5,
    100,
    2,
    20);

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
