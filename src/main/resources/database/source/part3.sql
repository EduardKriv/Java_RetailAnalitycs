-- DROP OWNED BY Administrator;  -- Error on the first run, uncomment when re-running
-- DROP OWNED BY Visitor;

DROP ROLE IF EXISTS Administrator;
DROP ROLE IF EXISTS Visitor;

CREATE ROLE Administrator;
GRANT ALL ON DATABASE retail_analytics TO Administrator;
--GRANT ALL ON DATABASE lordik TO Administrator;
GRANT postgres TO Administrator;

-- GRANT neko TO Administrator; -- Use the SESSION_USER value here
SELECT SESSION_USER, CURRENT_USER;

CREATE ROLE Visitor;
GRANT SELECT ON TABLE personal_data, cards, sku_groups, sku, stores, transactions, checks TO Visitor;
GRANT pg_read_all_data TO Visitor;

SELECT rolname FROM pg_roles;
SELECT * FROM pg_roles;


-- Testing --
SET SESSION ROLE Administrator;
SET SESSION ROLE Visitor;
SET SESSION ROLE postgres;
RESET ROLE;


--------------------------------------------------------------------
INSERT INTO cards(customer_card_id, customer_id) VALUES (1101, 555);
DELETE FROM cards WHERE customer_card_id = 1101;


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
