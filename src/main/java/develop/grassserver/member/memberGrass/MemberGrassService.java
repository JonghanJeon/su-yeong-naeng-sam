package develop.grassserver.member.memberGrass;

import develop.grassserver.grass.Grass;
import develop.grassserver.grass.GrassService;
import develop.grassserver.member.Member;
import develop.grassserver.member.MemberService;
import develop.grassserver.member.memberGrass.dto.MemberStreakResponse;
import develop.grassserver.member.memberGrass.dto.YearlyGrassResponse;
import develop.grassserver.member.memberGrass.dto.YearlyTotalGrassResponse;
import develop.grassserver.utils.duration.DurationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberGrassService {
    private final MemberService memberService;
    private final GrassService grassService;

    public MemberStreakResponse getMemberStreak(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        Grass grass = grassService.findTodayGrassByMemberId(memberId);
        return new MemberStreakResponse(
                grass.getCurrentStreak(),
                member.getStudyRecord().getTopStreak(),
                DurationUtils.formatDuration(member.getStudyRecord().getTotalStudyTime())
        );
    }

    public YearlyTotalGrassResponse getYearlyGrass(Long memberId, int year) {
        Member member = memberService.findMemberById(memberId);
        return new YearlyTotalGrassResponse(
                year,
                grassService.findYearlyGrassByMemberId(member, year).stream()
                        .map(grass -> new YearlyGrassResponse(
                                grass.getId(),
                                grass.getCreatedAt().getMonthValue(),
                                grass.getCreatedAt().getDayOfMonth(),
                                DurationUtils.formatDuration(grass.getStudyTime())
                        ))
                        .toList()
        );
    }
}
