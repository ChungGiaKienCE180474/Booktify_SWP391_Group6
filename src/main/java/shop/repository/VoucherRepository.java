package shop.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.domain.Voucher;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    // =====================
    // CHECK DUPLICATE CODE
    // =====================

    boolean existsByVoucherCodeIgnoreCase(String voucherCode);

    // =====================
    // LIST ORDER BY NEWEST
    // =====================

    List<Voucher> findAllByOrderByVoucherIdDesc();

}