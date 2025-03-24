package org.khanhpham.wms.service;

import org.khanhpham.wms.domain.dto.NotificationDTO;
import org.khanhpham.wms.domain.request.NotificationRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;

public interface NotificationService {
    NotificationDTO getNotificationWithProduct(long productId);
    PaginationResponse<NotificationDTO> getAllNotifications(int pageNo, int pageSize, String sortBy, String sortDir);
    NotificationDTO getNotificationById(long id);
    NotificationDTO createNotification(NotificationRequest request);
    NotificationDTO updateNotification(long notificationId, NotificationRequest notificationRequest);
    void deleteNotification(long id);
    PaginationResponse<NotificationDTO> searchNotification(String title, int pageNo, int pageSize, String sortBy, String sortDir);
}
