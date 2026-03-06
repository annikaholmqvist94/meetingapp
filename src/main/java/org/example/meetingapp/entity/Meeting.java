package org.example.meetingapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "meetings")
public class Meeting {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titel får inte vara tom")
    @Size(min = 2, max = 100, message = "Titel måste vara mellan 2 och 100 tecken")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Beskrivning får inte vara tom")
    @Size(max = 500, message = "Beskrivning får max vara 500 tecken")
    @Column(nullable = false, length = 500)
    private String description;

    @NotNull(message = "Datum måste anges")
    @FutureOrPresent(message = "Datum kan inte vara i det förflutna")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "Starttid måste anges")
    @Column(nullable = false)
    private LocalTime startTime;

    @NotNull(message = "Sluttid måste anges")
    @Column(nullable = false)
    private LocalTime endTime;

    // Domänspecifika attribut
    @NotBlank(message = "Organisatör får inte vara tom")
    @Size(min = 2, max = 100, message = "Organisatör måste vara mellan 2 och 100 tecken")
    @Column(nullable = false)
    private String organizer;

    @NotNull(message = "Status måste anges")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingStatus status;

    // Konstruktorer
    public Meeting() {}

    public Meeting(String title, String description, LocalDate date,
                   LocalTime startTime, LocalTime endTime,
                   String organizer, MeetingStatus status) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.organizer = organizer;
        this.status = status;
    }

    // Getters och Setters
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
        return id != null && id.equals(meeting.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }



}

