package org.khanhpham.wms.service.impl;

import lombok.RequiredArgsConstructor;
import org.khanhpham.wms.domain.dto.NotificationDTO;
import org.khanhpham.wms.domain.entity.Notification;
import org.khanhpham.wms.domain.request.NotificationRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.NotificationRepository;
import org.khanhpham.wms.service.NotificationService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    private NotificationDTO convertToDTO(Notification notification) {
        return modelMapper.map(notification, NotificationDTO.class);
    }

    private Notification convertToEntity(NotificationRequest notificationRequest) {
        Notification notification = modelMapper.map(notificationRequest, Notification.class);
        notification.setTimestamp(Instant.now());
        return notification;
    }

    @Override
    public NotificationDTO getNotificationWithProduct(long productId) {
        return null;
    }

    @Override
    public PaginationResponse<NotificationDTO> getAllNotifications(int pageNo, int pageSize, String sortBy, String sortDir) {
        Page<Notification> notifications = notificationRepository.findAll(
                PaginationUtils.convertToPageable(pageNo, pageSize, sortBy, sortDir)
        );

        List<NotificationDTO> content = notifications.getContent()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, notifications);
    }

    @Override
    public NotificationDTO getNotificationById(long id) {
        return convertToDTO(findById(id));
    }

    @Override
    public NotificationDTO createNotification(NotificationRequest notificationRequest) {
        Notification notification = convertToEntity(notificationRequest);
        return convertToDTO(notificationRepository.save(notification));
    }

    @Override
    public NotificationDTO updateNotification(long notificationId, NotificationRequest notificationRequest) {
        Notification notification = findById(notificationId);
        notification.setTitle(notificationRequest.getTitle());
        notification.setMessage(notificationRequest.getMessage());
        notification.setTimestamp(Instant.now());
        return convertToDTO(notificationRepository.save(notification));
    }

    @Override
    public void deleteNotification(long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        notificationRepository.delete(notification);
    }

    @Override
    public PaginationResponse<NotificationDTO> searchNotification(String title, int pageNo, int pageSize, String sortBy, String sortDir) {
        Page<Notification> notifications = notificationRepository.searchByTitle(title,
                PaginationUtils.convertToPageable(pageNo, pageSize, sortBy, sortDir));

        List<NotificationDTO> content = notifications.getContent()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, notifications);
    }

    private Notification findById(long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
    }
}
