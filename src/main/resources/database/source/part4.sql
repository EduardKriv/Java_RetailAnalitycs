-- метод расчета среднего чека (1 - за период, 2 - за количество)
-- первая и последняя даты периода (для 1 метода)
-- количество транзакций (для 2 метода)
-- коэффициент увеличения среднего чека
-- максимальный индекс оттока
-- максимальная доля транзакций со скидкой
-- допустимая доля маржи (в процентах)

DROP FUNCTION IF EXISTS fnc_personal_offers_average_check(int, date, date, int, numeric, numeric, numeric, numeric);
CREATE OR REPLACE FUNCTION fnc_personal_offers_average_check(
        method int,
        first_date date,
        last_date date,
        num_transactions int,
        coefficient numeric,
        churn_rate numeric,
        discount_share numeric,
        margin_share numeric
    ) RETURNS TABLE (
        customer_id_ int,
        required_check_measure_ numeric,
        group_name_ varchar,
        offer_discount_depth_ numeric
    )
AS $BODY$
    DECLARE
        cursor_ CURSOR FOR (
            WITH
            transactions_data AS (
                SELECT customer_id, transaction_datetime, transaction_summ,
                    ROW_NUMBER() OVER(PARTITION BY customer_id ORDER BY transaction_datetime DESC) AS num
                FROM transactions
                INNER JOIN cards USING (customer_card_id)
                ORDER BY cards.customer_id
            ),
            filtered_transactions_data AS (
                SELECT  DISTINCT customer_id,
                    avg(transaction_summ) OVER (PARTITION BY customer_id) AS customer_average_check,
                    coefficient * avg(transaction_summ) OVER (PARTITION BY customer_id) AS target_customer_average_check
                FROM transactions_data
                WHERE
                    (method = 1 AND date_trunc('day', transaction_datetime) BETWEEN first_date AND last_date)
                    OR
                    (method = 2 AND  num <= num_transactions)
                ORDER BY customer_id
            )
            SELECT customer_id, group_id, group_name,
                target_customer_average_check,
                group_affinity_index,
                group_margin,
                group_margin * margin_share * 0.01 AS margin,
                group_minimum_discount,
                ceil((group_minimum_discount * 100) * 0.2) * 5 AS group_minimum_discount_proc,
                CASE
                    WHEN group_churn_rate <= churn_rate THEN true
                    ELSE false
                END AS churn_rate_suit,
                CASE
                    WHEN group_discount_share <= discount_share THEN true
                    ELSE false
                END AS discount_share_suit

            FROM filtered_transactions_data
            INNER JOIN groups USING (customer_id)
            INNER JOIN sku_groups USING (group_id)
            ORDER BY customer_id, group_affinity_index DESC
        );
        current_id int = 0;
    BEGIN
        FOR rec IN cursor_ LOOP
            IF rec.churn_rate_suit AND
               rec.discount_share_suit AND
               rec.group_minimum_discount_proc <= rec.margin AND
               rec.group_minimum_discount_proc > 0 AND
               rec.customer_id <> current_id
            THEN
                --RAISE NOTICE 'customer_id: %, groups: %, affinity: %, churn_rate_suit %, discount_share_suit %',
                --rec.customer_id, rec.group_id, rec.group_affinity_index, rec.churn_rate_suit, rec.discount_share_suit;
                customer_id_ = rec.customer_id;
                required_check_measure_ = rec.target_customer_average_check;
                group_name_ = rec.group_name;
                offer_discount_depth_ = rec.group_minimum_discount_proc;
                RETURN NEXT;
                current_id = rec.customer_id;
            END IF;
        END LOOP;
    END
$BODY$ LANGUAGE plpgsql;


SELECT *
from fnc_personal_offers_average_check(2, '2010-01-01', '2022-12-31', 100,
    1.2, 100, 1, 10);

SELECT *
from fnc_personal_offers_average_check(1, '2020-01-01', '2022-12-31', 100,
    1.3, 200, 1, 20);


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
