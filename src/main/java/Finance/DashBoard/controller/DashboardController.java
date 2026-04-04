package Finance.DashBoard.controller;

import Finance.DashBoard.dto.FinancialSummaryDto;
import Finance.DashBoard.dto.PeriodTrendDto;
import Finance.DashBoard.dto.RecentRecordDto;
import Finance.DashBoard.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    public FinancialSummaryDto summary() {
        return dashboardService.getFinancialSummary();
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    public List<RecentRecordDto> recent(@RequestParam(defaultValue = "10") int limit) {
        return dashboardService.getRecentActivity(limit);
    }

    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    public List<PeriodTrendDto> trends(@RequestParam(defaultValue = "MONTH") String granularity) {
        return dashboardService.getTrends(granularity);
    }
}
