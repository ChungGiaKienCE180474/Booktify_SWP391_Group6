package shop.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.StockMovementType;
import shop.domain.StockProductType;
import shop.domain.User;
import shop.domain.dto.StockItemDTO;
import shop.domain.dto.StockTransactionDTO;
import shop.service.StockService;
import shop.service.UserService;

@Controller
@RequestMapping("/admin/stock")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStockController {

    private static final int INVENTORY_PAGE_SIZE = 10;
    private static final int HISTORY_PAGE_SIZE = 15;

    private final StockService stockService;
    private final UserService userService;

    public AdminStockController(
            StockService stockService,
            UserService userService
    ) {
        this.stockService = stockService;
        this.userService = userService;
    }

    /*
     * =========================================================
     * STOCK MANAGEMENT PAGE
     * URL: GET /admin/stock
     * =========================================================
     */

    @GetMapping
    public String inventory(
            @RequestParam(
                    value = "q",
                    required = false
            )
            String keyword,

            @RequestParam(
                    value = "productType",
                    defaultValue = "ALL"
            )
            String productType,

            @RequestParam(
                    value = "stockStatus",
                    defaultValue = "ALL"
            )
            String stockStatus,

            @RequestParam(
                    value = "supplier",
                    defaultValue = "ALL"
            )
            String supplier,

            @RequestParam(
                    value = "category",
                    defaultValue = "ALL"
            )
            String category,

            @RequestParam(
                    value = "page",
                    defaultValue = "0"
            )
            int page,

            Model model
    ) {
        int currentPage = Math.max(page, 0);

        Page<StockItemDTO> inventoryPage =
                stockService.searchInventory(
                        keyword,
                        productType,
                        stockStatus,
                        supplier,
                        category,
                        PageRequest.of(
                                currentPage,
                                INVENTORY_PAGE_SIZE
                        )
                );

        /*
         * Nếu URL chứa page lớn hơn tổng số trang,
         * tự động quay về trang cuối cùng.
         */
        if (inventoryPage.getTotalPages() > 0
                && currentPage
                >= inventoryPage.getTotalPages()) {

            currentPage =
                    inventoryPage.getTotalPages() - 1;

            inventoryPage =
                    stockService.searchInventory(
                            keyword,
                            productType,
                            stockStatus,
                            supplier,
                            category,
                            PageRequest.of(
                                    currentPage,
                                    INVENTORY_PAGE_SIZE
                            )
                    );
        }

        long totalItems =
                inventoryPage.getTotalElements();

        long fromItem =
                totalItems == 0
                        ? 0
                        : (long) currentPage
                        * INVENTORY_PAGE_SIZE + 1;

        long toItem =
                Math.min(
                        (long) (currentPage + 1)
                                * INVENTORY_PAGE_SIZE,
                        totalItems
                );

        /*
         * Dữ liệu thống kê.
         */
        model.addAttribute(
                "summary",
                stockService.getSummary()
        );

        /*
         * Danh sách sản phẩm tồn kho của trang hiện tại.
         */
        model.addAttribute(
                "items",
                inventoryPage.getContent()
        );

        /*
         * Dữ liệu cho các dropdown filter.
         */
        model.addAttribute(
                "supplierOptions",
                stockService.getSupplierOptions()
        );

        model.addAttribute(
                "categoryOptions",
                stockService.getCategoryOptions()
        );

        model.addAttribute(
                "productTypes",
                StockProductType.values()
        );

        model.addAttribute(
                "lowStockThreshold",
                StockService.LOW_STOCK_THRESHOLD
        );

        /*
         * Giữ lại giá trị filter sau khi submit.
         */
        model.addAttribute(
                "q",
                keyword
        );

        model.addAttribute(
                "selectedProductType",
                productType
        );

        model.addAttribute(
                "selectedStockStatus",
                stockStatus
        );

        model.addAttribute(
                "selectedSupplier",
                supplier
        );

        model.addAttribute(
                "selectedCategory",
                category
        );

        /*
         * Dữ liệu phân trang.
         */
        model.addAttribute(
                "currentPage",
                currentPage
        );

        model.addAttribute(
                "totalPages",
                inventoryPage.getTotalPages()
        );

        model.addAttribute(
                "totalItems",
                totalItems
        );

        model.addAttribute(
                "fromItem",
                fromItem
        );

        model.addAttribute(
                "toItem",
                toItem
        );

        return "admin/stock/index";
    }

    /*
     * =========================================================
     * STOCK TRANSACTION HISTORY
     * URL: GET /admin/stock/history
     * =========================================================
     */

    @GetMapping("/history")
    public String history(
            @RequestParam(
                    value = "q",
                    required = false
            )
            String keyword,

            @RequestParam(
                    value = "productType",
                    defaultValue = "ALL"
            )
            String productType,

            @RequestParam(
                    value = "productId",
                    required = false
            )
            Long productId,

            @RequestParam(
                    value = "movementType",
                    defaultValue = "ALL"
            )
            String movementType,

            @RequestParam(
                    value = "page",
                    defaultValue = "0"
            )
            int page,

            Model model,
            RedirectAttributes redirectAttributes
    ) {
        int currentPage = Math.max(page, 0);

        try {
            Page<StockTransactionDTO> transactionPage =
                    stockService.searchTransactions(
                            productType,
                            productId,
                            movementType,
                            keyword,
                            PageRequest.of(
                                    currentPage,
                                    HISTORY_PAGE_SIZE
                            )
                    );

            /*
             * Nếu trang yêu cầu vượt quá số trang hiện có,
             * tự động lấy trang cuối.
             */
            if (transactionPage.getTotalPages() > 0
                    && currentPage
                    >= transactionPage.getTotalPages()) {

                currentPage =
                        transactionPage.getTotalPages() - 1;

                transactionPage =
                        stockService.searchTransactions(
                                productType,
                                productId,
                                movementType,
                                keyword,
                                PageRequest.of(
                                        currentPage,
                                        HISTORY_PAGE_SIZE
                                )
                        );
            }

            long totalItems =
                    transactionPage.getTotalElements();

            long fromItem =
                    totalItems == 0
                            ? 0
                            : (long) currentPage
                            * HISTORY_PAGE_SIZE + 1;

            long toItem =
                    Math.min(
                            (long) (currentPage + 1)
                                    * HISTORY_PAGE_SIZE,
                            totalItems
                    );

            model.addAttribute(
                    "transactions",
                    transactionPage.getContent()
            );

            model.addAttribute(
                    "productTypes",
                    StockProductType.values()
            );

            model.addAttribute(
                    "movementTypes",
                    StockMovementType.values()
            );

            model.addAttribute(
                    "todayMovements",
                    stockService.getTodayMovementCount()
            );

            model.addAttribute(
                    "totalAdjustments",
                    stockService.getAdjustmentCount()
            );

            model.addAttribute(
                    "totalTransactions",
                    totalItems
            );

            /*
             * Giữ lại filter.
             */
            model.addAttribute(
                    "q",
                    keyword
            );

            model.addAttribute(
                    "selectedProductType",
                    productType
            );

            model.addAttribute(
                    "selectedProductId",
                    productId
            );

            model.addAttribute(
                    "selectedMovementType",
                    movementType
            );

            /*
             * Phân trang.
             */
            model.addAttribute(
                    "currentPage",
                    currentPage
            );

            model.addAttribute(
                    "totalPages",
                    transactionPage.getTotalPages()
            );

            model.addAttribute(
                    "totalItems",
                    totalItems
            );

            model.addAttribute(
                    "fromItem",
                    fromItem
            );

            model.addAttribute(
                    "toItem",
                    toItem
            );

            return "admin/stock/history";

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            return "redirect:/admin/stock/history";
        }
    }

    /*
     * =========================================================
     * STOCK IN
     * URL: POST /admin/stock/in
     * =========================================================
     */

    @PostMapping("/in")
    public String stockIn(
            @RequestParam("productType")
            String productType,

            @RequestParam("productId")
            Long productId,

            @RequestParam("quantity")
            int quantity,

            @RequestParam(
                    value = "referenceCode",
                    required = false
            )
            String referenceCode,

            @RequestParam(
                    value = "note",
                    required = false
            )
            String note,

            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User currentUser =
                    resolveCurrentUser(authentication);

            stockService.stockIn(
                    productType,
                    productId,
                    quantity,
                    referenceCode,
                    note,
                    currentUser
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Stock received successfully."
            );

        } catch (
                IllegalArgumentException
                | ArithmeticException exception
        ) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/admin/stock";
    }

    /*
     * =========================================================
     * STOCK OUT
     * URL: POST /admin/stock/out
     * =========================================================
     */

    @PostMapping("/out")
    public String stockOut(
            @RequestParam("productType")
            String productType,

            @RequestParam("productId")
            Long productId,

            @RequestParam("quantity")
            int quantity,

            @RequestParam("reason")
            String reason,

            @RequestParam(
                    value = "note",
                    required = false
            )
            String note,

            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User currentUser =
                    resolveCurrentUser(authentication);

            stockService.stockOut(
                    productType,
                    productId,
                    quantity,
                    reason,
                    note,
                    currentUser
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Stock removed successfully."
            );

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/admin/stock";
    }

    /*
     * =========================================================
     * ADJUST STOCK
     * URL: POST /admin/stock/adjust
     * =========================================================
     */

    @PostMapping("/adjust")
    public String adjustStock(
            @RequestParam("productType")
            String productType,

            @RequestParam("productId")
            Long productId,

            @RequestParam("actualStock")
            int actualStock,

            @RequestParam("reason")
            String reason,

            @RequestParam(
                    value = "note",
                    required = false
            )
            String note,

            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User currentUser =
                    resolveCurrentUser(authentication);

            stockService.adjustStock(
                    productType,
                    productId,
                    actualStock,
                    reason,
                    note,
                    currentUser
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Stock adjusted successfully."
            );

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/admin/stock";
    }

    /*
     * =========================================================
     * CURRENT ADMIN USER
     * =========================================================
     */

    private User resolveCurrentUser(
            Authentication authentication
    ) {
        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Authenticated administrator is required."
            );
        }

        User user =
                userService.getUserByEmail(
                        authentication.getName()
                );

        if (user == null) {
            throw new IllegalStateException(
                    "Administrator account was not found."
            );
        }

        return user;
    }
}