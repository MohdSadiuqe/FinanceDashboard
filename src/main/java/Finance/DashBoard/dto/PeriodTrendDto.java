package Finance.DashBoard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodTrendDto {

    /** ISO week (e.g. 2026-W14) or year-month (e.g. 2026-04) */
    private String period;
    private double income;
    private double expense;
    private double net;
}
