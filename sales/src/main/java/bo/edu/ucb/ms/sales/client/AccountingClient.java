package bo.edu.ucb.ms.sales.client;

import bo.edu.ucb.ms.sales.dto.JournalDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "accounting")
public interface AccountingClient {

    @PostMapping("/api/accounting/journal")
    ResponseEntity<JournalDto> createJournalEntry(@RequestBody JournalDto journalDto);

    @PostMapping("/api/accounting/journal/batch")
    ResponseEntity<List<JournalDto>> createJournalEntries(@RequestBody List<JournalDto> journalEntries);

    @DeleteMapping("/api/accounting/journal/transaction/{transactionNumber}")
    ResponseEntity<Void> deleteJournalEntriesByTransaction(@PathVariable("transactionNumber") String transactionNumber);

    @GetMapping("/api/accounting/journal/transaction/{transactionNumber}")
    ResponseEntity<List<JournalDto>> getJournalEntriesByTransaction(@PathVariable("transactionNumber") String transactionNumber);
}