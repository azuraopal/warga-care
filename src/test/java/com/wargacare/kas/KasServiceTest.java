package com.wargacare.kas;

import com.wargacare.common.ResourceNotFoundException;
import com.wargacare.kas.dto.*;
import com.wargacare.user.User;
import com.wargacare.user.UserRepository;
import com.wargacare.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KasService Unit Tests")
class KasServiceTest {

    @Mock
    private KasTransactionRepository kasTransactionRepository;

    @Mock
    private IuranWargaRepository iuranWargaRepository;

    @Mock
    private WargaMasterRepository wargaMasterRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private KasService kasService;

    private User adminUser;
    private WargaMaster wargaMaster;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(1L)
                .email("admin@wargacare.id")
                .fullName("Pak RT")
                .role(UserRole.ADMIN_RT)
                .rt("RT 01")
                .rw("RW 05")
                .isActive(true)
                .build();

        wargaMaster = WargaMaster.builder()
                .id(10L)
                .wargaName("Budi")
                .blockAddress("Blok A1")
                .rt("RT 01")
                .category(WargaCategory.PEKERJA)
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(adminUser.getEmail(), null)
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("payIuranWeekly() - Sukses mencatat pembayaran jika belum lunas")
    void payIuranWeekly_Success() {
        when(userRepository.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUser));
        when(wargaMasterRepository.findById(10L)).thenReturn(Optional.of(wargaMaster));
        when(iuranWargaRepository.findByRtAndWargaMasterIdAndPeriodWeek("RT 01", 10L, "2026-W33"))
                .thenReturn(Optional.empty());

        PayWeeklyIuranRequest request = PayWeeklyIuranRequest.builder()
                .wargaMasterId(10L)
                .periodWeek("2026-W33")
                .amount(new BigDecimal("5000.00"))
                .paymentMethod("Tunai")
                .build();

        WeeklyIuranStatusResponse response = kasService.payIuranWeekly(request);

        assertThat(response).isNotNull();
        assertThat(response.getIsPaid()).isTrue();
        assertThat(response.getWeeklyDuesRate()).isEqualByComparingTo("5000.00");

        verify(iuranWargaRepository).save(any(IuranWarga.class));
        verify(kasTransactionRepository).save(any(KasTransaction.class));
    }

    @Test
    @DisplayName("payIuranWeekly() - Gagal jika iuran mingguan sudah lunas (mencegah double count)")
    void payIuranWeekly_AlreadyPaid_ThrowsException() {
        when(userRepository.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUser));
        when(wargaMasterRepository.findById(10L)).thenReturn(Optional.of(wargaMaster));

        IuranWarga paidIuran = IuranWarga.builder()
                .id(100L)
                .wargaMasterId(10L)
                .isPaid(true)
                .periodWeek("2026-W33")
                .build();

        when(iuranWargaRepository.findByRtAndWargaMasterIdAndPeriodWeek("RT 01", 10L, "2026-W33"))
                .thenReturn(Optional.of(paidIuran));

        PayWeeklyIuranRequest request = PayWeeklyIuranRequest.builder()
                .wargaMasterId(10L)
                .periodWeek("2026-W33")
                .amount(new BigDecimal("5000.00"))
                .build();

        assertThatThrownBy(() -> kasService.payIuranWeekly(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sudah tercatat lunas");

        verify(kasTransactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("payIuranWeekly() - Gagal jika mengakses warga dari RT lain")
    void payIuranWeekly_CrossRt_ThrowsException() {
        when(userRepository.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUser));

        WargaMaster otherRtWarga = WargaMaster.builder()
                .id(20L)
                .wargaName("Siti")
                .rt("RT 02")
                .category(WargaCategory.PEKERJA)
                .build();

        when(wargaMasterRepository.findById(20L)).thenReturn(Optional.of(otherRtWarga));

        PayWeeklyIuranRequest request = PayWeeklyIuranRequest.builder()
                .wargaMasterId(20L)
                .periodWeek("2026-W33")
                .build();

        assertThatThrownBy(() -> kasService.payIuranWeekly(request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("bukan bagian dari RT 01");

        verify(iuranWargaRepository, never()).save(any());
    }

    @Test
    @DisplayName("payIuranWeekly() - Gagal jika nominal negatif atau nol")
    void payIuranWeekly_NegativeAmount_ThrowsException() {
        when(userRepository.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUser));
        when(wargaMasterRepository.findById(10L)).thenReturn(Optional.of(wargaMaster));

        PayWeeklyIuranRequest request = PayWeeklyIuranRequest.builder()
                .wargaMasterId(10L)
                .periodWeek("2026-W33")
                .amount(new BigDecimal("-1000.00"))
                .build();

        assertThatThrownBy(() -> kasService.payIuranWeekly(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("lebih besar dari 0");
    }

    @Test
    @DisplayName("payIuran() - Gagal jika iuran bulanan sudah lunas")
    void payIuran_AlreadyPaid_ThrowsException() {
        when(userRepository.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUser));

        IuranWarga existingPaid = IuranWarga.builder()
                .id(50L)
                .wargaName("Budi")
                .isPaid(true)
                .periodMonth("2026-08")
                .build();

        when(iuranWargaRepository.findByRtAndWargaNameAndPeriodMonth("RT 01", "Budi", "2026-08"))
                .thenReturn(Optional.of(existingPaid));

        PayIuranRequest request = new PayIuranRequest();
        request.setWargaName("Budi");
        request.setPeriodMonth("2026-08");
        request.setAmount(new BigDecimal("50000.00"));

        assertThatThrownBy(() -> kasService.payIuran(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sudah tercatat lunas");

        verify(kasTransactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("getSummary() - Aman dari NullPointerException ketika transaksi belum ada")
    void getSummary_NullSumsHandledSafely() {
        when(userRepository.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUser));
        when(kasTransactionRepository.sumAmountByRtAndType("RT 01", KasType.INCOME)).thenReturn(null);
        when(kasTransactionRepository.sumAmountByRtAndType("RT 01", KasType.EXPENSE)).thenReturn(null);
        when(kasTransactionRepository.findByRtOrderByDateDescCreatedAtDesc("RT 01")).thenReturn(Collections.emptyList());

        KasSummaryResponse summary = kasService.getSummary();

        assertThat(summary).isNotNull();
        assertThat(summary.getCurrentBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getTotalIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getTotalExpense()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getMonthIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getMonthExpense()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("payIuranWeekly() - Tetap aman dan menggunakan default rate ketika kategori warga null")
    void payIuranWeekly_NullCategory_DefaultsSafely() {
        WargaMaster masterWithoutCategory = WargaMaster.builder()
                .id(15L)
                .wargaName("Andi")
                .rt("RT 01")
                .category(null)
                .build();

        when(userRepository.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUser));
        when(wargaMasterRepository.findById(15L)).thenReturn(Optional.of(masterWithoutCategory));
        when(iuranWargaRepository.findByRtAndWargaMasterIdAndPeriodWeek("RT 01", 15L, "2026-W33"))
                .thenReturn(Optional.empty());

        PayWeeklyIuranRequest request = PayWeeklyIuranRequest.builder()
                .wargaMasterId(15L)
                .periodWeek("2026-W33")
                .paymentMethod("Tunai")
                .build();

        WeeklyIuranStatusResponse response = kasService.payIuranWeekly(request);

        assertThat(response).isNotNull();
        assertThat(response.getWeeklyDuesRate()).isEqualByComparingTo(WargaCategory.PEKERJA.getWeeklyDuesRate());
        assertThat(response.getCategory()).isEqualTo(WargaCategory.PEKERJA);
    }

    @Test
    @DisplayName("payIuranWeekly() - Menemukan data pembayaran melalui fallback wargaName jika wargaMasterId belum terisi")
    void payIuranWeekly_FallbackByWargaName_AlreadyPaid() {
        when(userRepository.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUser));
        when(wargaMasterRepository.findById(10L)).thenReturn(Optional.of(wargaMaster));
        when(iuranWargaRepository.findByRtAndWargaMasterIdAndPeriodWeek("RT 01", 10L, "2026-W33"))
                .thenReturn(Optional.empty());

        IuranWarga legacyPaid = IuranWarga.builder()
                .id(200L)
                .wargaMasterId(null)
                .wargaName("Budi")
                .isPaid(true)
                .periodWeek("2026-W33")
                .build();

        when(iuranWargaRepository.findByRtAndWargaNameAndPeriodWeek("RT 01", "Budi", "2026-W33"))
                .thenReturn(Optional.of(legacyPaid));

        PayWeeklyIuranRequest request = PayWeeklyIuranRequest.builder()
                .wargaMasterId(10L)
                .periodWeek("2026-W33")
                .amount(new BigDecimal("5000.00"))
                .build();

        assertThatThrownBy(() -> kasService.payIuranWeekly(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sudah tercatat lunas");
    }
}
