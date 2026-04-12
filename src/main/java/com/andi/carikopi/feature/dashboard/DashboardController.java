package com.andi.carikopi.feature.dashboard;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.dashboard.dto.DashboardResponse;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/{shopId}")
    public WebResponse<DashboardResponse> getDashboardData(
            @PathVariable UUID shopId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        WebResponse<DashboardResponse> response = dashboardService.getDashboard(shopId, startDate, endDate);
        return response;
    }
}
