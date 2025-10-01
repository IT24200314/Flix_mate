package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Receipt;
import com.flixmate.flixmate.api.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receipts")
@CrossOrigin(origins = "*")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @PostMapping("/generate/{bookingId}/{paymentId}")
    public ResponseEntity<Receipt> generateReceipt(@PathVariable Integer bookingId, @PathVariable Integer paymentId) {
        try {
            Receipt receipt = receiptService.generateReceipt(bookingId, paymentId);
            return ResponseEntity.ok(receipt);
        } catch (Exception e) {
            System.err.println("Error generating receipt: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<Receipt>> getUserReceipts(@RequestHeader(value = "User-Email", required = false) String userEmail) {
        try {
            if (userEmail == null || userEmail.trim().isEmpty()) {
                System.err.println("User-Email header is missing or empty");
                return ResponseEntity.badRequest().body(null);
            }
            
            System.out.println("Getting receipts for user: " + userEmail);
            List<Receipt> receipts = receiptService.getUserReceipts(userEmail);
            System.out.println("Found " + receipts.size() + " receipts for user: " + userEmail);
            return ResponseEntity.ok(receipts);
        } catch (Exception e) {
            System.err.println("Error getting user receipts: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{receiptNumber}")
    public ResponseEntity<Receipt> getReceiptByNumber(@PathVariable String receiptNumber, 
                                                     @RequestHeader(value = "User-Email", required = false) String userEmail) {
        try {
            Receipt receipt = receiptService.getReceiptByNumber(receiptNumber);
            if (receipt != null) {
                // Security check: ensure user can only access their own receipts
                if (userEmail != null && !receipt.getUserEmail().equals(userEmail)) {
                    System.err.println("User " + userEmail + " attempted to access receipt " + receiptNumber + " belonging to " + receipt.getUserEmail());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                return ResponseEntity.ok(receipt);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error getting receipt: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Receipt> getReceiptByBookingId(@PathVariable Integer bookingId,
                                                          @RequestHeader(value = "User-Email", required = false) String userEmail) {
        try {
            Receipt receipt = receiptService.getReceiptByBookingId(bookingId);
            if (receipt != null) {
                // Security check: ensure user can only access their own receipts
                if (userEmail != null && !receipt.getUserEmail().equals(userEmail)) {
                    System.err.println("User " + userEmail + " attempted to access receipt for booking " + bookingId + " belonging to " + receipt.getUserEmail());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                return ResponseEntity.ok(receipt);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error getting receipt by booking ID: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
