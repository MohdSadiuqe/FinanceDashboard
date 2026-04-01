package Finance.DashBoard.service;

import Finance.DashBoard.model.*;
import Finance.DashBoard.repository.FinancialRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private FinancialRecordRepository repo;

    public Map<String, Object> getSummary() {

        List<FinancialRecord> records = repo.findAll();

        double income = 0;
        double expense = 0;

        Map<String, Double> categoryMap = new HashMap<>();

        for (FinancialRecord r : records) {

            if (r.getType() == RecordType.INCOME) {
                income += r.getAmount();
            } else {
                expense += r.getAmount();
            }

            categoryMap.put(
                    r.getCategory(),
                    categoryMap.getOrDefault(r.getCategory(), 0.0) + r.getAmount()
            );
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalIncome", income);
        result.put("totalExpense", expense);
        result.put("netBalance", income - expense);
        result.put("categoryBreakdown", categoryMap);

        return result;
    }
}