package org.example.meetingapp.mapper;

import org.example.meetingapp.dto.MeetingCreateDto;
import org.example.meetingapp.dto.MeetingUpdateDto;
import org.example.meetingapp.dto.MeetingViewDto;
import org.example.meetingapp.entity.Meeting;
import org.example.meetingapp.entity.MeetingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class MeetingMapperTest {

    private MeetingMapper mapper;

    // Testdata
    private static final Long ID = 1L;
    private static final String TITLE = "Planeringsmöte";
    private static final String DESCRIPTION = "Genomgång av Q2";
    private static final LocalDate DATE = LocalDate.of(2026, 5, 10);
    private static final LocalTime START = LocalTime.of(9, 0);
    private static final LocalTime END = LocalTime.of(10, 0);
    private static final String ORGANIZER = "Anna Svensson";
    private static final MeetingStatus STATUS = MeetingStatus.PLANNED;

    @BeforeEach
    void setUp() {
        mapper = new MeetingMapper();
    }

    // ===== toViewDto =====

    @Test
    @DisplayName("toViewDto: ska mappa alla fält korrekt från Entity till ViewDto")
    void toViewDto_shouldMapAllFields() {
        Meeting meeting = buildMeeting();

        MeetingViewDto dto = mapper.toViewDto(meeting);

        assertAll(
                () -> assertEquals(ID, dto.id()),
                () -> assertEquals(TITLE, dto.title()),
                () -> assertEquals(DESCRIPTION, dto.description()),
                () -> assertEquals(DATE, dto.date()),
                () -> assertEquals(START, dto.startTime()),
                () -> assertEquals(END, dto.endTime()),
                () -> assertEquals(ORGANIZER, dto.organizer()),
                () -> assertEquals(STATUS, dto.status())
        );
    }

    @Test
    @DisplayName("toViewDto: ska kasta NullPointerException om meeting är null")
    void toViewDto_shouldThrowWhenMeetingIsNull() {
        assertThrows(NullPointerException.class,
                () -> mapper.toViewDto(null));
    }

    // ===== toEntity =====

    @Test
    @DisplayName("toEntity: ska mappa alla fält korrekt från CreateDto till Entity")
    void toEntity_shouldMapAllFields() {
        MeetingCreateDto dto = buildCreateDto();

        Meeting meeting = mapper.toEntity(dto);

        assertAll(
                () -> assertNull(meeting.getId()),
                () -> assertEquals(TITLE, meeting.getTitle()),
                () -> assertEquals(DESCRIPTION, meeting.getDescription()),
                () -> assertEquals(DATE, meeting.getDate()),
                () -> assertEquals(START, meeting.getStartTime()),
                () -> assertEquals(END, meeting.getEndTime()),
                () -> assertEquals(ORGANIZER, meeting.getOrganizer()),
                () -> assertEquals(STATUS, meeting.getStatus())
        );
    }

    @Test
    @DisplayName("toEntity: id ska vara null — databasen sätter id")
    void toEntity_idShouldBeNull() {
        MeetingCreateDto dto = buildCreateDto();

        Meeting meeting = mapper.toEntity(dto);

        assertNull(meeting.getId());
    }

    // ===== toUpdateDto =====

    @Test
    @DisplayName("toUpdateDto: ska mappa alla fält korrekt från Entity till UpdateDto")
    void toUpdateDto_shouldMapAllFields() {
        Meeting meeting = buildMeeting();

        MeetingUpdateDto dto = mapper.toUpdateDto(meeting);

        assertAll(
                () -> assertEquals(ID, dto.getId()),
                () -> assertEquals(TITLE, dto.getTitle()),
                () -> assertEquals(DESCRIPTION, dto.getDescription()),
                () -> assertEquals(DATE, dto.getDate()),
                () -> assertEquals(START, dto.getStartTime()),
                () -> assertEquals(END, dto.getEndTime()),
                () -> assertEquals(ORGANIZER, dto.getOrganizer()),
                () -> assertEquals(STATUS, dto.getStatus())
        );
    }

    // ===== updateEntityFromDto =====

    @Test
    @DisplayName("updateEntityFromDto: ska uppdatera befintlig entity med nya värden")
    void updateEntityFromDto_shouldUpdateAllFields() {
        Meeting meeting = buildMeeting();
        MeetingUpdateDto dto = new MeetingUpdateDto();
        dto.setId(ID);
        dto.setTitle("Nytt titel");
        dto.setDescription("Ny beskrivning");
        dto.setDate(LocalDate.of(2026, 6, 1));
        dto.setStartTime(LocalTime.of(14, 0));
        dto.setEndTime(LocalTime.of(15, 0));
        dto.setOrganizer("Erik Karlsson");
        dto.setStatus(MeetingStatus.COMPLETED);

        mapper.updateEntityFromDto(meeting, dto);

        assertAll(
                () -> assertEquals("Nytt titel", meeting.getTitle()),
                () -> assertEquals("Ny beskrivning", meeting.getDescription()),
                () -> assertEquals(LocalDate.of(2026, 6, 1), meeting.getDate()),
                () -> assertEquals(LocalTime.of(14, 0), meeting.getStartTime()),
                () -> assertEquals(LocalTime.of(15, 0), meeting.getEndTime()),
                () -> assertEquals("Erik Karlsson", meeting.getOrganizer()),
                () -> assertEquals(MeetingStatus.COMPLETED, meeting.getStatus())
        );
    }

    @Test
    @DisplayName("updateEntityFromDto: id ska inte ändras på entity")
    void updateEntityFromDto_shouldNotChangeEntityId() {
        Meeting meeting = buildMeeting();
        MeetingUpdateDto dto = new MeetingUpdateDto();
        dto.setId(99L);
        dto.setTitle("Annan titel");
        dto.setDescription("Annan beskrivning");
        dto.setDate(DATE);
        dto.setStartTime(START);
        dto.setEndTime(END);
        dto.setOrganizer(ORGANIZER);
        dto.setStatus(STATUS);

        mapper.updateEntityFromDto(meeting, dto);

        assertEquals(ID, meeting.getId());
    }

    // ===== Hjälpmetoder =====

    private Meeting buildMeeting() {
        Meeting meeting = new Meeting();
        meeting.setId(ID);
        meeting.setTitle(TITLE);
        meeting.setDescription(DESCRIPTION);
        meeting.setDate(DATE);
        meeting.setStartTime(START);
        meeting.setEndTime(END);
        meeting.setOrganizer(ORGANIZER);
        meeting.setStatus(STATUS);
        return meeting;
    }

    private MeetingCreateDto buildCreateDto() {
        MeetingCreateDto dto = new MeetingCreateDto();
        dto.setTitle(TITLE);
        dto.setDescription(DESCRIPTION);
        dto.setDate(DATE);
        dto.setStartTime(START);
        dto.setEndTime(END);
        dto.setOrganizer(ORGANIZER);
        dto.setStatus(STATUS);
        return dto;
    }
}