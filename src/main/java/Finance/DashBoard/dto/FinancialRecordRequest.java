package Finance.DashBoard.dto;

import Finance.DashBoard.model.RecordType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FinancialRecordRequest {

    @Min(value = 1, message = "Amount must be greater than 0")
    private double amount;

    @NotNull(message = "Type is required")
    private RecordType type;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String note;

    @NotNull(message = "User ID is required")
    private Long userId;
}