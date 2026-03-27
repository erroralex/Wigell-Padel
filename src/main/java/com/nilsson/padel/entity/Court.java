package com.nilsson.padel.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * ──────────────────────────────────────────────
 * <h2>Court</h2>
 * ──────────────────────────────────────────────
 * <p><strong>Ansvar:</strong> Representerar en padelbana i systemet.</p>
 * <p><strong>Innehåll:</strong></p>
 * <ul>
 * <li>Unikt ID</li>
 * <li>Namn på banan (unika och max 50 tecken)</li>
 * <li>Valfri beskrivning</li>
 * <li>Anger om banan är inomhus</li>
 * <li>Pris per timme i SEK</li>
 * </ul>
 * <p><strong>Teknisk roll:</strong> JPA-annoterad entitet som mappas mot databastabellen {@code court}.</p>
 * ──────────────────────────────────────────────
 */
@Entity
@Table(name = "court")
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Column(nullable = true, length = 255)
    private String description;

    @Column(name = "is_indoor", nullable = false)
    private boolean isIndoor;

    @Column(name = "price_per_hour_sek", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    protected Court() {
    }

    public Court(String name, String description, boolean isIndoor, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.isIndoor = isIndoor;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIndoor() {
        return isIndoor;
    }

    public void setIndoor(boolean indoor) {
        isIndoor = indoor;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
