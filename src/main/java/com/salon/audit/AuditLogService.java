package com.salon.audit;

import com.salon.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void log(AuditAction action, String description) {

        AuditLog log = new AuditLog();
        log.setUserId(SecurityUtil.getCurrentUserId());
        log.setSalonId(SecurityUtil.getCurrentSalonId());
        log.setAction(action);
        log.setDescription(description);

        repository.save(log);
    }
    public Page<AuditLogResponse> getAuditLogs(Pageable pageable) {

        Long salonId = SecurityUtil.getCurrentSalonId();

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
