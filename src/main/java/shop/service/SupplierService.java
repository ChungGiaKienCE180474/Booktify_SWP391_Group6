package shop.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import shop.domain.Supplier;
import shop.domain.dto.SupplierDTO;
import shop.repository.SupplierRepository;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAllByOrderByActiveDescIdDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SupplierDTO> getAllActive() {
        return supplierRepository.findAllByActiveTrueOrderByIdDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SupplierDTO> getActiveSuppliers() {
        return getAllActive();
    }

    public List<SupplierDTO> getAllHidden() {
        return supplierRepository.findAllByActiveFalseOrderByIdDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SupplierDTO> searchSuppliers(String keyword, String status) {
        List<SupplierDTO> suppliers;

        if ("active".equalsIgnoreCase(status)) {
            suppliers = getAllActive();
        } else if ("hidden".equalsIgnoreCase(status)) {
            suppliers = getAllHidden();
        } else {
            suppliers = getAllSuppliers();
        }

        if (!StringUtils.hasText(keyword)) {
            return suppliers;
        }

        String searchValue = keyword.trim().toLowerCase();

        return suppliers.stream()
                .filter(supplier ->
                        containsIgnoreCase(supplier.getSupplierName(), searchValue)
                                || containsIgnoreCase(supplier.getContactPerson(), searchValue)
                                || containsIgnoreCase(supplier.getEmail(), searchValue)
                                || containsIgnoreCase(supplier.getPhone(), searchValue)
                                || containsIgnoreCase(supplier.getAddress(), searchValue)
                )
                .collect(Collectors.toList());
    }

    public Optional<SupplierDTO> getById(Long id) {
        return supplierRepository.findById(id).map(this::toDto);
    }

    public boolean isDuplicateName(String supplierName) {
        String normalizedName = normalizeRequiredName(supplierName);
        return supplierRepository.existsBySupplierNameIgnoreCase(normalizedName);
    }

    public boolean isDuplicateNameForUpdate(String supplierName, Long id) {
        String normalizedName = normalizeRequiredName(supplierName);
        return supplierRepository.existsBySupplierNameIgnoreCaseAndIdNot(normalizedName, id);
    }

    @Transactional
    public SupplierDTO create(SupplierDTO dto) {
        String normalizedName = normalizeRequiredName(dto.getSupplierName());

        if (supplierRepository.existsBySupplierNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Supplier name already exists.");
        }

        Supplier supplier = toEntity(dto);
        supplier.setSupplierName(normalizedName);
        supplier.setActive(true);

        return toDto(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierDTO update(Long id, SupplierDTO dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found."));

        if (!supplier.isActive()) {
            throw new IllegalStateException("Hidden supplier must be restored before editing.");
        }

        String normalizedName = normalizeRequiredName(dto.getSupplierName());

        if (supplierRepository.existsBySupplierNameIgnoreCaseAndIdNot(normalizedName, id)) {
            throw new IllegalArgumentException("Supplier name already exists.");
        }

        supplier.setSupplierName(normalizedName);
        supplier.setContactPerson(normalizeNullable(dto.getContactPerson()));
        supplier.setEmail(normalizeNullable(dto.getEmail()));
        supplier.setPhone(normalizeNullable(dto.getPhone()));
        supplier.setAddress(normalizeNullable(dto.getAddress()));
        supplier.setDescription(normalizeNullable(dto.getDescription()));

        return toDto(supplierRepository.save(supplier));
    }

    @Transactional
    public void remove(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found."));

        if (!supplier.isActive()) {
            throw new IllegalStateException("Supplier is already hidden.");
        }

        supplier.setActive(false);
        supplierRepository.save(supplier);
    }

    @Transactional
    public void restore(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found."));

        if (supplier.isActive()) {
            throw new IllegalStateException("Supplier is already active.");
        }

        supplier.setActive(true);
        supplierRepository.save(supplier);
    }

    private SupplierDTO toDto(Supplier supplier) {
        SupplierDTO dto = new SupplierDTO();

        dto.setId(supplier.getId());
        dto.setSupplierName(supplier.getSupplierName());
        dto.setContactPerson(supplier.getContactPerson());
        dto.setEmail(supplier.getEmail());
        dto.setPhone(supplier.getPhone());
        dto.setAddress(supplier.getAddress());
        dto.setDescription(supplier.getDescription());
        dto.setActive(supplier.isActive());
        dto.setCreatedAt(supplier.getCreatedAt());
        dto.setUpdatedAt(supplier.getUpdatedAt());

        return dto;
    }

    private Supplier toEntity(SupplierDTO dto) {
        Supplier supplier = new Supplier();

        supplier.setSupplierName(normalizeRequiredName(dto.getSupplierName()));
        supplier.setContactPerson(normalizeNullable(dto.getContactPerson()));
        supplier.setEmail(normalizeNullable(dto.getEmail()));
        supplier.setPhone(normalizeNullable(dto.getPhone()));
        supplier.setAddress(normalizeNullable(dto.getAddress()));
        supplier.setDescription(normalizeNullable(dto.getDescription()));
        supplier.setActive(true);

        return supplier;
    }

    private boolean containsIgnoreCase(String source, String searchValue) {
        return source != null && source.toLowerCase().contains(searchValue);
    }

    private String normalizeRequiredName(String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("Supplier name is required.");
        }

        return value.trim();
    }

    private String normalizeNullable(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}