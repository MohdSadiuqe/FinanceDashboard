package Finance.DashBoard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSummaryDto {

    private double totalIncome;
    private double totalExpense;
    private double netBalance;
    private Map<String, Double> categoryTotalsIncome;
    private Map<String, Double> categoryTotalsExpense;
}
