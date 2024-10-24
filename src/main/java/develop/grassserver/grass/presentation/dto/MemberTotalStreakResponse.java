package develop.grassserver.grass.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record MemberTotalStreakResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일")
        LocalDate createdDate,
        long totalStreak,
        long totalStudyTime
) {
}
