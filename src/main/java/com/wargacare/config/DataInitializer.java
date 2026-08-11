package com.wargacare.config;

import com.wargacare.announcement.Announcement;
import com.wargacare.announcement.AnnouncementRepository;
import com.wargacare.event.Event;
import com.wargacare.event.EventRepository;
import com.wargacare.report.Report;
import com.wargacare.report.ReportCategory;
import com.wargacare.report.ReportRepository;
import com.wargacare.report.ReportStatus;
import com.wargacare.user.User;
import com.wargacare.user.UserRepository;
import com.wargacare.user.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AnnouncementRepository announcementRepository;
    private final EventRepository eventRepository;
    private final ReportRepository reportRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           AnnouncementRepository announcementRepository,
                           EventRepository eventRepository,
                           ReportRepository reportRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.announcementRepository = announcementRepository;
        this.eventRepository = eventRepository;
        this.reportRepository = reportRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User admin = userRepository.findByEmail("admin@wargacare.id").orElseGet(() -> {
            User newAdmin = User.builder()
                    .fullName("Pak RT Syamsuddin")
                    .email("admin@wargacare.id")
                    .password(passwordEncoder.encode("admin123"))
                    .role(UserRole.ADMIN_RT)
                    .rt("01")
                    .rw("05")
                    .phone("08123456789")
                    .address("Pos RT 01 / No. 01")
                    .isActive(true)
                    .build();
            return userRepository.save(newAdmin);
        });

        User warga = userRepository.findByEmail("warga@wargacare.id").orElseGet(() -> {
            User newWarga = User.builder()
                    .fullName("Ahmad Subagyo")
                    .email("warga@wargacare.id")
                    .password(passwordEncoder.encode("warga123"))
                    .role(UserRole.WARGA)
                    .rt("01")
                    .rw("05")
                    .phone("08987654321")
                    .address("Jl. Melati No. 12")
                    .isActive(true)
                    .build();
            return userRepository.save(newWarga);
        });

        if (announcementRepository.count() == 0) {
            announcementRepository.save(Announcement.builder()
                    .title("Kerja Bakti Massal Kebersihan Lingkungan RT 01")
                    .content("Diberitahukan kepada seluruh warga RT 01/RW 05 untuk mengikuti kegiatan kerja bakti pembersihan saluran drainase pada Minggu pagi jam 07.00 WIB.")
                    .isPinned(true)
                    .author(admin)
                    .build());

            announcementRepository.save(Announcement.builder()
                    .title("Iuran Kebersihan & Pos Satpam Bulan Ini")
                    .content("Pembayaran iuran bulanan RT dapat disetorkan kepada bendahara RT paling lambat tanggal 15 bulan ini.")
                    .isPinned(false)
                    .author(admin)
                    .build());
        }

        if (eventRepository.count() == 0) {
            eventRepository.save(Event.builder()
                    .title("Kerja Bakti & Pembersihan Drainase")
                    .description("Kerja bakti bersama warga RT 01 untuk mengantisipasi musim hujan. Harap membawa cangkul dan kantong sampah.")
                    .eventDate(LocalDateTime.now().plusDays(3).withHour(7).withMinute(0))
                    .location("Balai RT 01 / Lapangan Utama")
                    .imageUrl("https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?auto=format&fit=crop&w=800&q=80")
                    .organizer(admin)
                    .build());

            eventRepository.save(Event.builder()
                    .title("Rapat Pengurus RT & Penyuluhan Keamanan")
                    .description("Rapat koordinasi pengurus RT 01 bersama warga mengenai jam malam dan siskamling.")
                    .eventDate(LocalDateTime.now().plusDays(7).withHour(19).withMinute(30))
                    .location("Rumah Ketua RT 01")
                    .imageUrl("https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?auto=format&fit=crop&w=800&q=80")
                    .organizer(admin)
                    .build());
        }

        if (reportRepository.count() == 0) {
            reportRepository.save(Report.builder()
                    .title("Lampu Jalan Padam di Depan Rumah No. 12")
                    .description("Lampu jalan di dekat pertigaan RT 01 padam sejak kemarin malam, mohon dilakukan perbaikan.")
                    .category(ReportCategory.LAMPU_MATI)
                    .status(ReportStatus.PENDING)
                    .location("Jl. Melati RT 01")
                    .rt("01")
                    .rw("05")
                    .reporter(warga)
                    .build());

            reportRepository.save(Report.builder()
                    .title("Tumpukan Sampah Liar di Pinggir Jalan")
                    .description("Ada tumpukan sampah yang belum diangkut di dekat pos siskamling.")
                    .category(ReportCategory.SAMPAH)
                    .status(ReportStatus.DIPROSES)
                    .location("Pos Siskamling RT 01")
                    .rt("01")
                    .rw("05")
                    .adminNotes("Sudah dijadwalkan pengangkutan oleh petugas besok pagi.")
                    .reporter(warga)
                    .build());
        }
    }
}
