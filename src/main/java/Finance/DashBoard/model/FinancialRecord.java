package Finance.DashBoard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "financial_records")
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
    @Column(name = "record_date", nullable = false)
    private LocalDate date;

    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password"})
    private User createdBy;
}
