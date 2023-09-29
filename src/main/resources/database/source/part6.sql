-- количество групп
-- максимальный индекс оттока
-- максимальный индекс стабильности потребления
-- максимальная доля SKU
-- допустимая доля маржи (в процентах)

DROP FUNCTION IF EXISTS fnc_personal_offers_cross_sales(int, numeric, numeric, numeric, numeric);
CREATE OR REPLACE FUNCTION fnc_personal_offers_cross_sales(
        number_groups int,
        max_churn_rate numeric,
        max_stability_index numeric,
        max_sku_share numeric,
        margin_share  numeric   -- in percent
    ) RETURNS TABLE (
        Customer_ID_ int,
        SKU_Name_ varchar,
        Offer_Discount_Depth_ numeric
    )
AS $BODY$
    BEGIN
        RETURN QUERY (
            WITH
            stores_data AS (
                SELECT
                    transaction_store_id AS store_id,
                    group_id,
                    sku_id,
                    sku_name,
                    sku_retail_price,
                    sku_purchase_price,
                    sku_retail_price - sku_purchase_price AS max_margin_sku
                FROM stores
                    JOIN sku USING (sku_id)
                ORDER BY transaction_store_id, group_id
            )
            --TABLE store_data
            ,
            filtered_stores_data AS (
                SELECT DISTINCT ON (store_id, group_id)
                    *
                FROM stores_data
                ORDER BY store_id, group_id, max_margin_sku DESC
            )
            --TABLE filtered_store_data
            ,
            checks_data AS (
                SELECT sku_id, group_id,
                    count(transaction_id)::numeric / by_groups.number_group_transacrions AS sku_share
                FROM checks
                INNER JOIN sku USING (sku_id)
                INNER JOIN (
                        SELECT group_id,
                            count(DISTINCT transaction_id) AS number_group_transacrions
                        FROM checks
                        INNER JOIN sku USING (sku_id)
                        GROUP BY group_id
                        ORDER BY group_id
                    ) by_groups USING (group_id)
                GROUP BY sku_id, group_id, by_groups.number_group_transacrions
                ORDER BY sku_id, group_id
            )
            --TABLE checks_data
            ,
            group_data AS (
                SELECT
                    ROW_NUMBER() OVER (PARTITION BY (customer_id) ORDER BY group_id) AS group_number,
                    *
                FROM groups
                WHERE group_churn_rate <= max_churn_rate
                      AND group_stability_index < max_stability_index
            )
            --TABLE group_data
            ,
            filtered_group_data AS (
                SELECT customer_id,
                    group_number,
                    group_data.group_id,
                    group_stability_index,
                    group_churn_rate,
                    customer_primary_store,
                    checks_data.sku_id,
                    sku_name,
                    group_minimum_discount,
                    ceil((group_minimum_discount * 100) * 0.2) * 5 AS group_minimum_discount_proc,
                    sku_retail_price,
                    sku_purchase_price,
                    max_margin_sku,
                    max_margin_sku / sku_retail_price * margin_share AS discount,
                    sku_share
                FROM group_data
                INNER JOIN customers USING (customer_id)
                INNER JOIN filtered_stores_data ON store_id = customer_primary_store AND
                                                    filtered_stores_data.group_id = group_data.group_id
                INNER JOIN checks_data ON checks_data.sku_id = filtered_stores_data.sku_id AND
                                                    checks_data.group_id = group_data.group_id
                WHERE group_number <= number_groups
                ORDER BY customer_id, group_number, group_data.group_id
            )
            SELECT customer_id,
                sku_name,
                group_minimum_discount_proc
            FROM filtered_group_data
            WHERE sku_share <= max_sku_share
                  AND group_minimum_discount_proc <= discount
                  AND group_minimum_discount_proc > 0
        );
    END
$BODY$ LANGUAGE plpgsql;

SELECT * FROM fnc_personal_offers_cross_sales(3,
    100,
    0.7,
    0.2,
    45);

SELECT * FROM fnc_personal_offers_cross_sales(5,
    200,
    0.8,
    0.3,
    55.5);


SELECT * FROM fnc_personal_offers_cross_sales(5,
    10,
    0.5,
    0.5,
    80);

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
