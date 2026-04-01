package Finance.DashBoard.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import jakarta.validation.constraints.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    private double amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RecordType type;

    @NotBlank
    private String category;

    @NotNull
    private LocalDate date;

    private String note;

    @ManyToOne
    private User createdBy;
}