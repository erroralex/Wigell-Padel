package com.nilsson.padel.entity;

import jakarta.persistence.*;

/**
 * ──────────────────────────────────────────────
 * <h2>Customer</h2>
 * ──────────────────────────────────────────────
 * <p><strong>Ansvar:</strong> Representerar en kund i systemet.</p>
 * <p><strong>Innehåll:</strong></p>
 * <ul>
 * <li>Unikt ID</li>
 * <li>Användarnamn (unika och max 50 tecken)</li>
 * <li>Roll i systemet</li>
 * <li>Förnamn och efternamn</li>
 * <li>Adress kopplad via {@link Address}</li>
 * </ul>
 * <p><strong>Teknisk roll:</strong> JPA-annoterad entitet som mappas mot databastabellen {@code customer}.</p>
 * ──────────────────────────────────────────────
 */
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 50)
    private String role;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    protected Customer() {
    }

    public Customer(String username, String role, String firstName, String lastName, Address address) {
        this.username = username;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
