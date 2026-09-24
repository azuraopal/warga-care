package com.wargacare.kas;

import com.wargacare.common.ResourceNotFoundException;
import com.wargacare.kas.dto.*;
import com.wargacare.user.User;
import com.wargacare.user.UserRepository;
import com.wargacare.user.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KasService {

    private final KasTransactionRepository kasTransactionRepository;
    private final IuranWargaRepository iuranWargaRepository;
    private final WargaMasterRepository wargaMasterRepository;
    private final UserRepository userRepository;

    public KasService(KasTransactionRepository kasTransactionRepository,
                      IuranWargaRepository iuranWargaRepository,
                      WargaMasterRepository wargaMasterRepository,
                      UserRepository userRepository) {
        this.kasTransactionRepository = kasTransactionRepository;
        this.iuranWargaRepository = iuranWargaRepository;
        this.wargaMasterRepository = wargaMasterRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
    }

    private String getTargetRt(User user) {
        if (user.getRt() != null && !user.getRt().isBlank()) {
            return user.getRt();
        }
        return "RT 01";
    }
    private String getCurrentIsoWeek() {
        LocalDate now = LocalDate.now();
        int week = now.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int year = now.get(java.time.temporal.IsoFields.WEEK_BASED_YEAR);
        return String.format("%d-W%02d", year, week);
    }

    public KasSummaryResponse getSummary() {
        User currentUser = getCurrentUser();
        String targetRt = getTargetRt(currentUser);

        BigDecimal totalIncome = kasTransactionRepository.sumAmountByRtAndType(targetRt, KasType.INCOME);
        BigDecimal totalExpense = kasTransactionRepository.sumAmountByRtAndType(targetRt, KasType.EXPENSE);
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;
        BigDecimal currentBalance = totalIncome.subtract(totalExpense);

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        List<KasTransaction> allTxs = kasTransactionRepository.findByRtOrderByDateDescCreatedAtDesc(targetRt);

        BigDecimal monthIncome = BigDecimal.ZERO;
        BigDecimal monthExpense = BigDecimal.ZERO;
        for (KasTransaction t : allTxs) {
            LocalDate d = t.getDate();
            if (d != null && d.getYear() == currentYear && d.getMonthValue() == currentMonth) {
                BigDecimal amt = t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO;
                if (t.getType() == KasType.INCOME) {
                    monthIncome = monthIncome.add(amt);
                } else if (t.getType() == KasType.EXPENSE) {
                    monthExpense = monthExpense.add(amt);
                }
            }
        }

        return KasSummaryResponse.builder()
                .rt(targetRt)
                .currentBalance(currentBalance)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .monthIncome(monthIncome)
                .monthExpense(monthExpense)
                .transactionCount(allTxs.size())
                .build();
    }

    public List<KasTransactionResponse> getTransactions(KasType type, String category, String search) {
        User currentUser = getCurrentUser();
        String targetRt = getTargetRt(currentUser);

        List<KasTransaction> list = kasTransactionRepository.filterTransactions(
                targetRt,
                type,
                (category != null && !category.isBlank() && !category.equals("ALL")) ? category : null,
                (search != null && !search.isBlank()) ? search : null
        );

        return list.stream()
                .map(KasTransactionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public KasTransactionResponse createTransaction(CreateKasTransactionRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN_RT) {
            throw new AccessDeniedException("Hanya Admin RT yang dapat mencatat transaksi Kas RT");
        }

        String targetRt = getTargetRt(currentUser);

        KasTransaction tx = KasTransaction.builder()
                .rt(targetRt)
                .type(request.getType())
                .title(request.getTitle())
                .amount(request.getAmount())
                .category(request.getCategory())
                .date(request.getDate())
                .recordedBy(currentUser.getFullName() + " (Admin " + targetRt + ")")
                .notes(request.getNotes())
                .proofUrl(request.getProofUrl())
                .build();

        KasTransaction saved = kasTransactionRepository.save(tx);
        return KasTransactionResponse.fromEntity(saved);
    }

    @Transactional
    public KasTransactionResponse updateTransaction(Long id, CreateKasTransactionRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN_RT) {
            throw new AccessDeniedException("Hanya Admin RT yang dapat mengubah transaksi Kas RT");
        }

        String targetRt = getTargetRt(currentUser);
        KasTransaction tx = kasTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaksi Kas tidak ditemukan"));

        if (!tx.getRt().equalsIgnoreCase(targetRt)) {
            throw new AccessDeniedException("Anda tidak berhak mengedit transaksi dari RT lain");
        }

        tx.setType(request.getType());
        tx.setTitle(request.getTitle());
        tx.setAmount(request.getAmount());
        tx.setCategory(request.getCategory());
        tx.setDate(request.getDate());
        tx.setNotes(request.getNotes());
        if (request.getProofUrl() != null) tx.setProofUrl(request.getProofUrl());

        KasTransaction updated = kasTransactionRepository.save(tx);
        return KasTransactionResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN_RT) {
            throw new AccessDeniedException("Hanya Admin RT yang dapat menghapus transaksi Kas RT");
        }

        String targetRt = getTargetRt(currentUser);
        KasTransaction tx = kasTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaksi Kas tidak ditemukan"));

        if (!tx.getRt().equalsIgnoreCase(targetRt)) {
            throw new AccessDeniedException("Anda tidak berhak menghapus transaksi dari RT lain");
        }

        kasTransactionRepository.delete(tx);
    }

    @Transactional
    public WargaMasterResponse registerWarga(RegisterWargaRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN_RT) {
            throw new AccessDeniedException("Hanya Admin RT yang dapat mendaftarkan warga master");
        }

        String targetRt = getTargetRt(currentUser);
        WargaCategory cat = request.getCategory() != null ? request.getCategory() : WargaCategory.PEKERJA;

        Optional<WargaMaster> existing = wargaMasterRepository.findByRtAndWargaName(targetRt, request.getWargaName());
        WargaMaster master;
        if (existing.isPresent()) {
            master = existing.get();
            master.setBlockAddress(request.getBlockAddress());
            master.setCategory(cat);
        } else {
            master = WargaMaster.builder()
                    .wargaName(request.getWargaName())
                    .blockAddress(request.getBlockAddress())
                    .rt(targetRt)
                    .category(cat)
                    .build();
        }

        WargaMaster saved = wargaMasterRepository.save(master);
        return WargaMasterResponse.fromEntity(saved);
    }

    public List<WargaMasterResponse> getMasterWarga() {
        User currentUser = getCurrentUser();
        String targetRt = getTargetRt(currentUser);
        List<WargaMaster> list = wargaMasterRepository.findByRtOrderByWargaNameAsc(targetRt);
        return list.stream().map(WargaMasterResponse::fromEntity).collect(Collectors.toList());
    }


    public List<WeeklyIuranStatusResponse> getIuranWeekly(String periodWeek) {
        User currentUser = getCurrentUser();
        String targetRt = getTargetRt(currentUser);
        String targetWeek = (periodWeek != null && !periodWeek.isBlank()) ? periodWeek : getCurrentIsoWeek();

        List<WargaMaster> masterList = wargaMasterRepository.findByRtOrderByWargaNameAsc(targetRt);
        List<IuranWarga> paidWeeklyList = iuranWargaRepository.findByRtAndPeriodWeek(targetRt, targetWeek);

        Map<Long, IuranWarga> paidByMasterId = new HashMap<>();
        Map<String, IuranWarga> paidByName = new HashMap<>();
        for (IuranWarga iw : paidWeeklyList) {
            if (iw.getWargaMasterId() != null) {
                paidByMasterId.put(iw.getWargaMasterId(), iw);
            }
            if (iw.getWargaName() != null) {
                paidByName.put(iw.getWargaName().toLowerCase(), iw);
            }
        }

        List<WeeklyIuranStatusResponse> result = new ArrayList<>();
        for (WargaMaster wm : masterList) {
            IuranWarga paidRecord = paidByMasterId.get(wm.getId());
            if (paidRecord == null) {
                paidRecord = paidByName.get(wm.getWargaName().toLowerCase());
            }

            boolean isPaid = paidRecord != null && Boolean.TRUE.equals(paidRecord.getIsPaid());
            BigDecimal weeklyRate = wm.getCategory() != null ? wm.getCategory().getWeeklyDuesRate() : new BigDecimal("5000.00");
            String categoryLabel = wm.getCategory() != null ? wm.getCategory().getLabel() : "Sudah Bekerja";

            result.add(WeeklyIuranStatusResponse.builder()
                    .wargaMasterId(wm.getId())
                    .wargaName(wm.getWargaName())
                    .blockAddress(wm.getBlockAddress())
                    .rt(targetRt)
                    .category(wm.getCategory())
                    .categoryLabel(categoryLabel)
                    .weeklyDuesRate(weeklyRate)
                    .periodWeek(targetWeek)
                    .isPaid(isPaid)
                    .paidDate(isPaid ? paidRecord.getPaidDate() : null)
                    .paymentMethod(isPaid ? paidRecord.getPaymentMethod() : null)
                    .recordedBy(isPaid ? paidRecord.getRecordedBy() : null)
                    .totalArrearsWeeks(isPaid ? 0 : 1)
                    .totalArrearsAmount(isPaid ? BigDecimal.ZERO : weeklyRate)
                    .build());
        }

        return result;
    }

    @Transactional
    public WeeklyIuranStatusResponse payIuranWeekly(PayWeeklyIuranRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN_RT) {
            throw new AccessDeniedException("Hanya Admin RT yang dapat mencatat pembayaran Iuran Warga");
        }

        String targetRt = getTargetRt(currentUser);
        String targetWeek = (request.getPeriodWeek() != null && !request.getPeriodWeek().isBlank()) ? request.getPeriodWeek() : getCurrentIsoWeek();

        WargaMaster master;
        if (request.getWargaMasterId() != null) {
            master = wargaMasterRepository.findById(request.getWargaMasterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Data Warga Master tidak ditemukan"));
            if (!master.getRt().equalsIgnoreCase(targetRt)) {
                throw new AccessDeniedException("Data warga bukan bagian dari " + targetRt);
            }
        } else {
            String nameToUse = request.getWargaName();
            if (nameToUse == null || nameToUse.isBlank()) {
                throw new IllegalArgumentException("Nama warga atau Warga Master ID wajib diisi");
            }
            Optional<WargaMaster> existing = wargaMasterRepository.findByRtAndWargaName(targetRt, nameToUse);
            if (existing.isPresent()) {
                master = existing.get();
            } else {
                master = WargaMaster.builder()
                        .wargaName(nameToUse)
                        .blockAddress(request.getBlockAddress())
                        .rt(targetRt)
                        .category(WargaCategory.PEKERJA)
                        .build();
                master = wargaMasterRepository.save(master);
            }
        }

        WargaCategory category = master.getCategory() != null ? master.getCategory() : WargaCategory.PEKERJA;
        BigDecimal duesAmount = request.getAmount() != null ? request.getAmount() : category.getWeeklyDuesRate();
        if (duesAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Nominal iuran harus lebih besar dari 0");
        }

        Optional<IuranWarga> existingIuran = iuranWargaRepository.findByRtAndWargaMasterIdAndPeriodWeek(targetRt, master.getId(), targetWeek);
        if (existingIuran.isEmpty() && master.getWargaName() != null) {
            existingIuran = iuranWargaRepository.findByRtAndWargaNameAndPeriodWeek(targetRt, master.getWargaName(), targetWeek);
        }
        if (existingIuran.isPresent() && Boolean.TRUE.equals(existingIuran.get().getIsPaid())) {
            throw new IllegalStateException("Iuran mingguan warga " + master.getWargaName() + " untuk periode " + targetWeek + " sudah tercatat lunas.");
        }

        String currentMonth = LocalDate.now().toString().substring(0, 7);
        IuranWarga iuran;
        if (existingIuran.isPresent()) {
            iuran = existingIuran.get();
            iuran.setWargaMasterId(master.getId());
            iuran.setIsPaid(true);
            iuran.setPaidDate(LocalDate.now());
            iuran.setAmount(duesAmount);
            iuran.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "Tunai");
            iuran.setRecordedBy(currentUser.getFullName() + " (Admin " + targetRt + ")");
        } else {
            iuran = IuranWarga.builder()
                    .wargaMasterId(master.getId())
                    .wargaName(master.getWargaName())
                    .blockAddress(master.getBlockAddress())
                    .rt(targetRt)
                    .periodMonth(currentMonth)
                    .periodWeek(targetWeek)
                    .amount(duesAmount)
                    .isPaid(true)
                    .paidDate(LocalDate.now())
                    .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "Tunai")
                    .recordedBy(currentUser.getFullName() + " (Admin " + targetRt + ")")
                    .build();
        }
        iuranWargaRepository.save(iuran);

        KasTransaction incomeTx = KasTransaction.builder()
                .rt(targetRt)
                .type(KasType.INCOME)
                .title("Iuran Mingguan " + master.getWargaName() + " (" + targetWeek + ")")
                .amount(duesAmount)
                .category("Iuran Mingguan")
                .date(LocalDate.now())
                .recordedBy(currentUser.getFullName() + " (Admin " + targetRt + ")")
                .notes("Kategori: " + category.getLabel() + " | Metode: " + (request.getPaymentMethod() != null ? request.getPaymentMethod() : "Tunai"))
                .build();
        kasTransactionRepository.save(incomeTx);

        return WeeklyIuranStatusResponse.builder()
                .wargaMasterId(master.getId())
                .wargaName(master.getWargaName())
                .blockAddress(master.getBlockAddress())
                .rt(targetRt)
                .category(category)
                .categoryLabel(category.getLabel())
                .weeklyDuesRate(duesAmount)
                .periodWeek(targetWeek)
                .isPaid(true)
                .paidDate(LocalDate.now())
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "Tunai")
                .recordedBy(currentUser.getFullName() + " (Admin " + targetRt + ")")
                .totalArrearsWeeks(0)
                .totalArrearsAmount(BigDecimal.ZERO)
                .build();
    }

    public List<IuranWargaResponse> getIuranWarga(String periodMonth) {
        User currentUser = getCurrentUser();
        String targetRt = getTargetRt(currentUser);

        List<IuranWarga> list = iuranWargaRepository.findByRtOrderByWargaNameAsc(targetRt);
        if (periodMonth != null && !periodMonth.isBlank()) {
            list = list.stream()
                    .filter(i -> periodMonth.equals(i.getPeriodMonth()))
                    .collect(Collectors.toList());
        }

        return list.stream()
                .map(IuranWargaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public IuranWargaResponse payIuran(PayIuranRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN_RT) {
            throw new AccessDeniedException("Hanya Admin RT yang dapat mencatat pembayaran Iuran Warga");
        }

        String targetRt = getTargetRt(currentUser);
        BigDecimal amount = request.getAmount() != null ? request.getAmount() : new BigDecimal("50000.00");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Nominal iuran harus lebih besar dari 0");
        }
        String period = request.getPeriodMonth() != null ? request.getPeriodMonth() : LocalDate.now().toString().substring(0, 7);

        Optional<IuranWarga> existing = iuranWargaRepository.findByRtAndWargaNameAndPeriodMonth(targetRt, request.getWargaName(), period);
        if (existing.isPresent() && Boolean.TRUE.equals(existing.get().getIsPaid())) {
            throw new IllegalStateException("Iuran bulanan warga " + request.getWargaName() + " untuk periode " + period + " sudah tercatat lunas.");
        }

        IuranWarga iuran;
        if (existing.isPresent()) {
            iuran = existing.get();
            iuran.setIsPaid(true);
            iuran.setPaidDate(LocalDate.now());
            iuran.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "Tunai");
            iuran.setAmount(amount);
            iuran.setRecordedBy(currentUser.getFullName() + " (Admin " + targetRt + ")");
        } else {
            iuran = IuranWarga.builder()
                    .wargaName(request.getWargaName())
                    .blockAddress(request.getBlockAddress())
                    .rt(targetRt)
                    .periodMonth(period)
                    .amount(amount)
                    .isPaid(true)
                    .paidDate(LocalDate.now())
                    .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "Tunai")
                    .recordedBy(currentUser.getFullName() + " (Admin " + targetRt + ")")
                    .build();
        }

        IuranWarga saved = iuranWargaRepository.save(iuran);

        KasTransaction incomeTx = KasTransaction.builder()
                .rt(targetRt)
                .type(KasType.INCOME)
                .title("Iuran Bulanan " + request.getWargaName() + " (" + period + ")")
                .amount(amount)
                .category("Iuran Bulanan")
                .date(LocalDate.now())
                .recordedBy(currentUser.getFullName() + " (Admin " + targetRt + ")")
                .notes("Metode: " + (request.getPaymentMethod() != null ? request.getPaymentMethod() : "Tunai") + " | " + (request.getBlockAddress() != null ? request.getBlockAddress() : ""))
                .build();

        kasTransactionRepository.save(incomeTx);

        return IuranWargaResponse.fromEntity(saved);
    }
}
