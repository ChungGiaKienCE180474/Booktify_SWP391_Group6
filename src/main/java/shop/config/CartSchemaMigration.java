package shop.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Ensures cart/order line tables support stationery (VPP) rows where book_id is null.
 * Hibernate ddl-auto=update does not always drop existing NOT NULL constraints.
 */
@Component
@Order(0)
public class CartSchemaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public CartSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        migrateCartItems();
        migrateOrderItems();
    }

    private void migrateCartItems() {
        ensureBookIdNullable("cart_items");
        ensureVppItemColumn("cart_items");

        jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                        SELECT 1 FROM pg_constraint WHERE conname = 'uk_cart_vpp'
                    ) THEN
                        ALTER TABLE cart_items
                            ADD CONSTRAINT uk_cart_vpp UNIQUE (cart_id, vpp_item_id);
                    END IF;
                END $$;
                """);
    }

    private void migrateOrderItems() {
        ensureBookIdNullable("order_items");
        ensureVppItemColumn("order_items");
    }

    private void ensureBookIdNullable(String tableName) {
        Boolean bookIdNullable = jdbcTemplate.queryForObject("""
                SELECT is_nullable = 'YES'
                FROM information_schema.columns
                WHERE table_schema = current_schema()
                  AND table_name = ?
                  AND column_name = 'book_id'
                """, Boolean.class, tableName);

        if (bookIdNullable == null || !bookIdNullable) {
            jdbcTemplate.execute(
                    "ALTER TABLE " + tableName + " ALTER COLUMN book_id DROP NOT NULL");
        }
    }

    private void ensureVppItemColumn(String tableName) {
        jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                        SELECT 1
                        FROM information_schema.columns
                        WHERE table_schema = current_schema()
                          AND table_name = '%s'
                          AND column_name = 'vpp_item_id'
                    ) THEN
                        ALTER TABLE %s ADD COLUMN vpp_item_id BIGINT;
                    END IF;
                END $$;
                """.formatted(tableName, tableName));
    }
}
