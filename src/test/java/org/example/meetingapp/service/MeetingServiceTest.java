package org.example.meetingapp.service;

import org.example.meetingapp.dto.MeetingCreateDto;
import org.example.meetingapp.dto.MeetingUpdateDto;
import org.example.meetingapp.dto.MeetingViewDto;
import org.example.meetingapp.entity.Meeting;
import org.example.meetingapp.entity.MeetingStatus;
import org.example.meetingapp.exception.ResourceNotFoundException;
import org.example.meetingapp.mapper.MeetingMapper;
import org.example.meetingapp.repository.MeetingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private MeetingMapper meetingMapper;

    @InjectMocks
    private MeetingService meetingService;

    private static final Long ID = 1L;
    private static final String TITLE = "Planeringsmöte";
    private static final String DESCRIPTION = "Genomgång av Q2";
    private static final LocalDate DATE = LocalDate.of(2026, 5, 10);
    private static final LocalTime START = LocalTime.of(9, 0);
    private static final LocalTime END = LocalTime.of(10, 0);
    private static final String ORGANIZER = "Anna Svensson";
    private static final MeetingStatus STATUS = MeetingStatus.PLANNED;

    private Meeting meeting;
    private MeetingViewDto viewDto;
    private MeetingCreateDto createDto;
    private MeetingUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        meeting = new Meeting();
        meeting.setId(ID);
        meeting.setTitle(TITLE);
        meeting.setDescription(DESCRIPTION);
        meeting.setDate(DATE);
        meeting.setStartTime(START);
        meeting.setEndTime(END);
        meeting.setOrganizer(ORGANIZER);
        meeting.setStatus(STATUS);

        viewDto = new MeetingViewDto(ID, TITLE, DESCRIPTION,
                DATE, START, END, ORGANIZER, STATUS);

        createDto = new MeetingCreateDto();
        createDto.setTitle(TITLE);
        createDto.setDescription(DESCRIPTION);
        createDto.setDate(DATE);
        createDto.setStartTime(START);
        createDto.setEndTime(END);
        createDto.setOrganizer(ORGANIZER);
        createDto.setStatus(STATUS);

        updateDto = new MeetingUpdateDto();
        updateDto.setId(ID);
        updateDto.setTitle(TITLE);
        updateDto.setDescription(DESCRIPTION);
        updateDto.setDate(DATE);
        updateDto.setStartTime(START);
        updateDto.setEndTime(END);
        updateDto.setOrganizer(ORGANIZER);
        updateDto.setStatus(STATUS);
    }

    // ===== getMeetingById =====

    @Test
    @DisplayName("getMeetingById: ska returnera ViewDto när möte finns")
    void getMeetingById_shouldReturnViewDto() {
        when(meetingRepository.findById(ID)).thenReturn(Optional.of(meeting));
        when(meetingMapper.toViewDto(meeting)).thenReturn(viewDto);

        MeetingViewDto result = meetingService.getMeetingById(ID);

        assertEquals(viewDto, result);
        verify(meetingRepository).findById(ID);
        verify(meetingMapper).toViewDto(meeting);
    }

    @Test
    @DisplayName("getMeetingById: ska kasta ResourceNotFoundException när möte saknas")
    void getMeetingById_shouldThrowWhenNotFound() {
        when(meetingRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> meetingService.getMeetingById(ID));

        verify(meetingRepository).findById(ID);
        verifyNoInteractions(meetingMapper);
    }

    // ===== getMeetingUpdateDtoById =====

    @Test
    @DisplayName("getMeetingUpdateDtoById: ska returnera UpdateDto när möte finns")
    void getMeetingUpdateDtoById_shouldReturnUpdateDto() {
        MeetingUpdateDto expectedDto = new MeetingUpdateDto();
        expectedDto.setId(ID);

        when(meetingRepository.findById(ID)).thenReturn(Optional.of(meeting));
        when(meetingMapper.toUpdateDto(meeting)).thenReturn(expectedDto);

        MeetingUpdateDto result = meetingService.getMeetingUpdateDtoById(ID);

        assertEquals(expectedDto, result);
        verify(meetingRepository).findById(ID);
        verify(meetingMapper).toUpdateDto(meeting);
    }

    // ===== createMeeting =====

    @Test
    @DisplayName("createMeeting: ska spara möte och returnera ViewDto")
    void createMeeting_shouldSaveAndReturnViewDto() {
        when(meetingMapper.toEntity(createDto)).thenReturn(meeting);
        when(meetingRepository.save(meeting)).thenReturn(meeting);
        when(meetingMapper.toViewDto(meeting)).thenReturn(viewDto);

        MeetingViewDto result = meetingService.createMeeting(createDto);

        assertEquals(viewDto, result);
        verify(meetingMapper).toEntity(createDto);
        verify(meetingRepository).save(meeting);
        verify(meetingMapper).toViewDto(meeting);
    }

    @Test
    @DisplayName("createMeeting: ska kasta exception om sluttid är före starttid")
    void createMeeting_shouldThrowWhenEndTimeBeforeStartTime() {
        createDto.setStartTime(LocalTime.of(10, 0));
        createDto.setEndTime(LocalTime.of(9, 0));

        assertThrows(IllegalArgumentException.class,
                () -> meetingService.createMeeting(createDto));

        verifyNoInteractions(meetingRepository);
    }

    // ===== updateMeeting =====

    @Test
    @DisplayName("updateMeeting: ska uppdatera möte och returnera ViewDto")
    void updateMeeting_shouldUpdateAndReturnViewDto() {
        when(meetingRepository.findById(ID)).thenReturn(Optional.of(meeting));
        when(meetingRepository.save(meeting)).thenReturn(meeting);
        when(meetingMapper.toViewDto(meeting)).thenReturn(viewDto);

        MeetingViewDto result = meetingService.updateMeeting(ID, updateDto);

        assertEquals(viewDto, result);
        verify(meetingRepository).findById(ID);
        verify(meetingMapper).updateEntityFromDto(meeting, updateDto);
        verify(meetingRepository).save(meeting);
    }

    @Test
    @DisplayName("updateMeeting: ska kasta ResourceNotFoundException när möte saknas")
    void updateMeeting_shouldThrowWhenNotFound() {
        when(meetingRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> meetingService.updateMeeting(ID, updateDto));

        verify(meetingRepository).findById(ID);
        verifyNoInteractions(meetingMapper);
    }

    @Test
    @DisplayName("updateMeeting: ska kasta exception om sluttid är före starttid")
    void updateMeeting_shouldThrowWhenEndTimeBeforeStartTime() {
        updateDto.setStartTime(LocalTime.of(10, 0));
        updateDto.setEndTime(LocalTime.of(9, 0));

        assertThrows(IllegalArgumentException.class,
                () -> meetingService.updateMeeting(ID, updateDto));

        verifyNoInteractions(meetingRepository);
    }

    // ===== deleteMeeting =====

    @Test
    @DisplayName("deleteMeeting: ska ta bort möte när det finns")
    void deleteMeeting_shouldDeleteWhenFound() {
        when(meetingRepository.findById(ID)).thenReturn(Optional.of(meeting));

        meetingService.deleteMeeting(ID);

        verify(meetingRepository).findById(ID);
        verify(meetingRepository).delete(meeting);
    }

    @Test
    @DisplayName("deleteMeeting: ska kasta ResourceNotFoundException när möte saknas")
    void deleteMeeting_shouldThrowWhenNotFound() {
        when(meetingRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> meetingService.deleteMeeting(ID));

        verify(meetingRepository).findById(ID);
        verify(meetingRepository, never()).delete(any());
    }

    // ===== getPagedMeetings =====

    @Test
    @DisplayName("getPagedMeetings: ska returnera paginerad lista av ViewDtos")
    void getPagedMeetings_shouldReturnPagedViewDtos() {
        Page<Meeting> meetingPage = new PageImpl<>(List.of(meeting));
        when(meetingRepository.findAll(any(Pageable.class))).thenReturn(meetingPage);
        when(meetingMapper.toViewDto(meeting)).thenReturn(viewDto);

        Page<MeetingViewDto> result = meetingService.getPagedMeetings(0);

        assertEquals(1, result.getTotalElements());
        assertEquals(viewDto, result.getContent().get(0));
        verify(meetingRepository).findAll(any(Pageable.class));
    }

    // ===== getMeetingsByOrganizer =====

    @Test
    @DisplayName("getMeetingsByOrganizer: ska returnera möten filtrerade på organisatör")
    void getMeetingsByOrganizer_shouldReturnFilteredMeetings() {
        when(meetingRepository.findByOrganizer(ORGANIZER)).thenReturn(List.of(meeting));
        when(meetingMapper.toViewDto(meeting)).thenReturn(viewDto);

        List<MeetingViewDto> result = meetingService.getMeetingsByOrganizer(ORGANIZER);

        assertEquals(1, result.size());
        assertEquals(viewDto, result.get(0));
        verify(meetingRepository).findByOrganizer(ORGANIZER);
    }

    // ===== getMeetingsByDateRange =====

    @Test
    @DisplayName("getMeetingsByDateRange: ska returnera möten inom datumintervall")
    void getMeetingsByDateRange_shouldReturnMeetingsInRange() {
        LocalDate from = LocalDate.of(2026, 5, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);

        when(meetingRepository.findByDateBetween(from, to)).thenReturn(List.of(meeting));
        when(meetingMapper.toViewDto(meeting)).thenReturn(viewDto);

        List<MeetingViewDto> result = meetingService.getMeetingsByDateRange(from, to);

        assertEquals(1, result.size());
        assertEquals(viewDto, result.get(0));
        verify(meetingRepository).findByDateBetween(from, to);
    }

    // ===== updateStatus =====

    @Test
    @DisplayName("updateStatus: ska uppdatera status och returnera ViewDto")
    void updateStatus_shouldUpdateStatusAndReturnViewDto() {
        when(meetingRepository.findById(ID)).thenReturn(Optional.of(meeting));
        when(meetingRepository.save(meeting)).thenReturn(meeting);
        when(meetingMapper.toViewDto(meeting)).thenReturn(viewDto);

        MeetingViewDto result = meetingService.updateStatus(ID, MeetingStatus.ONGOING);

        assertEquals(MeetingStatus.ONGOING, meeting.getStatus());
        assertEquals(viewDto, result);
        verify(meetingRepository).findById(ID);
        verify(meetingRepository).save(meeting);
    }

    @Test
    @DisplayName("updateStatus: ska kasta ResourceNotFoundException när möte saknas")
    void updateStatus_shouldThrowWhenNotFound() {
        when(meetingRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> meetingService.updateStatus(ID, MeetingStatus.ONGOING));

        verify(meetingRepository, never()).save(any());
    }

    // ===== getKanbanData =====

    @Test
    @DisplayName("getKanbanData: ska returnera möten grupperade per status")
    void getKanbanData_shouldReturnMeetingsGroupedByStatus() {
        when(meetingRepository.findByStatusOrderByDateAscStartTimeAsc(any()))
                .thenReturn(List.of(meeting));
        when(meetingMapper.toViewDto(meeting)).thenReturn(viewDto);

        Map<String, List<MeetingViewDto>> result = meetingService.getKanbanData();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.containsKey("PLANNED"));
        assertTrue(result.containsKey("ONGOING"));
        assertTrue(result.containsKey("COMPLETED"));
        assertTrue(result.containsKey("CANCELLED"));
    }

    @Test
    @DisplayName("getKanbanData: ska returnera tom lista för status utan möten")
    void getKanbanData_shouldReturnEmptyListForStatusWithNoMeetings() {
        when(meetingRepository.findByStatusOrderByDateAscStartTimeAsc(
                eq(MeetingStatus.PLANNED))).thenReturn(List.of(meeting));
        when(meetingRepository.findByStatusOrderByDateAscStartTimeAsc(
                eq(MeetingStatus.ONGOING))).thenReturn(List.of());
        when(meetingRepository.findByStatusOrderByDateAscStartTimeAsc(
                eq(MeetingStatus.COMPLETED))).thenReturn(List.of());
        when(meetingRepository.findByStatusOrderByDateAscStartTimeAsc(
                eq(MeetingStatus.CANCELLED))).thenReturn(List.of());
        when(meetingMapper.toViewDto(meeting)).thenReturn(viewDto);

        Map<String, List<MeetingViewDto>> result = meetingService.getKanbanData();

        assertEquals(1, result.get("PLANNED").size());
        assertEquals(0, result.get("ONGOING").size());
        assertEquals(0, result.get("COMPLETED").size());
        assertEquals(0, result.get("CANCELLED").size());
    }

    // ===== getMeetingsForMonth =====

    @Test
    @DisplayName("getMeetingsForMonth: ska returnera möten grupperade per dag")
    void getMeetingsForMonth_shouldReturnMeetingsGroupedByDay() {
        when(meetingRepository.findByDateBetween(any(), any()))
                .thenReturn(List.of(meeting));
        when(meetingMapper.toViewDto(meeting)).thenReturn(viewDto);

        Map<LocalDate, List<MeetingViewDto>> result =
                meetingService.getMeetingsForMonth(2026, 5);

        assertNotNull(result);
        assertTrue(result.containsKey(DATE));
        assertEquals(1, result.get(DATE).size());
    }

    @Test
    @DisplayName("getMeetingsForMonth: ska returnera tom map om inga möten finns")
    void getMeetingsForMonth_shouldReturnEmptyMapWhenNoMeetings() {
        when(meetingRepository.findByDateBetween(any(), any()))
                .thenReturn(List.of());

        Map<LocalDate, List<MeetingViewDto>> result =
                meetingService.getMeetingsForMonth(2026, 5);

        assertTrue(result.isEmpty());
    }
}