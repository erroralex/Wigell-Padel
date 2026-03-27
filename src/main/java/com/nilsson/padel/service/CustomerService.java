package com.nilsson.padel.service;

import com.groupc.shared.exception.ResourceNotFoundException;
import com.nilsson.padel.dto.AddressRecord;
import com.nilsson.padel.dto.CustomerRequest;
import com.nilsson.padel.dto.CustomerResponse;
import com.nilsson.padel.entity.Address;
import com.nilsson.padel.entity.Customer;
import com.nilsson.padel.repository.AddressRepository;
import com.nilsson.padel.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    public CustomerService(CustomerRepository customerRepository, AddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kund med ID " + id + " hittades inte."));
        return mapToResponse(customer);
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Adress med ID " + request.addressId() + " hittades inte."));

        Customer customer = new Customer(
                request.username(),
                request.role(),
                request.firstName(),
                request.lastName(),
                address
        );

        return mapToResponse(customerRepository.save(customer));
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kund med ID " + id + " hittades inte."));

        Address newAddress = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Adress med ID " + request.addressId() + " hittades inte."));

        customer.setUsername(request.username());
        customer.setRole(request.role());
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setAddress(newAddress);

        return mapToResponse(customerRepository.save(customer));
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kunde inte radera: Kund med ID " + id + " hittades inte.");
        }
        customerRepository.deleteById(id);
    }

    public CustomerResponse createAddressForCustomer(Long customerId, AddressRecord request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Kund med ID " + customerId + " hittades inte."));

        Address newAddress = new Address();
        newAddress.setStreetName(request.streetName());
        newAddress.setCity(request.city());
        newAddress.setZipCode(request.zipCode());
        newAddress.setCountry(request.country());
        Address savedAddress = addressRepository.save(newAddress);

        customer.setAddress(savedAddress);
        return mapToResponse(customerRepository.save(customer));
    }

    public void deleteAddressFromCustomer(Long customerId, Long addressId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Kund med ID " + customerId + " hittades inte."));

        if (!customer.getAddress().getId().equals(addressId)) {
            throw new IllegalArgumentException("Adressen tillhör inte denna kund.");
        }
        throw new IllegalStateException("Kunden måste ha minst en adress. Vänligen uppdatera kunden med en ny adress istället för att radera den.");
    }

    private CustomerResponse mapToResponse(Customer customer) {
        AddressRecord addressRecord = new AddressRecord(
                customer.getAddress().getId(),
                customer.getAddress().getStreetName(),
                customer.getAddress().getCity(),
                customer.getAddress().getZipCode(),
                customer.getAddress().getCountry()
        );

        return new CustomerResponse(
                customer.getId(),
                customer.getUsername(),
                customer.getRole(),
                customer.getFirstName(),
                customer.getLastName(),
                addressRecord
        );
    }
}
