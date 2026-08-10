package com.wargacare.announcement;

import com.wargacare.announcement.dto.*;
import com.wargacare.common.PagedResponse;
import com.wargacare.common.ResourceNotFoundException;
import com.wargacare.user.User;
import com.wargacare.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository, UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AnnouncementResponse create(String email, CreateAnnouncementRequest request) {
        User author = findUserByEmail(email);

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .isPinned(request.getIsPinned() != null ? request.getIsPinned() : false)
                .author(author)
                .build();

        return mapToResponse(announcementRepository.save(announcement));
    }

    @Transactional(readOnly = true)
    public PagedResponse<AnnouncementResponse> getAll(Pageable pageable) {
        Page<Announcement> page = announcementRepository.findAllByOrderByIsPinnedDescCreatedAtDesc(pageable);
        return PagedResponse.<AnnouncementResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public AnnouncementResponse getById(Long id) {
        return mapToResponse(findById(id));
    }

    @Transactional
    public AnnouncementResponse update(Long id, UpdateAnnouncementRequest request) {
        Announcement announcement = findById(id);

        if (request.getTitle() != null) {
            announcement.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            announcement.setContent(request.getContent());
        }
        if (request.getIsPinned() != null) {
            announcement.setIsPinned(request.getIsPinned());
        }

        return mapToResponse(announcementRepository.save(announcement));
    }

    @Transactional
    public AnnouncementResponse togglePin(Long id) {
        Announcement announcement = findById(id);
        announcement.setIsPinned(!Boolean.TRUE.equals(announcement.getIsPinned()));
        return mapToResponse(announcementRepository.save(announcement));
    }

    @Transactional
    public void delete(Long id) {
        Announcement announcement = findById(id);
        announcementRepository.delete(announcement);
    }

    private Announcement findById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pengumuman dengan ID " + id + " tidak ditemukan"));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
    }

    private AnnouncementResponse mapToResponse(Announcement a) {
        return AnnouncementResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .content(a.getContent())
                .isPinned(a.getIsPinned())
                .author(AnnouncementResponse.AuthorInfo.builder()
                        .id(a.getAuthor().getId())
                        .fullName(a.getAuthor().getFullName())
                        .build())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
