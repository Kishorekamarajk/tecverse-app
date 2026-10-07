package com.tecverse.backend.controller;

import com.tecverse.backend.entity.FaqItem;
import com.tecverse.backend.repository.FaqItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class FaqController {

    @Autowired
    private FaqItemRepository faqItemRepository;

    /**
     * Get all FAQ items directly from `faq_items` database table.
     */
    @GetMapping({"/api/faq", "/api/faqs", "/api/faq_items"})
    public ResponseEntity<List<FaqItem>> getAllFaqs() {
        List<FaqItem> faqs = faqItemRepository.findAllByOrderByDisplayOrderAsc();
        return ResponseEntity.ok(faqs);
    }

    /**
     * Get FAQ items filtered by page key (e.g. "home", "retreat", "ticket").
     */
    @GetMapping("/api/faq/page/{pageKey}")
    public ResponseEntity<List<FaqItem>> getFaqsByPageKey(@PathVariable String pageKey) {
        List<FaqItem> faqs = faqItemRepository.findByPageKeyIgnoreCaseOrderByDisplayOrderAsc(pageKey);
        return ResponseEntity.ok(faqs);
    }

    /**
     * Search FAQ items dynamically by keyword.
     */
    @GetMapping("/api/faq/search")
    public ResponseEntity<List<FaqItem>> searchFaqs(@RequestParam(name = "query", required = false, defaultValue = "") String query) {
        if (query.trim().isEmpty()) {
            return ResponseEntity.ok(faqItemRepository.findAllByOrderByDisplayOrderAsc());
        }
        List<FaqItem> results = faqItemRepository.searchFaq(query.trim());
        return ResponseEntity.ok(results);
    }
}
