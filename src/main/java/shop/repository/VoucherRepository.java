package shop.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.domain.Voucher;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    boolean existsByVoucherCodeIgnoreCase(String voucherCode);

    boolean existsByVoucherCodeIgnoreCaseAndVoucherIdNot(
            String voucherCode,
            Long voucherId);

    List<Voucher> findAllByOrderByVoucherIdDesc();

    Optional<Voucher> findByVoucherCodeIgnoreCase(String voucherCode);
}