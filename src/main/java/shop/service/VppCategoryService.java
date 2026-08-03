package shop.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import shop.domain.VppCategory;
import shop.domain.dto.VppCategoryDTO;
import shop.repository.VppCategoryRepository;

@Service
public class VppCategoryService {

    // Starter categories seeded once at startup — not an exclusive list,
    // admins can add/hide/rename freely afterwards.
    private static final List<String> DEFAULT_VPP_CATEGORIES = List.of(
            "Pens",
            "Note-taking Tools",
            "Other"
    );

    private final VppCategoryRepository categoryRepository;

    public VppCategoryService(VppCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostConstruct
    public void initDefaultCategories() {
        ensureDefaultCategories();
    }

    public List<VppCategoryDTO> getAllCategories() {
        return categoryRepository.findAllByOrderByActiveDescNameAsc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<VppCategoryDTO> getActiveCategories() {
        return categoryRepository.findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public VppCategoryDTO getById(Long id) {
        VppCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("VPP category not found."));

        return toDto(category);
    }

    @Transactional
    public VppCategoryDTO create(VppCategoryDTO dto) {
        String normalizedName = normalizeName(dto.getName());

        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("VPP category already exists.");
        }

        VppCategory category = new VppCategory();

        category.setName(normalizedName);
        category.setDescription(normalizeNullable(dto.getDescription()));
        category.setActive(true);

        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public VppCategoryDTO update(Long id, VppCategoryDTO dto) {
        VppCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("VPP category not found."));

        String normalizedName = normalizeName(dto.getName());

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
            throw new IllegalArgumentException("VPP category already exists.");
        }

        category.setName(normalizedName);
        category.setDescription(normalizeNullable(dto.getDescription()));

        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public void hide(Long id) {
        VppCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("VPP category not found."));

        category.setActive(false);

        categoryRepository.save(category);
    }

    @Transactional
    public void restore(Long id) {
        VppCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("VPP category not found."));

        category.setActive(true);

        categoryRepository.save(category);
    }

    @Transactional
    public VppCategory findOrCreateByName(String rawName) {
        String normalizedName = normalizeName(rawName);

        return categoryRepository.findByNameIgnoreCase(normalizedName)
                .orElseGet(() -> {
                    VppCategory category = new VppCategory();

                    category.setName(normalizedName);
                    category.setDescription(null);
                    category.setActive(true);

                    return categoryRepository.save(category);
                });
    }

    @Transactional
    public void ensureDefaultCategories() {
        for (String categoryName : DEFAULT_VPP_CATEGORIES) {
            VppCategory category = categoryRepository.findByNameIgnoreCase(categoryName)
                    .orElse(null);

            if (category == null) {
                VppCategory newCategory = new VppCategory();

                newCategory.setName(categoryName);
                newCategory.setActive(true);

                categoryRepository.save(newCategory);
            } else if (!category.isActive()) {
                category.setActive(true);
                categoryRepository.save(category);
            }
        }
    }

    // Free-form name now (only non-blank required) — Pens / Note-taking Tools /
    // Other are just starter categories seeded by ensureDefaultCategories(),
    // not an exclusive list anymore.
    private String normalizeName(String rawName) {
        if (!StringUtils.hasText(rawName)) {
            throw new IllegalArgumentException("Category is required.");
        }

        return rawName.trim();
    }

    private String normalizeNullable(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private VppCategoryDTO toDto(VppCategory category) {
        return new VppCategoryDTO()
                .setId(category.getId())
                .setName(category.getName())
                .setDescription(category.getDescription())
                .setActive(category.isActive())
                .setCreatedAt(category.getCreatedAt())
                .setUpdatedAt(category.getUpdatedAt());
    }
}
