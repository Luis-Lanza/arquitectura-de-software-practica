package bo.edu.ucb.ms.accounting.bl;

import bo.edu.ucb.ms.accounting.dto.JournalDto;
import bo.edu.ucb.ms.accounting.entity.BalanceType;
import bo.edu.ucb.ms.accounting.entity.Journal;
import bo.edu.ucb.ms.accounting.repository.JournalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegisterJournal {

    private static final Logger logger = LoggerFactory.getLogger(RegisterJournal.class);

    @Autowired
    private JournalRepository journalRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Journal registerJournal(JournalDto journalDto) {
        logger.info("=== ACCOUNTING SERVICE ===");
        logger.info("RegisterJournal.registerJournal called with journalDto: {}", journalDto);

        if (journalDto == null) {
            logger.error("JournalDto is null");
            throw new IllegalArgumentException("JournalDto cannot be null");
        }

        if (journalDto.getAccountCode() == null || journalDto.getAccountCode().trim().isEmpty()) {
            logger.error("Account code is required");
            throw new IllegalArgumentException("Account code is required");
        }

        if (journalDto.getAccountName() == null || journalDto.getAccountName().trim().isEmpty()) {
            logger.error("Account name is required");
            throw new IllegalArgumentException("Account name is required");
        }

        if (journalDto.getBalanceType() == null) {
            logger.error("Balance type is required");
            throw new IllegalArgumentException("Balance type is required");
        }

        if (journalDto.getAmount() == null || journalDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Amount must be greater than zero");
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        try {
            // Create Journal entity from DTO
            Journal journal = createJournalFromDto(journalDto);
            
            // Save to database - This will trigger PostgreSQL 0.99 validation if amount is 0.99
            Journal savedJournal = journalRepository.save(journal);
            
            logger.info("Journal entry registered successfully: {}", savedJournal);
            return savedJournal;

        } catch (Exception e) {
            logger.error("Failed to register journal entry", e);
            
            // Check if this is the 0.99 trigger from database
            if (e.getMessage().contains("0.99") || e.getMessage().contains("TRANSACTION_TEST_ERROR")) {
                logger.error("0.99 ROLLBACK TRIGGER ACTIVATED: Database trigger prevented journal creation");
                throw new RuntimeException("Accounting rollback trigger: Amount 0.99 is not allowed for testing", e);
            }
            
            throw new RuntimeException("Failed to register journal entry: " + e.getMessage(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Journal> registerJournalEntries(List<JournalDto> journalDtos) {
        logger.info("=== ACCOUNTING SERVICE ===");
        logger.info("RegisterJournal.registerJournalEntries called with {} entries", 
                   journalDtos != null ? journalDtos.size() : 0);

        if (journalDtos == null || journalDtos.isEmpty()) {
            logger.error("Journal entries list is null or empty");
            throw new IllegalArgumentException("Journal entries list cannot be null or empty");
        }

        List<Journal> savedEntries = new ArrayList<>();

        try {
            for (JournalDto dto : journalDtos) {
                Journal savedEntry = registerJournal(dto);
                savedEntries.add(savedEntry);
            }

            logger.info("All {} journal entries registered successfully", savedEntries.size());
            return savedEntries;

        } catch (Exception e) {
            logger.error("Failed to register journal entries batch", e);
            throw new RuntimeException("Batch journal registration failed: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Journal getJournalById(Integer journalId) {
        logger.info("=== ACCOUNTING SERVICE ===");
        logger.info("RegisterJournal.getJournalById called with journalId: {}", journalId);

        if (journalId == null) {
            logger.warn("Journal ID is null");
            return null;
        }

        return journalRepository.findById(journalId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Journal> getJournalsByReference(String referenceNumber) {
        logger.info("=== ACCOUNTING SERVICE ===");
        logger.info("RegisterJournal.getJournalsByReference called with referenceNumber: {}", referenceNumber);

        if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
            logger.warn("Reference number is null or empty");
            return new ArrayList<>();
        }

        return journalRepository.findByReferenceNumberOrderByCreatedAt(referenceNumber);
    }

    @Transactional
    public void deleteJournalsByReference(String referenceNumber) {
        logger.info("=== ACCOUNTING SERVICE ===");
        logger.info("RegisterJournal.deleteJournalsByReference called with referenceNumber: {}", referenceNumber);

        if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
            logger.warn("Reference number is null or empty, nothing to delete");
            return;
        }

        try {
            List<Journal> existingEntries = journalRepository.findByReferenceNumber(referenceNumber);
            
            if (existingEntries.isEmpty()) {
                logger.warn("No journal entries found for reference: {}", referenceNumber);
                return;
            }

            journalRepository.deleteByReferenceNumber(referenceNumber);
            logger.info("Deleted {} journal entries for reference: {}", existingEntries.size(), referenceNumber);

        } catch (Exception e) {
            logger.error("Failed to delete journal entries for reference: {}", referenceNumber, e);
            throw new RuntimeException("Failed to delete journal entries: " + e.getMessage(), e);
        }
    }

    private Journal createJournalFromDto(JournalDto dto) {
        Journal journal = new Journal();

        // Basic fields
        journal.setAccountCode(dto.getAccountCode());
        journal.setAccountName(dto.getAccountName());
        journal.setDescription(dto.getDescription());
        journal.setReferenceNumber(dto.getReferenceNumber());
        journal.setBalanceType(dto.getBalanceType());

        // Transaction date (default to today if not provided)
        if (dto.getTransactionDate() != null) {
            journal.setTransactionDate(dto.getTransactionDate());
        } else {
            journal.setTransactionDate(LocalDate.now());
        }

        // Set amounts based on balance type and provided amount
        if (dto.getAmount() != null) {
            if (dto.getBalanceType() == BalanceType.D) {
                journal.setDebitAmount(dto.getAmount());
                journal.setCreditAmount(BigDecimal.ZERO);
            } else {
                journal.setCreditAmount(dto.getAmount());
                journal.setDebitAmount(BigDecimal.ZERO);
            }
        } else {
            // Use provided debit/credit amounts directly
            journal.setDebitAmount(dto.getDebitAmount() != null ? dto.getDebitAmount() : BigDecimal.ZERO);
            journal.setCreditAmount(dto.getCreditAmount() != null ? dto.getCreditAmount() : BigDecimal.ZERO);
        }

        // Optional fields with defaults
        journal.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "USD");
        journal.setExchangeRate(dto.getExchangeRate() != null ? dto.getExchangeRate() : BigDecimal.ONE);
        journal.setDepartment(dto.getDepartment());
        journal.setCostCenter(dto.getCostCenter());
        journal.setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : "SYSTEM");
        journal.setNotes(dto.getNotes());

        return journal;
    }

    public JournalDto convertToDto(Journal journal) {
        if (journal == null) {
            return null;
        }

        JournalDto dto = new JournalDto();
        dto.setId(journal.getId());
        dto.setJournalEntryNumber(journal.getJournalEntryNumber());
        dto.setAccountCode(journal.getAccountCode());
        dto.setAccountName(journal.getAccountName());
        dto.setDescription(journal.getDescription());
        dto.setTransactionDate(journal.getTransactionDate());
        dto.setReferenceNumber(journal.getReferenceNumber());
        dto.setBalanceType(journal.getBalanceType());
        dto.setAmount(journal.getAmount()); // This uses the getAmount() method from Journal entity
        dto.setDebitAmount(journal.getDebitAmount());
        dto.setCreditAmount(journal.getCreditAmount());
        dto.setCurrency(journal.getCurrency());
        dto.setExchangeRate(journal.getExchangeRate());
        dto.setStatus(journal.getStatus());
        dto.setDepartment(journal.getDepartment());
        dto.setCostCenter(journal.getCostCenter());
        dto.setCreatedBy(journal.getCreatedBy());
        dto.setCreatedAt(journal.getCreatedAt());
        dto.setUpdatedAt(journal.getUpdatedAt());
        dto.setPostedAt(journal.getPostedAt());
        dto.setPostedBy(journal.getPostedBy());
        dto.setNotes(journal.getNotes());

        return dto;
    }
}