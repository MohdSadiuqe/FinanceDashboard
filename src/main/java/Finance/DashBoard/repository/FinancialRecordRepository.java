package Finance.DashBoard.repository;

import Finance.DashBoard.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    // Find by type (INCOME / EXPENSE)
    List<FinancialRecord> findByType(RecordType type);

    // Find by category
    List<FinancialRecord> findByCategory(String category);

    // Filter by date range
    List<FinancialRecord> findByDateBetween(LocalDate start, LocalDate end);
}