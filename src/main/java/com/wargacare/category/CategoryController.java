package com.wargacare.category;

import com.wargacare.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Endpoint CRUD manajemen kategori kegiatan dan laporan")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Daftar kategori", description = "Mendapatkan daftar kategori (opsional filter type=EVENT/REPORT)")
    public ResponseEntity<ApiResponse<List<Category>>> getCategories(
            @RequestParam(required = false) String type) {
        List<Category> list = categoryService.getCategories(type);
        return ResponseEntity.ok(ApiResponse.success("Daftar kategori berhasil diambil", list));
    }

    @PostMapping
    @Operation(summary = "Buat kategori baru", description = "Admin RT membuat kategori kegiatan/laporan baru")
    public ResponseEntity<ApiResponse<Category>> create(@RequestBody Category category) {
        Category created = categoryService.create(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Kategori berhasil dibuat", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update kategori", description = "Admin RT mengedit nama/deskripsi kategori")
    public ResponseEntity<ApiResponse<Category>> update(
            @PathVariable Long id,
            @RequestBody Category category) {
        Category updated = categoryService.update(id, category);
        return ResponseEntity.ok(ApiResponse.success("Kategori berhasil diperbarui", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus kategori", description = "Admin RT menghapus kategori")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Kategori berhasil dihapus"));
    }
}
