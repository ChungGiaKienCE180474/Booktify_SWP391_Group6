package shop.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Ensures cart/order line tables support nullable book_id and book_set_id.
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
        migrateBookSetTags();
        migrateOrderPaymentStatus();
    }

    /** Cột theo dõi thanh toán COD/VNPay: payment_status + paid_at. */
    private void migrateOrderPaymentStatus() {
        ensureColumn("orders", "payment_status", "VARCHAR(30) DEFAULT 'UNPAID' NOT NULL");
        ensureColumn("orders", "paid_at", "TIMESTAMP");
        try {
            jdbcTemplate.execute(
                    "UPDATE orders SET payment_status = 'UNPAID' WHERE payment_status IS NULL");
        } catch (Exception ignored) {
            // Bảng có thể chưa tồn tại ở lần boot đầu — Hibernate ddl-auto sẽ tạo.
        }
    }

    /** Allow free-text set tags (comics, series…), not only short grade codes. */
    private void migrateBookSetTags() {
        try {
            jdbcTemplate.execute("""
                    DO $$
                    BEGIN
                        IF EXISTS (
                            SELECT 1
                            FROM information_schema.columns
                            WHERE table_schema = current_schema()
                              AND table_name = 'book_sets'
                              AND column_name = 'grade_level'
                        ) THEN
                            ALTER TABLE book_sets
                                ALTER COLUMN grade_level TYPE VARCHAR(100);
                        END IF;
                    END $$;
                    """);
        } catch (Exception ignored) {
            // Table may not exist yet on first boot; Hibernate ddl-auto will create it.
        }
    }

    private void migrateCartItems() {
        ensureBookIdNullable("cart_items");
        ensureColumn("cart_items", "vpp_item_id", "BIGINT");
        ensureColumn("cart_items", "book_set_id", "BIGINT");

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

        jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                        SELECT 1 FROM pg_constraint WHERE conname = 'uk_cart_book_set'
                    ) THEN
                        ALTER TABLE cart_items
                            ADD CONSTRAINT uk_cart_book_set UNIQUE (cart_id, book_set_id);
                    END IF;
                END $$;
                """);
    }

    private void migrateOrderItems() {
        ensureBookIdNullable("order_items");
        ensureColumn("order_items", "vpp_item_id", "BIGINT");
        ensureColumn("order_items", "book_set_id", "BIGINT");
        ensureColumn("order_items", "book_set_name", "VARCHAR(200)");
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

    private void ensureColumn(String tableName, String columnName, String sqlType) {
        jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                        SELECT 1
                        FROM information_schema.columns
                        WHERE table_schema = current_schema()
                          AND table_name = '%s'
                          AND column_name = '%s'
                    ) THEN
                        ALTER TABLE %s ADD COLUMN %s %s;
                    END IF;
                END $$;
                """.formatted(tableName, columnName, tableName, columnName, sqlType));
    }
}
