CREATE EXTENSION IF NOT EXISTS unaccent;

ALTER TABLE books ALTER COLUMN author DROP NOT NULL;

ALTER TABLE users ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE users ADD COLUMN IF NOT EXISTS staff_role VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS auth_provider VARCHAR(32) NOT NULL DEFAULT 'LOCAL';

-- Cart/order line items: book_id nullable for stationery (VPP) rows (see CartSchemaMigration)
ALTER TABLE cart_items ALTER COLUMN book_id DROP NOT NULL;
ALTER TABLE order_items ALTER COLUMN book_id DROP NOT NULL;


-- Rating: support review for both Book and Stationery(VPP)
ALTER TABLE ratings
ALTER COLUMN book_id DROP NOT NULL;