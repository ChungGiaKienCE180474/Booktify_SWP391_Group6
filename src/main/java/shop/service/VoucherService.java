package shop.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import shop.domain.Voucher;
import shop.domain.dto.VoucherDTO;
import shop.repository.VoucherRepository;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

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

                voucher.setVoucherName(
                                voucherDTO.getVoucherName().trim());

                voucher.setVoucherCode(
                                voucherDTO.getVoucherCode().trim());

                voucher.setDiscountType(voucherDTO.getDiscountType());

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
        // GET ACTIVE VOUCHERS FOR CUSTOMER CART
        // =====================

        public List<Voucher> getActiveVouchers() {
                LocalDate today = LocalDate.now();
                return voucherRepository.findAll()
                                .stream()
                                .filter(voucher -> {
                                        // chưa tới ngày bắt đầu
                                        if (voucher.getStartDate() != null
                                                        && today.isBefore(voucher.getStartDate())) {
                                                return false;
                                        }
                                        // đã hết hạn
                                        if (voucher.getEndDate() != null
                                                        && today.isAfter(voucher.getEndDate())) {
                                                return false;
                                        }
                                        // hết số lượng
                                        if (voucher.getQuantity() != null
                                                        && voucher.getQuantity() <= 0) {
                                                return false;
                                        }
                                        return true;
                                })
                                .collect(Collectors.toList());
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

                voucher.setVoucherName(voucherDTO.getVoucherName().trim());
                voucher.setVoucherCode(voucherDTO.getVoucherCode().trim());
                voucher.setDiscountType(voucherDTO.getDiscountType());
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

                if (voucher.getEndDate() != null
                                && today.isAfter(voucher.getEndDate())) {
                        return "EXPIRED";
                }
                if (voucher.getStartDate() != null
                                && today.isBefore(voucher.getStartDate())) {
                        return "UPCOMING";
                }
                if (voucher.getQuantity() != null
                                && voucher.getQuantity() <= 0) {
                        return "INACTIVE";
                }
                return "ACTIVE";
        }

        public Voucher findValidVoucher(String voucherCode) {
                Voucher voucher = voucherRepository
                                .findByVoucherCodeIgnoreCase(voucherCode.trim())
                                .orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "Voucher không tồn tại."));

                LocalDate today = LocalDate.now();
                if (voucher.getStartDate() != null
                                && today.isBefore(voucher.getStartDate())) {
                        throw new IllegalArgumentException(
                                        "Voucher chưa bắt đầu.");
                }
                if (voucher.getEndDate() != null
                                && today.isAfter(voucher.getEndDate())) {

                        throw new IllegalArgumentException(
                                        "Voucher đã hết hạn.");
                }
                if (voucher.getQuantity() <= 0) {
                        throw new IllegalArgumentException(
                                        "Voucher đã hết lượt sử dụng.");
                }
                return voucher;
        }

        public BigDecimal calculateDiscount(
                        String voucherCode,
                        BigDecimal subtotal) {

                Voucher voucher = findValidVoucher(voucherCode);

                if (voucher.getMinOrderAmount() != null
                                && subtotal.compareTo(voucher.getMinOrderAmount()) < 0) {

                        throw new IllegalArgumentException(
                                        "Đơn hàng chưa đạt giá trị tối thiểu để dùng voucher.");
                }

                BigDecimal discount = BigDecimal.ZERO;

                switch (voucher.getDiscountType()) {

                        case "PERCENT":

                                discount = subtotal
                                                .multiply(voucher.getDiscountValue())
                                                .divide(
                                                                BigDecimal.valueOf(100),
                                                                2,
                                                                RoundingMode.HALF_UP);

                                if (discount.compareTo(subtotal) > 0) {
                                        discount = subtotal;
                                }

                                break;

                        case "FIXED":

                                discount = voucher.getDiscountValue();

                                if (discount.compareTo(subtotal) > 0) {
                                        discount = subtotal;
                                }

                                break;

                        default:
                                throw new IllegalArgumentException(
                                                "Loại voucher không hợp lệ.");
                }
                return discount;
        }

        @Transactional
        public void decreaseVoucherQuantity(String voucherCode) {

                Voucher voucher = findValidVoucher(voucherCode);

                if (voucher.getQuantity() <= 0) {
                        throw new IllegalArgumentException(
                                        "Voucher đã hết lượt sử dụng.");
                }

                voucher.setQuantity(
                                voucher.getQuantity() - 1);

                voucherRepository.save(voucher);
        }
}