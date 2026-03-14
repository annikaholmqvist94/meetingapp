package org.example.meetingapp.service;

import org.example.meetingapp.dto.MeetingViewDto;
import org.example.meetingapp.entity.MeetingStatus;
import org.example.meetingapp.mapper.MeetingMapper;
import org.example.meetingapp.repository.MeetingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper;

    public DashboardService(MeetingRepository meetingRepository,
                            MeetingMapper meetingMapper) {
        this.meetingRepository = meetingRepository;
        this.meetingMapper = meetingMapper;
    }

    // Totalt antal möten
    public long getTotalCount() {
        return meetingRepository.count();
    }

    // Antal per status
    public Map<String, Long> getCountByStatus() {
        return meetingRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        m -> m.getStatus().name(),
                        Collectors.counting()
                ));
    }

    // Kommande möten — datum från och med idag, sorterade
    public List<MeetingViewDto> getUpcomingMeetings() {
        return meetingRepository
                .findByDateGreaterThanEqualOrderByDateAscStartTimeAsc(LocalDate.now())
                .stream()
                .limit(5)
                .map(meetingMapper::toViewDto)
                .toList();
    }

    // Senaste möten — redan passerade, sorterade fallande
    public List<MeetingViewDto> getRecentMeetings() {
        return meetingRepository
                .findByDateLessThanOrderByDateDescStartTimeDesc(LocalDate.now())
                .stream()
                .limit(5)
                .map(meetingMapper::toViewDto)
                .toList();
    }

    // Procentuell fördelning per status
    public Map<String, Double> getStatusPercentages() {
        long total = getTotalCount();
        if (total == 0) return Map.of();

        return meetingRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        m -> m.getStatus().name(),
                        Collectors.collectingAndThen(
                                Collectors.counting(),
                                count -> Math.round((count * 100.0 / total) * 10.0) / 10.0
                        )
                ));
    }
}