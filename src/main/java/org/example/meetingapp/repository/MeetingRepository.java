package org.example.meetingapp.repository;

import org.example.meetingapp.entity.Meeting;
import org.example.meetingapp.entity.MeetingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeetingRepository extends
        ListCrudRepository<Meeting, Long>,
        PagingAndSortingRepository<Meeting, Long> {

    Page<Meeting> findAll(Pageable pageable);
    Page<Meeting> findByStatus(MeetingStatus status, Pageable pageable);
    Page<Meeting> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    List<Meeting> findByOrganizer(String organizer);
    List<Meeting> findByDateBetween(LocalDate from, LocalDate to);

    // Sorterad på datum och starttid — används av kanban
    List<Meeting> findByStatusOrderByDateAscStartTimeAsc(MeetingStatus status);
}