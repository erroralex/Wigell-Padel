package com.nilsson.padel.repository;

import com.nilsson.padel.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
