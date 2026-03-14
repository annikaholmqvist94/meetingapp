package org.example.meetingapp.service;

import org.example.meetingapp.dto.MeetingCreateDto;
import org.example.meetingapp.dto.MeetingUpdateDto;
import org.example.meetingapp.dto.MeetingViewDto;
import org.example.meetingapp.entity.Meeting;
import org.example.meetingapp.entity.MeetingStatus;
import org.example.meetingapp.exception.ResourceNotFoundException;
import org.example.meetingapp.mapper.MeetingMapper;
import org.example.meetingapp.repository.MeetingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MeetingService {

    private static final int PAGE_SIZE = 5;
    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper;

    public MeetingService(MeetingRepository meetingRepository,
                          MeetingMapper meetingMapper) {
        this.meetingRepository = meetingRepository;
        this.meetingMapper = meetingMapper;
    }

    // Hämta ett möte som ViewDto
    @Transactional(readOnly = true)
    public MeetingViewDto getMeetingById(Long id) {
        Meeting meeting = findMeetingOrThrow(id);
        return meetingMapper.toViewDto(meeting);
    }

    // Hämta ett möte som UpdateDto (för uppdateringsformulär)
    @Transactional(readOnly = true)
    public MeetingUpdateDto getMeetingUpdateDtoById(Long id) {
        Meeting meeting = findMeetingOrThrow(id);
        return meetingMapper.toUpdateDto(meeting);
    }

    // Skapa nytt möte
    public MeetingViewDto createMeeting(MeetingCreateDto dto) {
        validateTimes(dto.getStartTime(), dto.getEndTime());
        Meeting meeting = meetingMapper.toEntity(dto);
        Meeting saved = meetingRepository.save(meeting);
        return meetingMapper.toViewDto(saved);
    }

    // Uppdatera befintligt möte
    public MeetingViewDto updateMeeting(Long id, MeetingUpdateDto dto) {
        validateTimes(dto.getStartTime(), dto.getEndTime());
        Meeting meeting = findMeetingOrThrow(id);
        meetingMapper.updateEntityFromDto(meeting, dto);
        Meeting saved = meetingRepository.save(meeting);
        return meetingMapper.toViewDto(saved);
    }

    // Ta bort möte
    public void deleteMeeting(Long id) {
        Meeting meeting = findMeetingOrThrow(id);
        meetingRepository.delete(meeting);
    }

    private void validateTimes(java.time.LocalTime start, java.time.LocalTime end) {
        if (start != null && end != null && !end.isAfter(start)) {
            throw new IllegalArgumentException(
                    "Sluttid måste vara efter starttid");
        }
    }

    // --- Filtrering ---

    @Transactional(readOnly = true)
    public List<MeetingViewDto> getMeetingsByOrganizer(String organizer) {
        return meetingRepository.findByOrganizer(organizer)
                .stream()
                .map(meetingMapper::toViewDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeetingViewDto> getMeetingsByDateRange(LocalDate from, LocalDate to) {
        return meetingRepository.findByDateBetween(from, to)
                .stream()
                .map(meetingMapper::toViewDto)
                .toList();
    }

    private Meeting findMeetingOrThrow(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    // Kanban — möten grupperade per status, sorterade på datum+starttid
    @Transactional(readOnly = true)
    public Map<MeetingStatus, List<MeetingViewDto>> getKanbanData() {
        Map<MeetingStatus, List<MeetingViewDto>> kanban = new LinkedHashMap<>();

        // Definierar ordningen på kolumnerna
        for (MeetingStatus status : MeetingStatus.values()) {
            List<MeetingViewDto> meetings = meetingRepository
                    .findByStatusOrderByDateAscStartTimeAsc(status)
                    .stream()
                    .map(meetingMapper::toViewDto)
                    .toList();
            kanban.put(status, meetings);
        }
        return kanban;
    }

    // Uppdatera bara status — används av drag & drop
    public MeetingViewDto updateStatus(Long id, MeetingStatus newStatus) {
        Meeting meeting = findMeetingOrThrow(id);
        meeting.setStatus(newStatus);
        Meeting saved = meetingRepository.save(meeting);
        return meetingMapper.toViewDto(saved);
    }

    //-----Paginering-----

    @Transactional(readOnly = true)
    public Page<MeetingViewDto> getPagedMeetings(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE,
                Sort.by("date").descending());
        return meetingRepository.findAll(pageable)
                .map(meetingMapper::toViewDto);
    }

    @Transactional(readOnly = true)
    public Page<MeetingViewDto> searchByTitlePaged(String keyword, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE,
                Sort.by("date").descending());
        return meetingRepository
                .findByTitleContainingIgnoreCase(keyword, pageable)
                .map(meetingMapper::toViewDto);
    }

    @Transactional(readOnly = true)
    public Page<MeetingViewDto> getMeetingsByStatusPaged(MeetingStatus status, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE,
                Sort.by("date").descending());
        return meetingRepository.findByStatus(status, pageable)
                .map(meetingMapper::toViewDto);
    }

}