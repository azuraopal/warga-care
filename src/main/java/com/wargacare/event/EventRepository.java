package com.wargacare.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByEventDateAfterOrderByEventDateAsc(LocalDateTime now, Pageable pageable);

    Page<Event> findAllByOrderByEventDateDesc(Pageable pageable);
}
