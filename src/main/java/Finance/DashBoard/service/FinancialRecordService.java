package Finance.DashBoard.service;
import Finance.DashBoard.model.FinancialRecord;
import Finance.DashBoard.repository.FinancialRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialRecordService {

    @Autowired
    private FinancialRecordRepository repo;

    // CREATE
    public FinancialRecord createRecord(FinancialRecord record) {
        if (record.getAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }
        return repo.save(record);
    }

    // READ
    public List<FinancialRecord> getAllRecords() {
        return repo.findAll();
    }

    // UPDATE
    public FinancialRecord updateRecord(Long id, FinancialRecord newRecord) {
        FinancialRecord record = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        record.setAmount(newRecord.getAmount());
        record.setCategory(newRecord.getCategory());
        record.setType(newRecord.getType());
        record.setDate(newRecord.getDate());
        record.setNote(newRecord.getNote());

        return repo.save(record);
    }

    // DELETE
    public void deleteRecord(Long id) {
        repo.deleteById(id);
    }
}