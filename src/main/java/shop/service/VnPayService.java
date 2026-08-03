package shop.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import shop.config.VnPayConfig;
import shop.domain.Order;

@Service
public class VnPayService {

    private static final DateTimeFormatter VNPAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final VnPayConfig vnPayConfig;

    public VnPayService(VnPayConfig vnPayConfig) {
        this.vnPayConfig = vnPayConfig;
    }

    public boolean isConfigured() {
        return vnPayConfig.isConfigured();
    }

    public String createPaymentUrl(Order order, String ipAddress) {
        if (!isConfigured()) {
            throw new IllegalStateException(
                    "VNPay is not configured. Please set vnpay.tmn-code and vnpay.hash-secret."
            );
        }

        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("Order is required.");
        }

        long amount = order.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        LocalDateTime now = LocalDateTime.now(
                java.time.ZoneId.of("Asia/Ho_Chi_Minh")
        );

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", vnPayConfig.getVersion());
        params.put("vnp_Command", vnPayConfig.getCommand());
        params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        params.put("vnp_Amount", String.valueOf(amount));
        params.put("vnp_CurrCode", vnPayConfig.getCurrCode());
        params.put("vnp_TxnRef", String.valueOf(order.getId()));
        params.put(
                "vnp_OrderInfo",
                "Thanh toan don hang " + order.getOrderCode()
        );
        params.put("vnp_OrderType", vnPayConfig.getOrderType());
        params.put("vnp_Locale", vnPayConfig.getLocale());
        params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        params.put("vnp_IpAddr", normalizeIpAddress(ipAddress));
        params.put("vnp_CreateDate", now.format(VNPAY_DATE_FORMAT));
        params.put(
                "vnp_ExpireDate",
                now.plusMinutes(15).format(VNPAY_DATE_FORMAT)
        );

        String signData = buildSignData(params);
        String secureHash = hmacSha512(vnPayConfig.getHashSecret(), signData);

        return vnPayConfig.getPayUrl()
                + "?"
                + signData
                + "&vnp_SecureHash="
                + secureHash;
    }

    public boolean verifyPaymentParams(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return false;
        }

        String receivedHash = params.get("vnp_SecureHash");
        if (receivedHash == null || receivedHash.isBlank()) {
            return false;
        }

        Map<String, String> copy = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("vnp_")
                    && !"vnp_SecureHash".equals(key)
                    && !"vnp_SecureHashType".equals(key)) {
                copy.put(key, entry.getValue());
            }
        }

        String calculatedHash = hmacSha512(
                vnPayConfig.getHashSecret(),
                buildSignData(copy)
        );

        return calculatedHash.equalsIgnoreCase(receivedHash);
    }

    public boolean isPaymentSuccessful(Map<String, String> params) {
        return "00".equals(params.get("vnp_ResponseCode"));
    }

    public Long extractOrderId(Map<String, String> params) {
        String txnRef = params.get("vnp_TxnRef");
        if (txnRef == null || txnRef.isBlank()) {
            return null;
        }

        try {
            return Long.parseLong(txnRef.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public BigDecimal extractPaidAmount(Map<String, String> params) {
        String amount = params.get("vnp_Amount");
        if (amount == null || amount.isBlank()) {
            return null;
        }

        try {
            return BigDecimal.valueOf(Long.parseLong(amount.trim()))
                    .divide(BigDecimal.valueOf(100));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String buildSignData(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        List<String> parts = new ArrayList<>();
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                parts.add(fieldName + "=" + urlEncode(fieldValue));
            }
        }

        return String.join("&", parts);
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }

    private String normalizeIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return "127.0.0.1";
        }

        if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
            return "127.0.0.1";
        }

        if (ipAddress.contains(",")) {
            return ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }

    private String hmacSha512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            );
            mac.init(secretKey);
            byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder(2 * result.length);
            for (byte value : result) {
                builder.append(String.format("%02x", value & 0xff));
            }
            return builder.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign VNPay request.", ex);
        }
    }
}
