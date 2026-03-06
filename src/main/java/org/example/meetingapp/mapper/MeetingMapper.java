package org.example.meetingapp.mapper;

import org.example.meetingapp.dto.MeetingFormDto;
import org.example.meetingapp.dto.MeetingViewDto;
import org.example.meetingapp.entity.Meeting;
import org.springframework.stereotype.Component;

@Component
public class MeetingMapper {

    // Entity → ViewDto (för listor och visning)
    public MeetingViewDto toViewDto(Meeting meeting) {
        return new MeetingViewDto(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getDescription(),
                meeting.getDate(),
                meeting.getStartTime(),
                meeting.getEndTime(),
                meeting.getOrganizer(),
                meeting.getStatus()
        );
    }

    // FormDto → Entity (för att spara nytt möte)
    public Meeting toEntity(MeetingFormDto dto) {
        Meeting meeting = new Meeting();
        applyFormDto(meeting, dto);
        return meeting;
    }

    // Entity → FormDto (för att fylla i formulär vid uppdatering)
    public MeetingFormDto toFormDto(Meeting meeting) {
        MeetingFormDto dto = new MeetingFormDto();
        dto.setTitle(meeting.getTitle());
        dto.setDescription(meeting.getDescription());
        dto.setDate(meeting.getDate());
        dto.setStartTime(meeting.getStartTime());
        dto.setEndTime(meeting.getEndTime());
        dto.setOrganizer(meeting.getOrganizer());
        dto.setStatus(meeting.getStatus());
        return dto;
    }

    // Uppdatera befintlig entity med formulärdata (för PUT/update)
    public void updateEntityFromDto(Meeting meeting, MeetingFormDto dto) {
        applyFormDto(meeting, dto);
    }

    // Privat hjälpmetod — undviker kodduplicering
    private void applyFormDto(Meeting meeting, MeetingFormDto dto) {
        meeting.setTitle(dto.getTitle());
        meeting.setDescription(dto.getDescription());
        meeting.setDate(dto.getDate());
        meeting.setStartTime(dto.getStartTime());
        meeting.setEndTime(dto.getEndTime());
        meeting.setOrganizer(dto.getOrganizer());
        meeting.setStatus(dto.getStatus());
    }
}