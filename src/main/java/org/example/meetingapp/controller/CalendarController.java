package org.example.meetingapp.controller;

import org.example.meetingapp.dto.MeetingViewDto;
import org.example.meetingapp.service.MeetingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private final MeetingService meetingService;

    public CalendarController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @GetMapping
    public String showCalendar(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        // Standard — visa aktuell månad
        LocalDate today = LocalDate.now();
        int currentYear  = year  != null ? year  : today.getYear();
        int currentMonth = month != null ? month : today.getMonthValue();

        YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);
        LocalDate firstDay  = yearMonth.atDay(1);

        // Föregående och nästa månad för navigation
        YearMonth prevMonth = yearMonth.minusMonths(1);
        YearMonth nextMonth = yearMonth.plusMonths(1);

        // Möten för månaden
        Map<LocalDate, List<MeetingViewDto>> meetingsByDay =
                meetingService.getMeetingsForMonth(currentYear, currentMonth);

        model.addAttribute("yearMonth",     yearMonth);
        model.addAttribute("firstDay",      firstDay);
        model.addAttribute("today",         today);
        model.addAttribute("meetingsByDay", meetingsByDay);
        model.addAttribute("prevYear",      prevMonth.getYear());
        model.addAttribute("prevMonth",     prevMonth.getMonthValue());
        model.addAttribute("nextYear",      nextMonth.getYear());
        model.addAttribute("nextMonth",     nextMonth.getMonthValue());
        model.addAttribute("daysInMonth",   yearMonth.lengthOfMonth());

        return "calendar";
    }
}