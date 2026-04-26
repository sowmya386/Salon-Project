package com.salon.service;

import com.salon.dto.ServiceRequest;



import com.salon.dto.ServiceResponse;
import com.salon.entity.Salon;
import com.salon.entity.Service;
import com.salon.repository.ServiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.salon.audit.AuditAction;
import com.salon.audit.AuditLogService;



import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final SalonService salonService;
    private final AuditLogService auditLogService;


    public ServiceService(ServiceRepository serviceRepository,
            SalonService salonService,
            AuditLogService auditLogService) {

			this.serviceRepository = serviceRepository;
			this.salonService = salonService;
			this.auditLogService = auditLogService;
	}


    // ADMIN ONLY
    public ServiceResponse createService(ServiceRequest request) {

        Salon salon = salonService.getCurrentSalon();

        Service service = new Service();
        service.setName(request.getName());
        service.setPrice(request.getPrice());
        service.setDurationInMinutes(request.getDurationInMinutes());
        service.setImageUrl(request.getImageUrl());
        service.setSalonName(salon.getName());

        service = serviceRepository.save(service);
        auditLogService.log(
        	    AuditAction.CREATE_SERVICE,
        	    "Admin created service ID " + service.getId()
        	);


        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getPrice(),
                service.getDurationInMinutes(),
                service.getImageUrl()
        );
    }

    public ServiceResponse updateService(Long serviceId, ServiceRequest request) {
        Salon salon = salonService.getCurrentSalon();
        Service service = serviceRepository.findByIdAndSalonName(serviceId, salon.getName())
                .orElseThrow(() -> new RuntimeException("Service not found"));
        
        service.setName(request.getName());
        service.setPrice(request.getPrice());
        service.setDurationInMinutes(request.getDurationInMinutes());
        service.setImageUrl(request.getImageUrl());
        service = serviceRepository.save(service);
        auditLogService.log(AuditAction.UPDATE_SERVICE, "Admin updated service ID " + service.getId());
        
        return new ServiceResponse(service.getId(), service.getName(), service.getPrice(), service.getDurationInMinutes(), service.getImageUrl());
    }

    // CUSTOMER + ADMIN
    public List<ServiceResponse> getActiveServices() {
        String salonName = salonService.getCurrentSalon().getName();

        return serviceRepository.findBySalonNameAndActiveTrue(salonName)
                .stream()
                .map(s -> new ServiceResponse(
                        s.getId(),
                        s.getName(),
                        s.getPrice(),
                        s.getDurationInMinutes(),
                        s.getImageUrl()
                ))
                .toList();

    }
    public Page<ServiceResponse> getActiveServices(Pageable pageable) {
        String salonName = salonService.getCurrentSalon().getName();

        return serviceRepository.findBySalonNameAndActiveTrue(salonName, pageable)
                .map(s -> new ServiceResponse(
                        s.getId(),
                        s.getName(),
                        s.getPrice(),
                        s.getDurationInMinutes(),
                        s.getImageUrl()
                ));
    }

    // ADMIN ONLY: soft-delete by deactivating service
    public ServiceResponse deactivateService(Long serviceId) {
        Salon salon = salonService.getCurrentSalon();

        Service service = serviceRepository.findByIdAndSalonName(serviceId, salon.getName())
                .orElseThrow(() -> new RuntimeException("Service not found in this salon"));

        service.setActive(false);
        service = serviceRepository.save(service);

        auditLogService.log(
                AuditAction.DEACTIVATE_SERVICE,
                "Admin deactivated service ID " + service.getId()
        );

        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getPrice(),
                service.getDurationInMinutes(),
                service.getImageUrl()
        );
    }
}
