package org.example.meetingapp.repository;

import org.example.meetingapp.entity.Meeting;
import org.example.meetingapp.entity.MeetingStatus;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeetingRepository extends ListCrudRepository<Meeting, Long> {

    List<Meeting> findByStatus(MeetingStatus status);
    List<Meeting> findByOrganizer(String organizer);
    List<Meeting> findByDateBetween(LocalDate from, LocalDate to);
    List<Meeting> findByTitleContainingIgnoreCase(String keyword);

}