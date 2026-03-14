package org.example.meetingapp.controller;

import org.example.meetingapp.entity.MeetingStatus;
import org.example.meetingapp.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public String showDashboard(Model model) {
        Map<String, Long> countByStatus = dashboardService.getCountByStatus();

        model.addAttribute("totalCount",
                dashboardService.getTotalCount());
        model.addAttribute("plannedCount",
                countByStatus.getOrDefault("PLANNED", 0L));
        model.addAttribute("ongoingCount",
                countByStatus.getOrDefault("ONGOING", 0L));
        model.addAttribute("completedCount",
                countByStatus.getOrDefault("COMPLETED", 0L));
        model.addAttribute("cancelledCount",
                countByStatus.getOrDefault("CANCELLED", 0L));
        model.addAttribute("upcomingMeetings",
                dashboardService.getUpcomingMeetings());
        model.addAttribute("recentMeetings",
                dashboardService.getRecentMeetings());
        model.addAttribute("statusPercentages",
                dashboardService.getStatusPercentages());
        model.addAttribute("statuses", MeetingStatus.values());

        return "dashboard";
    }
}