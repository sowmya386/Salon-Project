package com.salon.audit;

import com.salon.security.SecurityUtil;
import com.salon.service.SalonService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
public class AuditLogService {

    private final AuditLogRepository repository;
    private final SalonService salonService;

    public AuditLogService(AuditLogRepository repository, SalonService salonService) {
        this.repository = repository;
        this.salonService = salonService;
    }

    public void log(AuditAction action, String description) {

        AuditLog log = new AuditLog();
        log.setUserId(SecurityUtil.getCurrentUserId());
        log.setSalonId(salonService.getCurrentSalon().getId());
        log.setAction(action);
        log.setDescription(description);

        repository.save(log);
    }
    public Page<AuditLogResponse> getAuditLogs(Pageable pageable) {
        Long salonId = salonService.getCurrentSalon().getId();

        return repository.findBySalonId(salonId, pageable)
                .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getUserId(),
                        log.getAction().name(),
                        log.getDescription(),
                        log.getCreatedAt()
                ));
    }
}
