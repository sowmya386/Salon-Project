package com.salon.service;

import com.salon.dto.ServiceRequest;



import com.salon.dto.ServiceResponse;
import com.salon.entity.Salon;
import com.salon.entity.Service;
import com.salon.repository.SalonRepository;
import com.salon.repository.ServiceRepository;
import com.salon.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.salon.audit.AuditAction;
import com.salon.audit.AuditLogService;



import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final SalonRepository salonRepository;
    private final AuditLogService auditLogService;


    public ServiceService(ServiceRepository serviceRepository,
            SalonRepository salonRepository,
            AuditLogService auditLogService) {

			this.serviceRepository = serviceRepository;
			this.salonRepository = salonRepository;
			this.auditLogService = auditLogService;
	}


    // ADMIN ONLY
    public ServiceResponse createService(ServiceRequest request) {

        Long salonId = SecurityUtil.getCurrentSalonId();

        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon not found"));

        Service service = new Service();
        service.setName(request.getName());
        service.setPrice(request.getPrice());
        service.setDurationInMinutes(request.getDurationInMinutes());
        service.setSalon(salon);

        service = serviceRepository.save(service);
        auditLogService.log(
        	    AuditAction.CREATE_SERVICE,
        	    "Admin created service ID " + service.getId()
        	);


        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getPrice(),
                service.getDurationInMinutes()
        );
    }

    // CUSTOMER + ADMIN
    public List<ServiceResponse> getActiveServices() {

        Long salonId = SecurityUtil.getCurrentSalonId();

        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon not found"));


        return serviceRepository.findBySalon_IdAndActiveTrue(salonId)
                .stream()
                .map(s -> new ServiceResponse(
                        s.getId(),
                        s.getName(),
                        s.getPrice(),
                        s.getDurationInMinutes()
                ))
                .toList();

    }
    public Page<ServiceResponse> getActiveServices(Pageable pageable) {

        Long salonId = SecurityUtil.getCurrentSalonId();
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon not found"));

        return serviceRepository.findBySalon_IdAndActiveTrue(salonId, pageable)
                .map(s -> new ServiceResponse(
                        s.getId(),
                        s.getName(),
                        s.getPrice(),
                        s.getDurationInMinutes()
                ));
    }
}
