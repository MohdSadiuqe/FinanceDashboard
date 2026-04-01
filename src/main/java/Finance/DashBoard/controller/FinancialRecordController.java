package Finance.DashBoard.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import Finance.DashBoard.model.FinancialRecord;
import Finance.DashBoard.service.FinancialRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/records")
public class FinancialRecordController {

    @Autowired
    private FinancialRecordService service;

    // ADMIN ONLY CREATE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public FinancialRecord create(@Valid @RequestBody FinancialRecord record) {
        return service.createRecord(record);
    }

    // ADMIN + ANALYST READ
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping
    public List<FinancialRecord> getAll() {
        return service.getAllRecords();
    }

    // UPDATE
    @PutMapping("/{id}")
    public FinancialRecord update(@PathVariable Long id,
                                  @RequestBody FinancialRecord record) {
        return service.updateRecord(id, record);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteRecord(id);
        return "Deleted Successfully";
    }
}