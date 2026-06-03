package com.wargacare.event;

import com.wargacare.common.PagedResponse;
import com.wargacare.common.ResourceNotFoundException;
import com.wargacare.event.dto.*;
import com.wargacare.user.User;
import com.wargacare.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EventResponse create(String email, CreateEventRequest request) {
        User organizer = findUserByEmail(email);

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .location(request.getLocation())
                .organizer(organizer)
                .build();

        return mapToResponse(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public PagedResponse<EventResponse> getAll(Pageable pageable) {
        Page<Event> page = eventRepository.findAllByOrderByEventDateDesc(pageable);
        return mapToPagedResponse(page);
    }

    @Transactional(readOnly = true)
    public PagedResponse<EventResponse> getUpcoming(Pageable pageable) {
        Page<Event> page = eventRepository.findByEventDateAfterOrderByEventDateAsc(LocalDateTime.now(), pageable);
        return mapToPagedResponse(page);
    }

    @Transactional(readOnly = true)
    public EventResponse getById(Long id) {
        return mapToResponse(findById(id));
    }

    @Transactional
    public EventResponse update(Long id, UpdateEventRequest request) {
        Event event = findById(id);

        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }

        return mapToResponse(eventRepository.save(event));
    }

    @Transactional
    public void delete(Long id) {
        Event event = findById(id);
        eventRepository.delete(event);
    }

    private Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kegiatan dengan ID " + id + " tidak ditemukan"));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
    }

    private EventResponse mapToResponse(Event e) {
        return EventResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .location(e.getLocation())
                .organizer(EventResponse.OrganizerInfo.builder()
                        .id(e.getOrganizer().getId())
                        .fullName(e.getOrganizer().getFullName())
                        .build())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private PagedResponse<EventResponse> mapToPagedResponse(Page<Event> page) {
        return PagedResponse.<EventResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
