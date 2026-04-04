package Finance.DashBoard.dto;

import Finance.DashBoard.model.RecordType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentRecordDto {

    private Long id;
    private double amount;
    private RecordType type;
    private String category;
    private LocalDate date;
    private String note;
    private Long createdByUserId;
    private String createdByName;
}
