package develop.grassserver.notification.application.service;

import develop.grassserver.member.domain.entity.Member;
import develop.grassserver.notification.application.exception.ExceedSendEmojiCountException;
import develop.grassserver.notification.domain.entity.EmojiNotification;
import develop.grassserver.notification.infrastructure.repository.EmojiNotificationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmojiNotificationService {

    private final EmojiNotificationRepository emojiNotificationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveEmojiNotification(Member me, Member other, int emojiNumber) {
        checkTodaySentEmojiCount(me, other);

        EmojiNotification emojiNotification = createEmojiNotification(me, other, emojiNumber);
        emojiNotificationRepository.save(emojiNotification);
    }

    private void checkTodaySentEmojiCount(Member me, Member other) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59, 999999999);
        long todaySentEmojiCount =
                emojiNotificationRepository.findAllBySenderAndReceiverAndToday(me, other, startOfDay, endOfDay);
        if (todaySentEmojiCount >= 2) {
            throw new ExceedSendEmojiCountException();
        }
    }

    private EmojiNotification createEmojiNotification(Member me, Member other, int emojiNumber) {
        return new EmojiNotification(me, other, emojiNumber);
    }
}
