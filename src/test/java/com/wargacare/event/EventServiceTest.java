package com.wargacare.event;

import com.wargacare.common.PagedResponse;
import com.wargacare.common.ResourceNotFoundException;
import com.wargacare.event.dto.CreateEventRequest;
import com.wargacare.event.dto.EventResponse;
import com.wargacare.event.dto.UpdateEventRequest;
import com.wargacare.user.User;
import com.wargacare.user.UserRepository;
import com.wargacare.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private User admin;
    private Event event;

    @BeforeEach
    void setUp() {
        admin = User.builder()
                .id(2L)
                .fullName("Admin RT")
                .email("admin@test.com")
                .role(UserRole.ADMIN_RT)
                .build();

        event = Event.builder()
                .id(1L)
                .title("Rapat RT")
                .description("Rapat bulanan")
                .eventDate(LocalDateTime.now().plusDays(2))
                .location("Posko RT")
                .organizer(admin)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Buat event sukses")
    void create_success() {
        CreateEventRequest request = CreateEventRequest.builder()
                .title("Rapat RT")
                .description("Rapat bulanan")
                .eventDate(LocalDateTime.now().plusDays(2))
                .location("Posko RT")
                .build();

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventResponse response = eventService.create("admin@test.com", request);

        assertThat(response.getTitle()).isEqualTo("Rapat RT");
        assertThat(response.getOrganizer().getFullName()).isEqualTo("Admin RT");
    }

    @Test
    @DisplayName("Ambil semua event")
    void getAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> page = new PageImpl<>(List.of(event), pageable, 1);

        when(eventRepository.findAllByOrderByEventDateDesc(pageable)).thenReturn(page);

        PagedResponse<EventResponse> response = eventService.getAll(pageable);

        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Update event sukses")
    void update_success() {
        UpdateEventRequest request = UpdateEventRequest.builder()
                .title("Rapat RW")
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventResponse response = eventService.update(1L, request);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Delete event sukses")
    void delete_success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.delete(1L);

        verify(eventRepository).delete(event);
    }
}
