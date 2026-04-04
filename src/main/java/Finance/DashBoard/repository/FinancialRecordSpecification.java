package Finance.DashBoard.repository;

import Finance.DashBoard.model.FinancialRecord;
import Finance.DashBoard.model.RecordType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class FinancialRecordSpecification {

    private FinancialRecordSpecification() {
    }

    public static Specification<FinancialRecord> filtered(
            RecordType type,
            String category,
            LocalDate dateFrom,
            LocalDate dateTo,
            Long createdByUserId
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase()));
            }
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), dateTo));
            }
            if (createdByUserId != null) {
                predicates.add(cb.equal(root.get("createdBy").get("id"), createdByUserId));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
