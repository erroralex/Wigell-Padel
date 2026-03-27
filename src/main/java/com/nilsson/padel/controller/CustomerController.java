package com.nilsson.padel.controller;

import com.nilsson.padel.dto.AddressRecord;
import com.nilsson.padel.dto.CustomerRequest;
import com.nilsson.padel.dto.CustomerResponse;
import com.nilsson.padel.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * ──────────────────────────────────────────────
 * <h2>CustomerController</h2>
 * ──────────────────────────────────────────────
 * <p><strong>Ansvar:</strong> Exponerar REST-endpoints för administration av kunder och deras adresser.</p>
 * <p><strong>Funktioner:</strong></p>
 * <ul>
 * <li>Hämtar, skapar, uppdaterar och raderar kunder</li>
 * <li>Hantera koppling av adresser till specifika kunder</li>
 * <li>Tar emot och returnerar kundrelaterade DTO:er via API</li>
 * <li>Skyddar hela kontrollern med administratörsbehörighet</li>
 * </ul>
 * <p><strong>Teknisk roll:</strong> Spring {@code @RestController} som hanterar HTTP-anrop och delegerar affärslogik till {@code CustomerService}.</p>
 * ──────────────────────────────────────────────
 */
@RestController
@RequestMapping("/api/v1/customers")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        CustomerResponse createdCustomer = customerService.createCustomer(request);
        URI location = URI.create("/api/v1/customers/" + createdCustomer.id());
        return ResponseEntity.created(location).body(createdCustomer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id, @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{customerId}/addresses")
    public ResponseEntity<CustomerResponse> addAddressToCustomer(
            @PathVariable Long customerId,
            @RequestBody AddressRecord request) {

        CustomerResponse updatedCustomer = customerService.createAddressForCustomer(customerId, request);
        URI location = URI.create("/api/v1/customers/" + updatedCustomer.id());
        return ResponseEntity.created(location).body(updatedCustomer);
    }

    @DeleteMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddressFromCustomer(
            @PathVariable Long customerId,
            @PathVariable Long addressId) {

        customerService.deleteAddressFromCustomer(customerId, addressId);
        return ResponseEntity.noContent().build();
    }

}