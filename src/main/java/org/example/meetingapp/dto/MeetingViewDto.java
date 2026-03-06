package org.example.meetingapp.dto;

import org.example.meetingapp.entity.MeetingStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record MeetingViewDto(
        Long id,
        String title,
        String description,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String organizer,
        MeetingStatus status
) {
}
