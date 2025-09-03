package bo.edu.ucb.ms.accounting.api;

import bo.edu.ucb.ms.accounting.bl.RegisterJournal;
import bo.edu.ucb.ms.accounting.dto.JournalDto;
import bo.edu.ucb.ms.accounting.entity.Journal;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounting")
public class AccountingApi {

    private static final Logger logger = LoggerFactory.getLogger(AccountingApi.class);

    @Autowired
    private RegisterJournal registerJournal;

    @PostMapping("/journal")
    public ResponseEntity<JournalDto> createJournalEntry(@RequestBody @Valid JournalDto journalDto) {
        logger.info("=== ACCOUNTING API ===");
        logger.info("POST /api/accounting/journal called with journalDto: {}", journalDto);

        try {
            Journal createdJournal = registerJournal.registerJournal(journalDto);
            JournalDto responseDto = registerJournal.convertToDto(createdJournal);
            
            logger.info("Journal entry created successfully: {}", responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid journal data provided: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            logger.error("Error creating journal entry", e);
            
            // Check if this is the 0.99 rollback trigger
            if (e.getMessage().contains("0.99") || e.getMessage().contains("rollback trigger")) {
                logger.error("0.99 ROLLBACK TRIGGER: Journal creation failed for testing purposes");
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Unexpected error creating journal entry", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/journal/batch")
    public ResponseEntity<List<JournalDto>> createJournalEntries(@RequestBody @Valid List<JournalDto> journalDtos) {
        logger.info("=== ACCOUNTING API ===");
        logger.info("POST /api/accounting/journal/batch called with {} entries", 
                   journalDtos != null ? journalDtos.size() : 0);

        try {
            if (journalDtos == null || journalDtos.isEmpty()) {
                logger.warn("Empty journal entries list provided");
                return ResponseEntity.badRequest().build();
            }

            List<Journal> createdJournals = registerJournal.registerJournalEntries(journalDtos);
            List<JournalDto> responseDtos = createdJournals.stream()
                    .map(journal -> registerJournal.convertToDto(journal))
                    .collect(Collectors.toList());
            
            logger.info("Batch journal entries created successfully: {} entries", responseDtos.size());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDtos);

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid journal batch data provided: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            logger.error("Error creating journal entries batch", e);
            
            // Check if this is the 0.99 rollback trigger
            if (e.getMessage().contains("0.99") || e.getMessage().contains("rollback trigger")) {
                logger.error("0.99 ROLLBACK TRIGGER: Batch journal creation failed for testing purposes");
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Unexpected error creating journal entries batch", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

@GetMapping("/journal/{journalId}")
    public ResponseEntity<JournalDto> getJournalEntry(@PathVariable Long journalId) {
        logger.info("=== ACCOUNTING API ===");
        logger.info("GET /api/accounting/journal/{} called", journalId);

        try {
            Journal journal = registerJournal.getJournalById(journalId);
            
            if (journal == null) {
                logger.warn("Journal entry not found with id: {}", journalId);
                return ResponseEntity.notFound().build();
            }

            JournalDto journalDto = registerJournal.convertToDto(journal);
            logger.info("Returning journal entry: {}", journalDto);
            return ResponseEntity.ok(journalDto);

        } catch (Exception e) {
            logger.error("Error retrieving journal entry with id: {}", journalId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/journal/transaction/{transactionNumber}")
    public ResponseEntity<List<JournalDto>> getJournalEntriesByTransaction(
            @PathVariable String transactionNumber) {
        
        logger.info("=== ACCOUNTING API ===");
        logger.info("GET /api/accounting/journal/transaction/{} called", transactionNumber);

        try {
            List<Journal> journals = registerJournal.getJournalsByReference(transactionNumber);
            
            if (journals.isEmpty()) {
                logger.warn("No journal entries found for transaction: {}", transactionNumber);
                return ResponseEntity.notFound().build();
            }

            List<JournalDto> journalDtos = journals.stream()
                    .map(journal -> registerJournal.convertToDto(journal))
                    .collect(Collectors.toList());
            
            logger.info("Returning {} journal entries for transaction: {}", journalDtos.size(), transactionNumber);
            return ResponseEntity.ok(journalDtos);

        } catch (Exception e) {
            logger.error("Error retrieving journal entries for transaction: {}", transactionNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/journal/transaction/{transactionNumber}")
    public ResponseEntity<Void> deleteJournalEntriesByTransaction(
            @PathVariable String transactionNumber) {
        
        logger.info("=== ACCOUNTING API ===");
        logger.info("DELETE /api/accounting/journal/transaction/{} called", transactionNumber);

        try {
            registerJournal.deleteJournalsByReference(transactionNumber);
            
            logger.info("Journal entries deleted successfully for transaction: {}", transactionNumber);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            logger.error("Error deleting journal entries for transaction: {}", transactionNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Unexpected error deleting journal entries for transaction: {}", transactionNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/journal/test")
    public ResponseEntity<JournalDto> createTestJournalEntry(
            @RequestParam(defaultValue = "1200") String accountCode,
            @RequestParam(defaultValue = "Accounts Receivable") String accountName,
            @RequestParam(defaultValue = "Test journal entry") String description,
            @RequestParam(defaultValue = "D") String balanceType,
            @RequestParam(defaultValue = "10.99") String amount,
            @RequestParam(defaultValue = "TEST-001") String referenceNumber) {
        
        logger.info("=== ACCOUNTING API - TEST ===");
        logger.info("POST /api/accounting/journal/test called with amount: {}", amount);

        try {
            JournalDto testJournal = new JournalDto();
            testJournal.setAccountCode(accountCode);
            testJournal.setAccountName(accountName);
            testJournal.setDescription(description);
            testJournal.setBalanceType(balanceType);
            testJournal.setAmount(java.math.BigDecimal.valueOf(Double.parseDouble(amount)));
            testJournal.setReferenceNumber(referenceNumber);

            return createJournalEntry(testJournal);

        } catch (Exception e) {
            logger.error("Error in test journal entry creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}