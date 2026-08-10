package com.wargacare.category;

import com.wargacare.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> getCategories(String type) {
        if (type != null && !type.isBlank()) {
            return categoryRepository.findByTypeOrderByNameAsc(type.toUpperCase());
        }
        return categoryRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public Category create(Category category) {
        if (category.getType() != null) {
            category.setType(category.getType().toUpperCase());
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, Category request) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori tidak ditemukan"));

        if (request.getName() != null && !request.getName().isBlank()) {
            existing.setName(request.getName());
        }
        if (request.getType() != null && !request.getType().isBlank()) {
            existing.setType(request.getType().toUpperCase());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }

        return categoryRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori tidak ditemukan"));
        categoryRepository.delete(category);
    }
}
