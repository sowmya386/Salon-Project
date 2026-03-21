package com.salon.service;

import com.salon.dto.StaffRequest;
import com.salon.dto.StaffResponse;
import com.salon.entity.Salon;
import com.salon.entity.Staff;
import com.salon.repository.StaffRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final SalonService salonService;

    public StaffService(StaffRepository staffRepository, SalonService salonService) {
        this.staffRepository = staffRepository;
        this.salonService = salonService;
    }

    public Page<StaffResponse> getSalonStaff(Pageable pageable) {
        String salonName = salonService.getCurrentSalon().getName();
        return staffRepository.findBySalonNameIgnoreCase(salonName, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public StaffResponse createStaff(StaffRequest request) {
        String salonName = salonService.getCurrentSalon().getName();
        Staff staff = new Staff();
        staff.setSalonName(salonName);
        staff.setFullName(request.getFullName());
        staff.setEmail(request.getEmail());
        staff.setPhone(request.getPhone());
        if (request.getCommissionPercent() != null) {
            staff.setCommissionPercent(request.getCommissionPercent());
        }
        staff.setActive(true);
        
        return mapToResponse(staffRepository.save(staff));
    }

    @Transactional
    public StaffResponse updateStaff(Long id, StaffRequest request) {
        String salonName = salonService.getCurrentSalon().getName();
        Staff staff = staffRepository.findByIdAndSalonNameIgnoreCase(id, salonName)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        if (request.getFullName() != null) staff.setFullName(request.getFullName());
        if (request.getEmail() != null) staff.setEmail(request.getEmail());
        if (request.getPhone() != null) staff.setPhone(request.getPhone());
        if (request.getCommissionPercent() != null) staff.setCommissionPercent(request.getCommissionPercent());

        return mapToResponse(staffRepository.save(staff));
    }
    
    @Transactional
    public StaffResponse toggleStaffActive(Long id) {
        String salonName = salonService.getCurrentSalon().getName();
        Staff staff = staffRepository.findByIdAndSalonNameIgnoreCase(id, salonName)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        staff.setActive(!staff.isActive());
        return mapToResponse(staffRepository.save(staff));
    }

    @Transactional
    public void deleteStaff(Long id) {
        String salonName = salonService.getCurrentSalon().getName();
        Staff staff = staffRepository.findByIdAndSalonNameIgnoreCase(id, salonName)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        staffRepository.delete(staff);
    }

    private StaffResponse mapToResponse(Staff staff) {
        return new StaffResponse(
                staff.getId(),
                staff.getFullName(),
                staff.getEmail(),
                staff.getPhone(),
                staff.getCommissionPercent(),
                staff.isActive()
        );
    }
}
