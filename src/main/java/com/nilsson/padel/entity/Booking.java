package com.nilsson.padel.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 ──────────────────────────────────────────────
 <h2>Booking</h2>
 ──────────────────────────────────────────────
 <p><strong>Ansvar:</strong> Representerar en bokning av en padelbana för en specifik kund, tid och spelomgång.</p>
 <p><strong>Innehåll:</strong></p>
 <ul>
 <li>Koppling till kund och padelbana</li>
 <li>Bokningsdatum och starttid</li>
 <li>Antal spelare</li>
 <li>Totalpris i SEK och EUR</li>
 </ul>
 <p><strong>Teknisk roll:</strong> JPA-annoterad entitet som mappas mot databastabellen {@code booking}.</p>
 ──────────────────────────────────────────────

 */
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "players", nullable = false)
    private Integer numberOfPlayers;

    @Column(name = "total_price_sek", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPriceSek;

    @Column(name = "total_price_eur", precision = 10, scale = 2)
    private BigDecimal totalPriceEur;

    protected Booking() {
    }

    public Booking(Customer customer, Court court, LocalDate bookingDate, LocalTime startTime, Integer numberOfPlayers, BigDecimal totalPriceSek) {
        this.customer = customer;
        this.court = court;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.numberOfPlayers = numberOfPlayers;
        this.totalPriceSek = totalPriceSek;
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(Integer numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public BigDecimal getTotalPriceSek() {
        return totalPriceSek;
    }

    public void setTotalPriceSek(BigDecimal totalPriceSek) {
        this.totalPriceSek = totalPriceSek;
    }

    public BigDecimal getTotalPriceEur() {
        return totalPriceEur;
    }

    public void setTotalPriceEur(BigDecimal totalPriceEur) {
        this.totalPriceEur = totalPriceEur;
    }
}