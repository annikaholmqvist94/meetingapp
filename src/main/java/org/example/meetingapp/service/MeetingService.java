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

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper;

    public MeetingService(MeetingRepository meetingRepository,
                          MeetingMapper meetingMapper) {
        this.meetingRepository = meetingRepository;
        this.meetingMapper = meetingMapper;
    }

    // Hämta alla möten
    @Transactional(readOnly = true)
    public List<MeetingViewDto> getAllMeetings() {
        return meetingRepository.findAll()
                .stream()
                .map(meetingMapper::toViewDto)
                .toList();
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
        Meeting meeting = meetingMapper.toEntity(dto);
        Meeting saved = meetingRepository.save(meeting);
        return meetingMapper.toViewDto(saved);
    }

    // Uppdatera befintligt möte
    public MeetingViewDto updateMeeting(Long id, MeetingUpdateDto dto) {
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

    // --- Filtrering ---

    @Transactional(readOnly = true)
    public List<MeetingViewDto> getMeetingsByStatus(MeetingStatus status) {
        return meetingRepository.findByStatus(status)
                .stream()
                .map(meetingMapper::toViewDto)
                .toList();
    }

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

    @Transactional(readOnly = true)
    public List<MeetingViewDto> searchByTitle(String keyword) {
        return meetingRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(meetingMapper::toViewDto)
                .toList();
    }

    private Meeting findMeetingOrThrow(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }
}