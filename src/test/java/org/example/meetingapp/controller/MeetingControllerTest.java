package org.example.meetingapp.controller;

import org.example.meetingapp.dto.MeetingCreateDto;
import org.example.meetingapp.dto.MeetingUpdateDto;
import org.example.meetingapp.dto.MeetingViewDto;
import org.example.meetingapp.entity.MeetingStatus;
import org.example.meetingapp.exception.ResourceNotFoundException;
import org.example.meetingapp.service.MeetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeetingController.class)
class MeetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MeetingService meetingService;

    private static final Long ID = 1L;
    private MeetingViewDto viewDto;
    private MeetingUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        viewDto = new MeetingViewDto(
                ID,
                "Planeringsmöte",
                "Genomgång av Q2",
                LocalDate.of(2026, 5, 10),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Anna Svensson",
                MeetingStatus.PLANNED
        );

        updateDto = new MeetingUpdateDto();
        updateDto.setId(ID);
        updateDto.setTitle("Planeringsmöte");
        updateDto.setDescription("Genomgång av Q2");
        updateDto.setDate(LocalDate.of(2026, 5, 10));
        updateDto.setStartTime(LocalTime.of(9, 0));
        updateDto.setEndTime(LocalTime.of(10, 0));
        updateDto.setOrganizer("Anna Svensson");
        updateDto.setStatus(MeetingStatus.PLANNED);
    }

    // ===== GET /meetings =====

    @Test
    @DisplayName("GET /meetings: ska returnera list-vy")
    void listMeetings_shouldReturnListView() throws Exception {
        Page<MeetingViewDto> page = new PageImpl<>(List.of(viewDto));
        when(meetingService.getPagedMeetings(0)).thenReturn(page);

        mockMvc.perform(get("/meetings"))
                .andExpect(status().isOk())
                .andExpect(view().name("meetings/list"));
    }

    @Test
    @DisplayName("GET /meetings: ska sätta meetings i modellen")
    void listMeetings_shouldAddMeetingsToModel() throws Exception {
        Page<MeetingViewDto> page = new PageImpl<>(List.of(viewDto));
        when(meetingService.getPagedMeetings(0)).thenReturn(page);

        mockMvc.perform(get("/meetings"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("meetings"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalElements"))
                .andExpect(model().attributeExists("statuses"));
    }

    // ===== GET /meetings/new =====

    @Test
    @DisplayName("GET /meetings/new: ska returnera create-vy")
    void showCreateForm_shouldReturnCreateView() throws Exception {
        mockMvc.perform(get("/meetings/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("meetings/create"));
    }

    @Test
    @DisplayName("GET /meetings/new: ska sätta meetingCreateDto i modellen")
    void showCreateForm_shouldAddCreateDtoToModel() throws Exception {
        mockMvc.perform(get("/meetings/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("meetingCreateDto"))
                .andExpect(model().attributeExists("statuses"));
    }

    // ===== POST /meetings/new =====

    @Test
    @DisplayName("POST /meetings/new: ska redirecta till /meetings vid giltiga data")
    void createMeeting_shouldRedirectOnSuccess() throws Exception {
        when(meetingService.createMeeting(any(MeetingCreateDto.class)))
                .thenReturn(viewDto);

        mockMvc.perform(post("/meetings/new")
                        .param("title", "Planeringsmöte")
                        .param("description", "Genomgång av Q2")
                        .param("date", "2026-05-10")
                        .param("startTime", "09:00")
                        .param("endTime", "10:00")
                        .param("organizer", "Anna Svensson")
                        .param("status", "PLANNED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/meetings"));
    }

    @Test
    @DisplayName("POST /meetings/new: ska visa create-vy vid valideringsfel")
    void createMeeting_shouldReturnCreateViewOnValidationError() throws Exception {
        mockMvc.perform(post("/meetings/new")
                        .param("title", "")
                        .param("description", "")
                        .param("organizer", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("meetings/create"));
    }

    // ===== GET /meetings/{id}/edit =====

    @Test
    @DisplayName("GET /meetings/{id}/edit: ska returnera edit-vy")
    void showEditForm_shouldReturnEditView() throws Exception {
        when(meetingService.getMeetingUpdateDtoById(ID)).thenReturn(updateDto);

        mockMvc.perform(get("/meetings/{id}/edit", ID))
                .andExpect(status().isOk())
                .andExpect(view().name("meetings/edit"));
    }

    @Test
    @DisplayName("GET /meetings/{id}/edit: ska sätta meetingUpdateDto i modellen")
    void showEditForm_shouldAddUpdateDtoToModel() throws Exception {
        when(meetingService.getMeetingUpdateDtoById(ID)).thenReturn(updateDto);

        mockMvc.perform(get("/meetings/{id}/edit", ID))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("meetingUpdateDto"))
                .andExpect(model().attributeExists("statuses"));
    }

    @Test
    @DisplayName("GET /meetings/{id}/edit: ska visa felsida när möte saknas")
    void showEditForm_shouldHandleNotFound() throws Exception {
        when(meetingService.getMeetingUpdateDtoById(ID))
                .thenThrow(new ResourceNotFoundException(ID));

        mockMvc.perform(get("/meetings/{id}/edit", ID))
                .andExpect(status().isOk())
                .andExpect(view().name("error/not-found"));
    }

    // ===== POST /meetings/{id}/edit =====

    @Test
    @DisplayName("POST /meetings/{id}/edit: ska redirecta vid giltiga data")
    void updateMeeting_shouldRedirectOnSuccess() throws Exception {
        when(meetingService.updateMeeting(eq(ID), any(MeetingUpdateDto.class)))
                .thenReturn(viewDto);

        mockMvc.perform(post("/meetings/{id}/edit", ID)
                        .param("id", "1")
                        .param("title", "Planeringsmöte")
                        .param("description", "Genomgång av Q2")
                        .param("date", "2026-05-10")
                        .param("startTime", "09:00")
                        .param("endTime", "10:00")
                        .param("organizer", "Anna Svensson")
                        .param("status", "PLANNED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/meetings"));
    }

    @Test
    @DisplayName("POST /meetings/{id}/edit: ska visa edit-vy vid valideringsfel")
    void updateMeeting_shouldReturnEditViewOnValidationError() throws Exception {
        mockMvc.perform(post("/meetings/{id}/edit", ID)
                        .param("id", "1")
                        .param("title", "")
                        .param("description", "")
                        .param("organizer", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("meetings/edit"));
    }

    // ===== POST /meetings/{id}/delete =====

    @Test
    @DisplayName("POST /meetings/{id}/delete: ska redirecta efter borttagning")
    void deleteMeeting_shouldRedirectAfterDelete() throws Exception {
        doNothing().when(meetingService).deleteMeeting(ID);

        mockMvc.perform(post("/meetings/{id}/delete", ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/meetings"));

        verify(meetingService).deleteMeeting(ID);
    }

    @Test
    @DisplayName("POST /meetings/{id}/delete: ska visa felsida när möte saknas")
    void deleteMeeting_shouldHandleNotFound() throws Exception {
        doThrow(new ResourceNotFoundException(ID))
                .when(meetingService).deleteMeeting(ID);

        mockMvc.perform(post("/meetings/{id}/delete", ID))
                .andExpect(status().isOk())
                .andExpect(view().name("error/not-found"));
    }

    // ===== GET /meetings/kanban =====

    @Test
    @DisplayName("GET /meetings/kanban: ska returnera kanban-vy")
    void showKanban_shouldReturnKanbanView() throws Exception {
        when(meetingService.getKanbanData()).thenReturn(Map.of(
                "PLANNED", List.of(viewDto),
                "ONGOING", List.of(),
                "COMPLETED", List.of(),
                "CANCELLED", List.of()
        ));

        mockMvc.perform(get("/meetings/kanban"))
                .andExpect(status().isOk())
                .andExpect(view().name("meetings/kanban"));
    }

    @Test
    @DisplayName("GET /meetings/kanban: ska sätta kanbanData i modellen")
    void showKanban_shouldAddKanbanDataToModel() throws Exception {
        when(meetingService.getKanbanData()).thenReturn(Map.of(
                "PLANNED", List.of(viewDto),
                "ONGOING", List.of(),
                "COMPLETED", List.of(),
                "CANCELLED", List.of()
        ));

        mockMvc.perform(get("/meetings/kanban"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("kanbanData"))
                .andExpect(model().attributeExists("statuses"));
    }

    // ===== POST /meetings/{id}/status =====

    @Test
    @DisplayName("POST /meetings/{id}/status: ska returnera 200 vid lyckad statusuppdatering")
    void updateStatus_shouldReturn200OnSuccess() throws Exception {
        when(meetingService.updateStatus(ID, MeetingStatus.ONGOING))
                .thenReturn(viewDto);

        mockMvc.perform(post("/meetings/{id}/status", ID)
                        .param("status", "ONGOING"))
                .andExpect(status().isOk());

        verify(meetingService).updateStatus(ID, MeetingStatus.ONGOING);
    }

    @Test
    @DisplayName("POST /meetings/{id}/status: ska returnera 404 när möte saknas")
    void updateStatus_shouldHandleNotFound() throws Exception {
        when(meetingService.updateStatus(ID, MeetingStatus.ONGOING))
                .thenThrow(new ResourceNotFoundException(ID));

        mockMvc.perform(post("/meetings/{id}/status", ID)
                        .param("status", "ONGOING"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/not-found"));
    }

    // ===== GET /meetings/search =====

    @Test
    @DisplayName("GET /meetings/search: ska returnera list-vy med sökresultat")
    void searchMeetings_shouldReturnListView() throws Exception {
        Page<MeetingViewDto> page = new PageImpl<>(List.of(viewDto));
        when(meetingService.searchByTitlePaged(eq("Planering"), eq(0)))
                .thenReturn(page);

        mockMvc.perform(get("/meetings/search")
                        .param("keyword", "Planering"))
                .andExpect(status().isOk())
                .andExpect(view().name("meetings/list"))
                .andExpect(model().attributeExists("meetings"));
    }

    @Test
    @DisplayName("GET /meetings/search: ska sätta sökparametrar i modellen")
    void searchMeetings_shouldAddSearchParamsToModel() throws Exception {
        Page<MeetingViewDto> page = new PageImpl<>(List.of(viewDto));
        when(meetingService.getMeetingsByStatusPaged(
                eq(MeetingStatus.PLANNED), eq(0))).thenReturn(page);

        mockMvc.perform(get("/meetings/search")
                        .param("status", "PLANNED"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("selectedStatus"))
                .andExpect(model().attributeExists("statuses"));
    }
}