package Finance.DashBoard.controller;

import Finance.DashBoard.dto.FinancialRecordRequest;
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

    @PostMapping
    public FinancialRecord create(@Valid @RequestBody FinancialRecordRequest request) {
        return service.createRecord(request);
    }

    @GetMapping
    public List<FinancialRecord> getAll() {
        return service.getAllRecords();
    }

    @GetMapping("/{id}")
    public FinancialRecord getById(@PathVariable Long id) {
        return service.getRecordById(id);
    }

    @GetMapping("/user/{userId}")
    public List<FinancialRecord> getRecordsByUser(@PathVariable Long userId) {
        return service.getRecordsByUser(userId);
    }
}