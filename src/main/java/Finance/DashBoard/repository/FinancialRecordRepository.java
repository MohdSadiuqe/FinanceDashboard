package Finance.DashBoard.repository;

import Finance.DashBoard.model.FinancialRecord;
import Finance.DashBoard.model.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>, JpaSpecificationExecutor<FinancialRecord> {

    List<FinancialRecord> findByType(RecordType type);

    List<FinancialRecord> findByCategoryIgnoreCase(String category);

    List<FinancialRecord> findByDateBetween(LocalDate start, LocalDate end);

    List<FinancialRecord> findByCreatedById(Long userId);

    @EntityGraph(attributePaths = "createdBy")
    @Override
    Page<FinancialRecord> findAll(Specification<FinancialRecord> spec, Pageable pageable);

    @EntityGraph(attributePaths = "createdBy")
    List<FinancialRecord> findTop30ByOrderByDateDescIdDesc();
}
