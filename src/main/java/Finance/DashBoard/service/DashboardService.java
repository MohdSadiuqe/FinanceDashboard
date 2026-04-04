package Finance.DashBoard.service;

import Finance.DashBoard.dto.FinancialSummaryDto;
import Finance.DashBoard.dto.PeriodTrendDto;
import Finance.DashBoard.dto.RecentRecordDto;
import Finance.DashBoard.model.FinancialRecord;
import Finance.DashBoard.model.RecordType;
import Finance.DashBoard.repository.FinancialRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final WeekFields ISO_WEEK = WeekFields.ISO;

    @Autowired
    private FinancialRecordRepository repo;

    public FinancialSummaryDto getFinancialSummary() {
        List<FinancialRecord> records = repo.findAll();

        double income = 0;
        double expense = 0;
        Map<String, Double> incomeByCategory = new HashMap<>();
        Map<String, Double> expenseByCategory = new HashMap<>();

        for (FinancialRecord r : records) {
            if (r.getType() == RecordType.INCOME) {
                income += r.getAmount();
                incomeByCategory.merge(r.getCategory(), r.getAmount(), Double::sum);
            } else {
                expense += r.getAmount();
                expenseByCategory.merge(r.getCategory(), r.getAmount(), Double::sum);
            }
        }

        return new FinancialSummaryDto(
                income,
                expense,
                income - expense,
                incomeByCategory,
                expenseByCategory
        );
    }

    public List<RecentRecordDto> getRecentActivity(int limit) {
        List<FinancialRecord> rows = repo.findTop30ByOrderByDateDescIdDesc();
        return rows.stream()
                .limit(Math.max(1, Math.min(limit, 50)))
                .map(this::toRecentDto)
                .collect(Collectors.toList());
    }

    public List<PeriodTrendDto> getTrends(String granularity) {
        List<FinancialRecord> records = repo.findAll();
        String g = granularity == null ? "MONTH" : granularity.trim().toUpperCase(Locale.ROOT);

        Map<String, double[]> buckets = new TreeMap<>();

        for (FinancialRecord r : records) {
            String key = switch (g) {
                case "WEEK" -> weekKey(r.getDate());
                case "MONTH" -> monthKey(r.getDate());
                default -> monthKey(r.getDate());
            };
            double[] agg = buckets.computeIfAbsent(key, k -> new double[3]);
            if (r.getType() == RecordType.INCOME) {
                agg[0] += r.getAmount();
            } else {
                agg[1] += r.getAmount();
            }
        }

        List<PeriodTrendDto> out = new ArrayList<>();
        for (Map.Entry<String, double[]> e : buckets.entrySet()) {
            double inc = e.getValue()[0];
            double exp = e.getValue()[1];
            out.add(new PeriodTrendDto(e.getKey(), inc, exp, inc - exp));
        }
        return out;
    }

    private String monthKey(LocalDate d) {
        return String.format("%04d-%02d", d.getYear(), d.getMonthValue());
    }

    private String weekKey(LocalDate d) {
        int y = d.get(ISO_WEEK.weekBasedYear());
        int w = d.get(ISO_WEEK.weekOfWeekBasedYear());
        return y + "-W" + String.format("%02d", w);
    }

    private RecentRecordDto toRecentDto(FinancialRecord r) {
        Long uid = null;
        String uname = null;
        if (r.getCreatedBy() != null) {
            uid = r.getCreatedBy().getId();
            uname = r.getCreatedBy().getName();
        }
        return new RecentRecordDto(
                r.getId(),
                r.getAmount(),
                r.getType(),
                r.getCategory(),
                r.getDate(),
                r.getNote(),
                uid,
                uname
        );
    }
}
