package shop.controller.admin;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.domain.dto.AdminDashboardDTO;
import shop.service.AdminDashboardService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(
            AdminDashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    /*
     * ============================================================
     * ADMIN DASHBOARD
     * ============================================================
     *
     * URL examples:
     *
     * /admin
     *      → Uses today's date.
     *
     * /admin?date=2026-07-25
     *      → Uses July 25, 2026.
     */

    @GetMapping
    public String dashboard(
            @RequestParam(
                    value = "date",
                    required = false
            )
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate selectedDate,

            Model model
    ) {
        AdminDashboardDTO dashboard =
                dashboardService.getDashboard(
                        selectedDate
                );

        model.addAttribute(
                "dashboard",
                dashboard
        );

        return "admin/dashboard/index";
    }
}