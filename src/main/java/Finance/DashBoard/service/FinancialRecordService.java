package Finance.DashBoard.service;

import Finance.DashBoard.dto.FinancialRecordRequest;
import Finance.DashBoard.dto.FinancialRecordUpdateRequest;
import Finance.DashBoard.exception.BusinessException;
import Finance.DashBoard.model.FinancialRecord;
import Finance.DashBoard.model.User;
import Finance.DashBoard.repository.FinancialRecordRepository;
import Finance.DashBoard.repository.FinancialRecordSpecification;
import Finance.DashBoard.model.RecordType;
import Finance.DashBoard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class FinancialRecordService {

    @Autowired
    private FinancialRecordRepository repo;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public FinancialRecord createRecord(FinancialRecordRequest request) {
        if (request.getAmount() <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Amount must be greater than 0");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Cannot assign records to an inactive user");
        }

        FinancialRecord record = new FinancialRecord();
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory().trim());
        record.setDate(request.getDate());
        record.setNote(request.getNote());
        record.setCreatedBy(user);

        return repo.save(record);
    }

    public Page<FinancialRecord> findRecords(
            RecordType type,
            String category,
            LocalDate dateFrom,
            LocalDate dateTo,
            Long createdByUserId,
            Pageable pageable
    ) {
        Specification<FinancialRecord> spec = FinancialRecordSpecification.filtered(
                type, category, dateFrom, dateTo, createdByUserId
        );
        return repo.findAll(spec, pageable);
    }

    public FinancialRecord getRecordById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Record not found"));
    }

    @Transactional
    public FinancialRecord updateRecord(Long id, FinancialRecordUpdateRequest request) {
        if (request.getAmount() <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Amount must be greater than 0");
        }

        FinancialRecord record = repo.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Record not found"));

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory().trim());
        record.setDate(request.getDate());
        record.setNote(request.getNote());

        return repo.save(record);
    }

    @Transactional
    public void deleteRecord(Long id) {
        FinancialRecord record = repo.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Record not found"));
        repo.delete(record);
    }
}
