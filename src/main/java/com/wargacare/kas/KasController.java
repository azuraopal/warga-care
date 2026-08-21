package com.wargacare.kas;

import com.wargacare.common.ApiResponse;
import com.wargacare.kas.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kas")
public class KasController {

    private final KasService kasService;

    public KasController(KasService kasService) {
        this.kasService = kasService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<KasSummaryResponse>> getSummary() {
        KasSummaryResponse summary = kasService.getSummary();
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil ringkasan kas", summary));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<KasTransactionResponse>>> getTransactions(
            @RequestParam(required = false) KasType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        List<KasTransactionResponse> list = kasService.getTransactions(type, category, search);
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil riwayat transaksi kas", list));
    }

    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse<KasTransactionResponse>> createTransaction(
            @Valid @RequestBody CreateKasTransactionRequest request) {
        KasTransactionResponse tx = kasService.createTransaction(request);
        return ResponseEntity.ok(ApiResponse.success("Berhasil mencatat transaksi kas baru", tx));
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<ApiResponse<KasTransactionResponse>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody CreateKasTransactionRequest request) {
        KasTransactionResponse tx = kasService.updateTransaction(id, request);
        return ResponseEntity.ok(ApiResponse.success("Berhasil memperbarui transaksi kas", tx));
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        kasService.deleteTransaction(id);
        return ResponseEntity.ok(ApiResponse.success("Berhasil menghapus transaksi kas", null));
    }

    @PostMapping("/warga")
    public ResponseEntity<ApiResponse<WargaMasterResponse>> registerWarga(
            @Valid @RequestBody RegisterWargaRequest request) {
        WargaMasterResponse res = kasService.registerWarga(request);
        return ResponseEntity.ok(ApiResponse.success("Berhasil mendaftarkan data warga master", res));
    }

    @GetMapping("/warga")
    public ResponseEntity<ApiResponse<List<WargaMasterResponse>>> getMasterWarga() {
        List<WargaMasterResponse> list = kasService.getMasterWarga();
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil data warga master", list));
    }

    @GetMapping("/iuran/weekly")
    public ResponseEntity<ApiResponse<List<WeeklyIuranStatusResponse>>> getIuranWeekly(
            @RequestParam(required = false) String periodWeek) {
        List<WeeklyIuranStatusResponse> list = kasService.getIuranWeekly(periodWeek);
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil matriks iuran mingguan", list));
    }

    @PostMapping("/iuran/pay-weekly")
    public ResponseEntity<ApiResponse<WeeklyIuranStatusResponse>> payIuranWeekly(
            @Valid @RequestBody PayWeeklyIuranRequest request) {
        WeeklyIuranStatusResponse res = kasService.payIuranWeekly(request);
        return ResponseEntity.ok(ApiResponse.success("Berhasil mencatat pembayaran iuran mingguan", res));
    }

    @GetMapping("/iuran")
    public ResponseEntity<ApiResponse<List<IuranWargaResponse>>> getIuranWarga(
            @RequestParam(required = false) String periodMonth) {
        List<IuranWargaResponse> list = kasService.getIuranWarga(periodMonth);
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil status iuran warga", list));
    }

    @PostMapping("/iuran/pay")
    public ResponseEntity<ApiResponse<IuranWargaResponse>> payIuran(
            @Valid @RequestBody PayIuranRequest request) {
        IuranWargaResponse iuran = kasService.payIuran(request);
        return ResponseEntity.ok(ApiResponse.success("Berhasil mencatat pembayaran iuran", iuran));
    }
}
