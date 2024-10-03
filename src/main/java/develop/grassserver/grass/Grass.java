package develop.grassserver.grass;

import develop.grassserver.BaseEntity;
import develop.grassserver.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE grass SET status = false WHERE id = ?")
@SQLRestriction("status = true")
public class Grass extends BaseEntity {

    @Column(name = "study_time_seconds", nullable = false)
    @ColumnDefault("0")
    private Duration studyTime = Duration.ZERO;

    @ColumnDefault("1")
    private int currentStreak = 1;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

}
