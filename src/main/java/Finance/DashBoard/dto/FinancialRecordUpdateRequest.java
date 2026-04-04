package Finance.DashBoard.dto;

import Finance.DashBoard.model.RecordType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FinancialRecordUpdateRequest {

    @Min(value = 1, message = "Amount must be greater than 0")
    private double amount;

    @NotNull(message = "Type is required")
    private RecordType type;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String note;
}
