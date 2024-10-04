package develop.grassserver.member.dto;

import develop.grassserver.member.Member;
import develop.grassserver.member.profile.Profile;

public record MemberProfileResponse(MemberProfile member) {

    public static MemberProfileResponse from(Member member) {
        Profile profile = member.getProfile();
        return new MemberProfileResponse(
                new MemberProfile(
                        member.getId(),
                        member.getName(),
                        profile.getImage(),
                        profile.getMainTitle(),
                        profile.getMainBanner(),
                        profile.getFreeze().getFreezeCount(),
                        0, 0)
        );
    }

    public record MemberProfile(
            Long id,
            String name,
            String profileImage,
            String mainTitle,
            String mainBanner,
            int freezeCount,
            int friendCount,
            int studyCount
    ) {
    }
}