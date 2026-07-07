package shop.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import shop.domain.Voucher;
import shop.domain.dto.VoucherDTO;
import shop.repository.VoucherRepository;
import java.util.Optional;

@Service
public class VoucherService {

    // =====================
    // DEPENDENCY
    // =====================

    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    // =====================
    // CREATE VOUCHER
    // =====================

    public void saveVoucher(VoucherDTO voucherDTO) {

        Voucher voucher = new Voucher();

        voucher.setVoucherCode(
                voucherDTO.getVoucherCode().trim());

        voucher.setDiscountType("PERCENT");

        voucher.setDiscountValue(
                voucherDTO.getDiscountValue());

        voucher.setMinOrderAmount(
                voucherDTO.getMinOrderAmount());

        voucher.setMaxOrderAmount(
                voucherDTO.getMaxOrderAmount());

        voucher.setQuantity(
                voucherDTO.getQuantity());

        voucher.setStartDate(
                voucherDTO.getStartDate());

        voucher.setEndDate(
                voucherDTO.getEndDate());

        voucher.setDescription(
                voucherDTO.getDescription() == null
                        ? null
                        : voucherDTO.getDescription().trim());

        voucherRepository.save(voucher);
    }

    // =====================
    // GET ALL VOUCHER
    // =====================

    public List<Voucher> getAllVouchers() {

        List<Voucher> vouchers = voucherRepository.findAllByOrderByVoucherIdDesc();

        for (Voucher voucher : vouchers) {

            voucher.setStatus(
                    calculateStatus(voucher));

        }

        return vouchers;

    }

    // =====================
    // CHECK DUPLICATE CODE
    // =====================

    public boolean existsByVoucherCodeIgnoreCase(String voucherCode) {

        return voucherRepository.existsByVoucherCodeIgnoreCase(
                voucherCode.trim());

    }

    public Voucher getVoucherById(Long id) {

        return voucherRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Voucher not found"));
    }

    // UPDATE VOUCHER
    public void updateVoucher(Long id, VoucherDTO voucherDTO) {

        Voucher voucher = getVoucherById(id);

        voucher.setVoucherCode(voucherDTO.getVoucherCode().trim());
        voucher.setDiscountValue(voucherDTO.getDiscountValue());
        voucher.setMinOrderAmount(voucherDTO.getMinOrderAmount());
        voucher.setMaxOrderAmount(voucherDTO.getMaxOrderAmount());
        voucher.setQuantity(voucherDTO.getQuantity());
        voucher.setStartDate(voucherDTO.getStartDate());
        voucher.setEndDate(voucherDTO.getEndDate());

        voucher.setDescription(
                voucherDTO.getDescription() == null
                        ? null
                        : voucherDTO.getDescription().trim());

        voucherRepository.save(voucher);
    }

    public boolean existsByVoucherCodeIgnoreCaseAndVoucherIdNot(
            String voucherCode,
            Long voucherId) {

        return voucherRepository
                .existsByVoucherCodeIgnoreCaseAndVoucherIdNot(
                        voucherCode.trim(),
                        voucherId);

    }

    private String calculateStatus(Voucher voucher) {

        LocalDate today = LocalDate.now();
        
        // Hết hạn theo ngày
        if (today.isAfter(voucher.getEndDate())) {
            return "EXPIRED";
        }

        // Chưa tới ngày chạy
        if (today.isBefore(voucher.getStartDate())) {
            return "UPCOMING";
        }

        // Hết số lượng
        if (voucher.getQuantity() <= 0) {
            return "INACTIVE";
        }

        return "ACTIVE";
    }

}