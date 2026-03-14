package org.example.meetingapp.service;

import org.example.meetingapp.dto.MeetingViewDto;
import org.example.meetingapp.entity.Meeting;
import org.example.meetingapp.entity.MeetingStatus;
import org.example.meetingapp.mapper.MeetingMapper;
import org.example.meetingapp.repository.MeetingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private MeetingMapper meetingMapper;

    @InjectMocks
    private DashboardService dashboardService;

    private Meeting plannedMeeting;
    private Meeting ongoingMeeting;
    private Meeting completedMeeting;
    private MeetingViewDto plannedViewDto;
    private MeetingViewDto ongoingViewDto;
    private MeetingViewDto completedViewDto;

    private static final LocalDate FUTURE_DATE =
            LocalDate.now().plusDays(5);
    private static final LocalDate PAST_DATE =
            LocalDate.now().minusDays(5);

    @BeforeEach
    void setUp() {
        plannedMeeting = buildMeeting(1L, "Planeringsmöte",
                FUTURE_DATE, MeetingStatus.PLANNED);
        ongoingMeeting = buildMeeting(2L, "Teamsmöte",
                FUTURE_DATE, MeetingStatus.ONGOING);
        completedMeeting = buildMeeting(3L, "Budgetmöte",
                PAST_DATE, MeetingStatus.COMPLETED);

        plannedViewDto = buildViewDto(1L, "Planeringsmöte",
                FUTURE_DATE, MeetingStatus.PLANNED);
        ongoingViewDto = buildViewDto(2L, "Teamsmöte",
                FUTURE_DATE, MeetingStatus.ONGOING);
        completedViewDto = buildViewDto(3L, "Budgetmöte",
                PAST_DATE, MeetingStatus.COMPLETED);
    }

    // ===== getTotalCount =====

    @Test
    @DisplayName("getTotalCount: ska returnera antal möten")
    void getTotalCount_shouldReturnCount() {
        when(meetingRepository.count()).thenReturn(3L);

        long result = dashboardService.getTotalCount();

        assertEquals(3L, result);
        verify(meetingRepository).count();
    }

    @Test
    @DisplayName("getTotalCount: ska returnera 0 när inga möten finns")
    void getTotalCount_shouldReturnZeroWhenEmpty() {
        when(meetingRepository.count()).thenReturn(0L);

        long result = dashboardService.getTotalCount();

        assertEquals(0L, result);
    }

    // ===== getCountByStatus =====

    @Test
    @DisplayName("getCountByStatus: ska returnera antal per status")
    void getCountByStatus_shouldReturnCountPerStatus() {
        when(meetingRepository.findAll()).thenReturn(
                List.of(plannedMeeting, ongoingMeeting, completedMeeting));

        Map<String, Long> result = dashboardService.getCountByStatus();

        assertEquals(1L, result.get("PLANNED"));
        assertEquals(1L, result.get("ONGOING"));
        assertEquals(1L, result.get("COMPLETED"));
        assertNull(result.get("CANCELLED"));
    }

    @Test
    @DisplayName("getCountByStatus: ska returnera tom map när inga möten finns")
    void getCountByStatus_shouldReturnEmptyMapWhenNoMeetings() {
        when(meetingRepository.findAll()).thenReturn(List.of());

        Map<String, Long> result = dashboardService.getCountByStatus();

        assertTrue(result.isEmpty());
    }

    // ===== getUpcomingMeetings =====

    @Test
    @DisplayName("getUpcomingMeetings: ska returnera max 5 kommande möten")
    void getUpcomingMeetings_shouldReturnUpToFiveMeetings() {
        when(meetingRepository
                .findByDateGreaterThanEqualOrderByDateAscStartTimeAsc(any()))
                .thenReturn(List.of(
                        plannedMeeting, ongoingMeeting,
                        plannedMeeting, ongoingMeeting,
                        plannedMeeting, ongoingMeeting
                ));
        when(meetingMapper.toViewDto(plannedMeeting)).thenReturn(plannedViewDto);
        when(meetingMapper.toViewDto(ongoingMeeting)).thenReturn(ongoingViewDto);

        List<MeetingViewDto> result = dashboardService.getUpcomingMeetings();

        assertEquals(5, result.size());
    }

    @Test
    @DisplayName("getUpcomingMeetings: ska returnera tom lista när inga kommande möten")
    void getUpcomingMeetings_shouldReturnEmptyListWhenNone() {
        when(meetingRepository
                .findByDateGreaterThanEqualOrderByDateAscStartTimeAsc(any()))
                .thenReturn(List.of());

        List<MeetingViewDto> result = dashboardService.getUpcomingMeetings();

        assertTrue(result.isEmpty());
    }

    // ===== getRecentMeetings =====

    @Test
    @DisplayName("getRecentMeetings: ska returnera max 5 senaste möten")
    void getRecentMeetings_shouldReturnUpToFiveMeetings() {
        when(meetingRepository
                .findByDateLessThanOrderByDateDescStartTimeDesc(any()))
                .thenReturn(List.of(completedMeeting));
        when(meetingMapper.toViewDto(completedMeeting)).thenReturn(completedViewDto);

        List<MeetingViewDto> result = dashboardService.getRecentMeetings();

        assertEquals(1, result.size());
        assertEquals(completedViewDto, result.get(0));
    }

    // ===== getStatusPercentages =====

    @Test
    @DisplayName("getStatusPercentages: ska beräkna procentuell fördelning")
    void getStatusPercentages_shouldCalculatePercentages() {
        when(meetingRepository.count()).thenReturn(2L);
        when(meetingRepository.findAll()).thenReturn(
                List.of(plannedMeeting, ongoingMeeting));

        Map<String, Double> result = dashboardService.getStatusPercentages();

        assertEquals(50.0, result.get("PLANNED"));
        assertEquals(50.0, result.get("ONGOING"));
    }

    @Test
    @DisplayName("getStatusPercentages: ska returnera tom map när inga möten")
    void getStatusPercentages_shouldReturnEmptyMapWhenNoMeetings() {
        when(meetingRepository.count()).thenReturn(0L);

        Map<String, Double> result = dashboardService.getStatusPercentages();

        assertTrue(result.isEmpty());
        verify(meetingRepository, never()).findAll();
    }

    // ===== Hjälpmetoder =====

    private Meeting buildMeeting(Long id, String title,
                                 LocalDate date, MeetingStatus status) {
        Meeting m = new Meeting();
        m.setId(id);
        m.setTitle(title);
        m.setDescription("Beskrivning");
        m.setDate(date);
        m.setStartTime(LocalTime.of(9, 0));
        m.setEndTime(LocalTime.of(10, 0));
        m.setOrganizer("Anna Svensson");
        m.setStatus(status);
        return m;
    }

    private MeetingViewDto buildViewDto(Long id, String title,
                                        LocalDate date, MeetingStatus status) {
        return new MeetingViewDto(id, title, "Beskrivning",
                date, LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Anna Svensson", status);
    }
}