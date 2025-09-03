package bo.edu.ucb.ms.accounting.bl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.ucb.ms.accounting.dto.JournalDto;
import bo.edu.ucb.ms.accounting.entity.Journal;
import bo.edu.ucb.ms.accounting.repository.JournalRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegisterJournal {
    
    @Autowired
    private JournalRepository journalRepository;
    
    @Transactional(propagation = Propagation.REQUIRED)
    public Journal registerJournal(JournalDto journalDto) {
        // Validaciones básicas
        if (journalDto == null) {
            throw new IllegalArgumentException("JournalDto cannot be null");
        }
        
        if (journalDto.getAccountCode() == null || journalDto.getAccountCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Account code is required");
        }
        
        if (journalDto.getAccountName() == null || journalDto.getAccountName().trim().isEmpty()) {
            throw new IllegalArgumentException("Account name is required");
        }
        
        if (journalDto.getDescription() == null || journalDto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        
        if (journalDto.getAmount() == null || journalDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        if (journalDto.getBalanceType() == null || 
            (!journalDto.getBalanceType().equals("D") && !journalDto.getBalanceType().equals("C"))) {
            throw new IllegalArgumentException("Balance type must be 'D' (Debit) or 'C' (Credit)");
        }
        
        // CreatedBy is optional, use default if not provided
        String createdBy = (journalDto.getCreatedBy() != null && !journalDto.getCreatedBy().trim().isEmpty()) 
            ? journalDto.getCreatedBy().trim() 
            : "SYSTEM";
        
        // Crear nueva entrada de diario
        Journal journal = new Journal();
        
        // Generar número de entrada único
        String journalEntryNumber = generateJournalEntryNumber();
        journal.setJournalEntryNumber(journalEntryNumber);
        
        // Configurar campos básicos
        journal.setAccountCode(journalDto.getAccountCode().trim());
        journal.setAccountName(journalDto.getAccountName().trim());
        journal.setDescription(journalDto.getDescription().trim());
        journal.setCreatedBy(createdBy);
        
        // Configurar fechas
        LocalDate transactionDate = journalDto.getTransactionDate() != null 
            ? journalDto.getTransactionDate() 
            : LocalDate.now();
        journal.setTransactionDate(transactionDate);
        journal.setPostingDate(LocalDate.now());
        
        // Calcular y asignar montos según el tipo de balance
        BigDecimal amount = journalDto.getAmount();
        if ("D".equals(journalDto.getBalanceType())) {
            journal.setDebitAmount(amount);
            journal.setCreditAmount(BigDecimal.ZERO);
            journal.setBalanceType(Journal.BalanceType.D);
        } else {
            journal.setDebitAmount(BigDecimal.ZERO);
            journal.setCreditAmount(amount);
            journal.setBalanceType(Journal.BalanceType.C);
        }
        
        // Configurar campos opcionales
        if (journalDto.getReferenceNumber() != null && !journalDto.getReferenceNumber().trim().isEmpty()) {
            journal.setReferenceNumber(journalDto.getReferenceNumber().trim());
        }
        
        if (journalDto.getDepartment() != null && !journalDto.getDepartment().trim().isEmpty()) {
            journal.setDepartment(journalDto.getDepartment().trim());
        }
        
        if (journalDto.getCostCenter() != null && !journalDto.getCostCenter().trim().isEmpty()) {
            journal.setCostCenter(journalDto.getCostCenter().trim());
        }
        
        if (journalDto.getNotes() != null && !journalDto.getNotes().trim().isEmpty()) {
            journal.setNotes(journalDto.getNotes().trim());
        }
        
        // Configurar valores por defecto
        journal.setCurrencyCode("USD");
        journal.setExchangeRate(BigDecimal.ONE);
        journal.setStatus(Journal.Status.draft);
        
        // Guardar en la base de datos
        return journalRepository.save(journal);
    }
    
    /**
     * Genera un número único para la entrada de diario
     * Formato: JE-YYYYMMDD-HHMMSS
     */
    private String generateJournalEntryNumber() {
        LocalDate now = LocalDate.now();
        long timestamp = System.currentTimeMillis() % 100000; // últimos 5 dígitos del timestamp
        return String.format("JE-%s-%05d", 
            now.format(DateTimeFormatter.ofPattern("yyyyMMdd")), 
            timestamp);
    }
    
    // MICROSERVICES-ONLY METHODS (NOT IN MONOLITH) - Required for distributed transactions
    
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Journal> registerJournalEntries(List<JournalDto> journalDtos) {
        System.out.println("=== ACCOUNTING SERVICE - MICROSERVICES ONLY ===");
        System.out.println("RegisterJournal.registerJournalEntries called with " + 
                           (journalDtos != null ? journalDtos.size() : 0) + " entries");

        if (journalDtos == null || journalDtos.isEmpty()) {
            System.out.println("ERROR: Journal entries list is null or empty");
            throw new IllegalArgumentException("Journal entries list cannot be null or empty");
        }

        List<Journal> savedEntries = new ArrayList<>();

        try {
            for (JournalDto dto : journalDtos) {
                Journal savedEntry = registerJournal(dto);
                savedEntries.add(savedEntry);
            }

            System.out.println("All " + savedEntries.size() + " journal entries registered successfully");
            return savedEntries;

        } catch (Exception e) {
            System.out.println("ERROR: Failed to register journal entries batch: " + e.getMessage());
            throw new RuntimeException("Batch journal registration failed: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Journal getJournalById(Long journalId) {
        System.out.println("=== ACCOUNTING SERVICE - MICROSERVICES ONLY ===");
        System.out.println("RegisterJournal.getJournalById called with journalId: " + journalId);

        if (journalId == null) {
            System.out.println("WARNING: Journal ID is null");
            return null;
        }

        return journalRepository.findById(journalId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Journal> getJournalsByReference(String referenceNumber) {
        System.out.println("=== ACCOUNTING SERVICE - MICROSERVICES ONLY ===");
        System.out.println("RegisterJournal.getJournalsByReference called with referenceNumber: " + referenceNumber);

        if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
            System.out.println("WARNING: Reference number is null or empty");
            return new ArrayList<>();
        }

        return journalRepository.findByReferenceNumberOrderByCreatedAt(referenceNumber);
    }

    @Transactional
    public void deleteJournalsByReference(String referenceNumber) {
        System.out.println("=== ACCOUNTING SERVICE - MICROSERVICES ONLY ===");
        System.out.println("RegisterJournal.deleteJournalsByReference called with referenceNumber: " + referenceNumber);

        if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
            System.out.println("WARNING: Reference number is null or empty, nothing to delete");
            return;
        }

        try {
            List<Journal> existingEntries = journalRepository.findByReferenceNumber(referenceNumber);
            
            if (existingEntries.isEmpty()) {
                System.out.println("WARNING: No journal entries found for reference: " + referenceNumber);
                return;
            }

            journalRepository.deleteByReferenceNumber(referenceNumber);
            System.out.println("Deleted " + existingEntries.size() + " journal entries for reference: " + referenceNumber);

        } catch (Exception e) {
            System.out.println("ERROR: Failed to delete journal entries for reference: " + referenceNumber + " - " + e.getMessage());
            throw new RuntimeException("Failed to delete journal entries: " + e.getMessage(), e);
        }
    }

    public JournalDto convertToDto(Journal journal) {
        if (journal == null) {
            return null;
        }

        JournalDto dto = new JournalDto();
        dto.setAccountCode(journal.getAccountCode());
        dto.setAccountName(journal.getAccountName());
        dto.setDescription(journal.getDescription());
        dto.setTransactionDate(journal.getTransactionDate());
        dto.setReferenceNumber(journal.getReferenceNumber());
        dto.setBalanceType(journal.getBalanceTypeAsString()); // Use new method to avoid conflicts
        dto.setAmount(journal.getAmount()); // This uses the getAmount() method from Journal entity
        dto.setDepartment(journal.getDepartment());
        dto.setCostCenter(journal.getCostCenter());
        dto.setCreatedBy(journal.getCreatedBy());
        dto.setNotes(journal.getNotes());

        return dto;
    }
}