package shop.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import shop.domain.Voucher;
import shop.domain.dto.VoucherDTO;
import shop.repository.VoucherRepository;

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

        voucher.setStatus("ACTIVE");

        voucher.setCreatedAt(
                LocalDateTime.now());

        voucher.setUpdatedAt(
                LocalDateTime.now());

        voucherRepository.save(voucher);
    }

    // =====================
    // GET ALL VOUCHER
    // =====================

    public List<Voucher> getAllVouchers() {

        return voucherRepository.findAllByOrderByVoucherIdDesc();

    }

    // =====================
    // CHECK DUPLICATE CODE
    // =====================

    public boolean existsByVoucherCodeIgnoreCase(String voucherCode) {

        return voucherRepository.existsByVoucherCodeIgnoreCase(
                voucherCode.trim());

    }

}