package shop.controller.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.service.OrderService;
import shop.service.VnPayService;

@Controller
@RequestMapping("/payment/vnpay")
public class VnPayController {

    private final VnPayService vnPayService;
    private final OrderService orderService;

    public VnPayController(
            VnPayService vnPayService,
            OrderService orderService) {

        this.vnPayService = vnPayService;
        this.orderService = orderService;
    }

    @GetMapping("/return")
    public String handleReturn(
            @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes) {

        Long orderId = vnPayService.extractOrderId(params);

        if (orderId == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Invalid VNPay response."
            );
            return "redirect:/orders";
        }

        if (!vnPayService.verifyPaymentParams(params)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "VNPay payment verification failed."
            );
            return "redirect:/orders/" + orderId;
        }

        if (vnPayService.isPaymentSuccessful(params)) {
            try {
                orderService.confirmVnPayPayment(
                        orderId,
                        vnPayService.extractPaidAmount(params)
                );

                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "VNPay payment successful! Your order has been confirmed."
                );
            } catch (IllegalArgumentException exception) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        exception.getMessage()
                );
            }
        } else {
            try {
                if (orderService.cancelPendingVnPayOrder(orderId)) {
                    redirectAttributes.addFlashAttribute(
                            "successMessage",
                            "VNPay payment was cancelled. Your order has been cancelled."
                    );
                } else {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage",
                            "VNPay payment was not completed."
                    );
                }
            } catch (IllegalArgumentException exception) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        exception.getMessage()
                );
            }
        }

        return "redirect:/orders/" + orderId;
    }

    @GetMapping("/ipn")
    public ResponseEntity<Map<String, String>> handleIpn(
            @RequestParam Map<String, String> params) {

        Map<String, String> response = new HashMap<>();

        Long orderId = vnPayService.extractOrderId(params);
        if (orderId == null) {
            response.put("RspCode", "01");
            response.put("Message", "Invalid order reference");
            return ResponseEntity.ok(response);
        }

        if (!vnPayService.verifyPaymentParams(params)) {
            response.put("RspCode", "97");
            response.put("Message", "Invalid signature");
            return ResponseEntity.ok(response);
        }

        if (!vnPayService.isPaymentSuccessful(params)) {
            orderService.cancelPendingVnPayOrder(orderId);
            response.put("RspCode", "02");
            response.put("Message", "Payment failed");
            return ResponseEntity.ok(response);
        }

        try {
            BigDecimal paidAmount = vnPayService.extractPaidAmount(params);
            orderService.confirmVnPayPayment(orderId, paidAmount);
            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");
        } catch (IllegalArgumentException exception) {
            response.put("RspCode", "04");
            response.put("Message", exception.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
