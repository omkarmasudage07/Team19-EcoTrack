package com.ecotrack.notification.service;

import com.ecotrack.notification.entity.Notification;
import com.ecotrack.notification.enums.NotificationType;
import com.ecotrack.notification.enums.RoleType;
import com.ecotrack.notification.exception.BusinessException;
import com.ecotrack.notification.repository.AuditLogRepository;
import com.ecotrack.notification.repository.NotificationRepository;
import com.ecotrack.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void notify_savesANotificationForTheGivenUser() {
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);

        notificationService.notify(1L, RoleType.CITIZEN, "Pickup Accepted", "Details here", NotificationType.PICKUP_ACCEPTED);

        verify(notificationRepository).save(captor.capture());
        Notification saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getUserRole()).isEqualTo(RoleType.CITIZEN);
        assertThat(saved.isRead()).isFalse();
    }

    @Test
    void markAsRead_rejectsWhenNotificationBelongsToSomeoneElse() {
        Notification notification = Notification.builder().id(1L).userId(100L).build();
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.markAsRead(1L, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("your own notifications");
    }
}
