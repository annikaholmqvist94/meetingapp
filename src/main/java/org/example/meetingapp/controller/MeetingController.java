package org.example.meetingapp.controller;

import jakarta.validation.Valid;
import org.example.meetingapp.dto.MeetingCreateDto;
import org.example.meetingapp.dto.MeetingUpdateDto;
import org.example.meetingapp.dto.MeetingViewDto;
import org.example.meetingapp.entity.MeetingStatus;
import org.example.meetingapp.service.MeetingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    // GET /meetings — lista alla möten
    @GetMapping
    public String listMeetings(Model model) {
        List<MeetingViewDto> meetings = meetingService.getAllMeetings();
        model.addAttribute("meetings", meetings);
        model.addAttribute("statuses", MeetingStatus.values());
        return "meetings/list";
    }

    // GET /meetings/new — visa formulär för nytt möte
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("meetingCreateDto", new MeetingCreateDto());
        model.addAttribute("statuses", MeetingStatus.values());
        return "meetings/create";
    }

    // POST /meetings/new — spara nytt möte
    @PostMapping("/new")
    public String createMeeting(
            @Valid @ModelAttribute MeetingCreateDto meetingCreateDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", MeetingStatus.values());
            return "meetings/create";
        }

        meetingService.createMeeting(meetingCreateDto);
        redirectAttributes.addFlashAttribute("successMessage",
                "Mötet skapades!");
        return "redirect:/meetings";
    }

    // GET /meetings/{id}/edit — visa formulär för uppdatering
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        MeetingUpdateDto meetingUpdateDto = meetingService.getMeetingUpdateDtoById(id);
        model.addAttribute("meetingUpdateDto", meetingUpdateDto);
        model.addAttribute("statuses", MeetingStatus.values());
        return "meetings/edit";
    }

    // POST /meetings/{id}/edit — spara uppdaterat möte
    @PostMapping("/{id}/edit")
    public String updateMeeting(
            @PathVariable Long id,
            @Valid @ModelAttribute MeetingUpdateDto meetingUpdateDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", MeetingStatus.values());
            return "meetings/edit";
        }

        meetingService.updateMeeting(id, meetingUpdateDto);
        redirectAttributes.addFlashAttribute("successMessage",
                "Mötet uppdaterades!");
        return "redirect:/meetings";
    }

    // POST /meetings/{id}/delete — ta bort möte
    @PostMapping("/{id}/delete")
    public String deleteMeeting(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        meetingService.deleteMeeting(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Mötet togs bort.");
        return "redirect:/meetings";
    }

    // GET /meetings/search — filtrering
    @GetMapping("/search")
    public String searchMeetings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MeetingStatus status,
            @RequestParam(required = false) String organizer,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            Model model) {

        List<MeetingViewDto> meetings;

        if (keyword != null && !keyword.isBlank()) {
            meetings = meetingService.searchByTitle(keyword);
        } else if (status != null) {
            meetings = meetingService.getMeetingsByStatus(status);
        } else if (organizer != null && !organizer.isBlank()) {
            meetings = meetingService.getMeetingsByOrganizer(organizer);
        } else if (from != null && to != null) {
            meetings = meetingService.getMeetingsByDateRange(from, to);
        } else {
            meetings = meetingService.getAllMeetings();
        }

        model.addAttribute("meetings", meetings);
        model.addAttribute("statuses", MeetingStatus.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("organizer", organizer);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "meetings/list";
    }
}