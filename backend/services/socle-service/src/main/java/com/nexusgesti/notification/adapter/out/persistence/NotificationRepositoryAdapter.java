package com.nexusgesti.notification.adapter.out.persistence;

import com.nexusgesti.notification.adapter.out.persistence.mapper.NotificationMapper;
import com.nexusgesti.notification.domain.model.Notification;
import com.nexusgesti.notification.domain.port.out.NotificationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;

    public NotificationRepositoryAdapter(NotificationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Notification save(Notification notification) {
        var saved = jpaRepository.save(NotificationMapper.toEntity(notification));
        return NotificationMapper.toDomain(saved);
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return jpaRepository.findById(id).map(NotificationMapper::toDomain);
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationMapper::toDomain)
                .toList();
    }
}
