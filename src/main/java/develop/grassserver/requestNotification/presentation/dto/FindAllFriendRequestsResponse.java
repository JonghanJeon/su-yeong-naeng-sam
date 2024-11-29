package develop.grassserver.requestNotification.presentation.dto;

import develop.grassserver.common.utils.duration.DurationUtils;
import develop.grassserver.requestNotification.domain.entity.FriendRequestNotification;
import java.time.LocalDateTime;
import java.util.List;

public record FindAllFriendRequestsResponse(List<FriendRequest> friendRequests) {

    public static FindAllFriendRequestsResponse from(List<FriendRequestNotification> notifications) {
        return new FindAllFriendRequestsResponse(
                notifications.stream()
                        .map(FindAllFriendRequestsResponse::toFriendRequest)
                        .toList()
        );
    }

    private static FriendRequest toFriendRequest(FriendRequestNotification notification) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime createdAt = notification.getCreatedAt();

        boolean isToday = isSameDay(today, createdAt);
        String time = DurationUtils.formatNotificationTime(createdAt);
        String date = DurationUtils.formatNotificationDate(createdAt);

        return new FriendRequest(
                notification.getId(),
                notification.getFriend().getMember1().getName(),
                isToday,
                time,
                date
        );
    }

    private static boolean isSameDay(LocalDateTime today, LocalDateTime createdAt) {
        return today.getYear() == createdAt.getYear()
                && today.getMonthValue() == createdAt.getMonthValue()
                && today.getDayOfMonth() == createdAt.getDayOfMonth();
    }

    public record FriendRequest(
            Long id,
            String memberName,
            boolean isToday,
            String time,
            String date
    ) {
    }
}
