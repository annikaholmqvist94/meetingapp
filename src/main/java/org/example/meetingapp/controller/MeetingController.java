package org.example.meetingapp.controller;

import jakarta.validation.Valid;
import org.example.meetingapp.dto.MeetingCreateDto;
import org.example.meetingapp.dto.MeetingUpdateDto;
import org.example.meetingapp.dto.MeetingViewDto;
import org.example.meetingapp.entity.MeetingStatus;
import org.example.meetingapp.service.MeetingService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

    // GET /meetings — paginerad lista
    @GetMapping
    public String listMeetings(
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Page<MeetingViewDto> meetingPage = meetingService.getPagedMeetings(page);
        model.addAttribute("meetings", meetingPage.getContent());
        model.addAttribute("currentPage", meetingPage.getNumber());
        model.addAttribute("totalPages", meetingPage.getTotalPages());
        model.addAttribute("totalElements", meetingPage.getTotalElements());
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

    // GET /meetings/search — paginerad sökning
    @GetMapping("/search")
    public String searchMeetings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MeetingStatus status,
            @RequestParam(required = false) String organizer,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<MeetingViewDto> meetingPage = null;
        List<MeetingViewDto> meetingList = null;

        if (keyword != null && !keyword.isBlank()) {
            meetingPage = meetingService.searchByTitlePaged(keyword, page);
        } else if (status != null) {
            meetingPage = meetingService.getMeetingsByStatusPaged(status, page);
        } else if (organizer != null && !organizer.isBlank()) {
            meetingList = meetingService.getMeetingsByOrganizer(organizer);
        } else if (from != null && to != null) {
            meetingList = meetingService.getMeetingsByDateRange(from, to);
        } else {
            meetingPage = meetingService.getPagedMeetings(page);
        }

        if (meetingPage != null) {
            model.addAttribute("meetings", meetingPage.getContent());
            model.addAttribute("currentPage", meetingPage.getNumber());
            model.addAttribute("totalPages", meetingPage.getTotalPages());
            model.addAttribute("totalElements", meetingPage.getTotalElements());
        } else {
            model.addAttribute("meetings", meetingList);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
            model.addAttribute("totalElements", meetingList.size());
        }

        model.addAttribute("statuses", MeetingStatus.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("organizer", organizer);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "meetings/list";
    }

    // GET /meetings/kanban — visa kanban-vy
    @GetMapping("/kanban")
    public String showKanban(Model model) {
        model.addAttribute("kanbanData", meetingService.getKanbanData());
        model.addAttribute("statuses", MeetingStatus.values());
        return "meetings/kanban";
    }

    // POST /meetings/{id}/status — uppdatera status via drag & drop
    @PostMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam MeetingStatus status) {
        meetingService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}