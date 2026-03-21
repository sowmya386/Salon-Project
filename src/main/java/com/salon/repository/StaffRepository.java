package com.salon.repository;

import com.salon.entity.Salon;
import com.salon.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findBySalonNameAndActiveTrue(String salonName);
    Page<Staff> findBySalonNameIgnoreCase(String salonName, Pageable pageable);
    List<Staff> findBySalonNameIgnoreCase(String salonName);
    Optional<Staff> findByIdAndSalonNameIgnoreCase(Long id, String salonName);
}
