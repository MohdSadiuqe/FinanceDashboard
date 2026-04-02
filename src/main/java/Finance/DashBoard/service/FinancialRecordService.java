package Finance.DashBoard.service;

import Finance.DashBoard.dto.FinancialRecordRequest;
import Finance.DashBoard.model.FinancialRecord;
import Finance.DashBoard.model.User;
import Finance.DashBoard.repository.FinancialRecordRepository;
import Finance.DashBoard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialRecordService {

    @Autowired
    private FinancialRecordRepository repo;

    @Autowired
    private UserRepository userRepository;

    public FinancialRecord createRecord(FinancialRecordRequest request) {
        if (request.getAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        FinancialRecord record = new FinancialRecord();
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNote(request.getNote());
        record.setCreatedBy(user);

        return repo.save(record);
    }

    public List<FinancialRecord> getAllRecords() {
        return repo.findAll();
    }

    public FinancialRecord getRecordById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
    }

    public List<FinancialRecord> getRecordsByUser(Long userId) {
        return repo.findByCreatedById(userId);
    }
}