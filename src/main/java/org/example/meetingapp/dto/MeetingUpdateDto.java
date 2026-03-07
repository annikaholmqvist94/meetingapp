package org.example.meetingapp.dto;

import jakarta.validation.constraints.*;
import org.example.meetingapp.entity.MeetingStatus;
import org.example.meetingapp.validation.EndTimeAfterStartTime;

import java.time.LocalDate;
import java.time.LocalTime;

@EndTimeAfterStartTime
public class MeetingUpdateDto {

    // Id behövs för att identifiera vilket möte som uppdateras
    @NotNull(message = "Id måste anges")
    private Long id;

    @NotBlank(message = "Titel får inte vara tom")
    @Size(min = 2, max = 100, message = "Titel måste vara mellan 2 och 100 tecken")
    private String title;

    @NotBlank(message = "Beskrivning får inte vara tom")
    @Size(max = 500, message = "Beskrivning får max vara 500 tecken")
    private String description;

    @NotNull(message = "Datum måste anges")
    private LocalDate date;

    @NotNull(message = "Starttid måste anges")
    private LocalTime startTime;

    @NotNull(message = "Sluttid måste anges")
    private LocalTime endTime;

    @NotBlank(message = "Organisatör får inte vara tom")
    @Size(min = 2, max = 100)
    private String organizer;

    @NotNull(message = "Status måste anges")
    private MeetingStatus status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public MeetingStatus getStatus() { return status; }
    public void setStatus(MeetingStatus status) { this.status = status; }
}