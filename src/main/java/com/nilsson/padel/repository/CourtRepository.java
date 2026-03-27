package com.nilsson.padel.repository;

import com.nilsson.padel.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepository extends JpaRepository<Court, Long> {
}