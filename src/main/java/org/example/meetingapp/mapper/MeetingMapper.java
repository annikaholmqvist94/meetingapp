package org.example.meetingapp.mapper;

import org.example.meetingapp.dto.MeetingCreateDto;
import org.example.meetingapp.dto.MeetingUpdateDto;
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

    // CreateDto → Entity (för att spara nytt möte)
    public Meeting toEntity(MeetingCreateDto dto) {
        Meeting meeting = new Meeting();
        meeting.setTitle(dto.getTitle());
        meeting.setDescription(dto.getDescription());
        meeting.setDate(dto.getDate());
        meeting.setStartTime(dto.getStartTime());
        meeting.setEndTime(dto.getEndTime());
        meeting.setOrganizer(dto.getOrganizer());
        meeting.setStatus(dto.getStatus());
        return meeting;
    }

    // Entity → UpdateDto (för att fylla i uppdateringsformulär)
    public MeetingUpdateDto toUpdateDto(Meeting meeting) {
        MeetingUpdateDto dto = new MeetingUpdateDto();
        dto.setId(meeting.getId());
        dto.setTitle(meeting.getTitle());
        dto.setDescription(meeting.getDescription());
        dto.setDate(meeting.getDate());
        dto.setStartTime(meeting.getStartTime());
        dto.setEndTime(meeting.getEndTime());
        dto.setOrganizer(meeting.getOrganizer());
        dto.setStatus(meeting.getStatus());
        return dto;
    }

    // UpdateDto → uppdatera befintlig Entity
    public void updateEntityFromDto(Meeting meeting, MeetingUpdateDto dto) {
        meeting.setTitle(dto.getTitle());
        meeting.setDescription(dto.getDescription());
        meeting.setDate(dto.getDate());
        meeting.setStartTime(dto.getStartTime());
        meeting.setEndTime(dto.getEndTime());
        meeting.setOrganizer(dto.getOrganizer());
        meeting.setStatus(dto.getStatus());
    }
}