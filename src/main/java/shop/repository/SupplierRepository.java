package shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import shop.domain.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findAllByOrderByActiveDescIdDesc();

    List<Supplier> findAllByActiveTrueOrderByIdDesc();

    List<Supplier> findAllByActiveFalseOrderByIdDesc();

    boolean existsBySupplierNameIgnoreCase(String supplierName);

    boolean existsBySupplierNameIgnoreCaseAndIdNot(String supplierName, Long id);
}