package com.nilsson.padel.entity;

import jakarta.persistence.*;

/**
 * ──────────────────────────────────────────────
 * <h2>Address</h2>
 * ──────────────────────────────────────────────
 * <p><strong>Ansvar:</strong> Representerar en adress som kan kopplas till en kund i systemet.</p>
 * <p><strong>Innehåll:</strong></p>
 * <ul>
 * <li>Gatunamn</li>
 * <li>Stad</li>
 * <li>Postnummer</li>
 * <li>Land</li>
 * </ul>
 * <p><strong>Teknisk roll:</strong> JPA-annoterad entitet som mappas mot databastabellen {@code address}.</p>
 * ──────────────────────────────────────────────
 */
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "street_name", nullable = false, length = 100)
    private String streetName;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "zip_code", nullable = false, length = 10)
    private String zipCode;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    public Address() {
    }

    public Address(String streetName, String city, String zipCode, String country) {
        this.streetName = streetName;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
