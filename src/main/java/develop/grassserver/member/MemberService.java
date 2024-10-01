package develop.grassserver.member;

import develop.grassserver.mail.MailService;
import develop.grassserver.member.dto.ChangePasswordRequest;
import develop.grassserver.member.dto.MemberAuthRequest;
import develop.grassserver.member.dto.MemberJoinRequest;
import develop.grassserver.member.dto.MemberJoinSuccessResponse;
import develop.grassserver.member.exception.UnauthorizedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private static final String DEFAULT_PROFILE_IMAGE =
            "https://grass-bucket.s3.us-east-2.amazonaws.com/%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%91%E1%85%B5%E1%86%AF%E1%84%8B%E1%85%B5%E1%84%86%E1%85%B5%E1%84%8C%E1%85%B5.png";
    private static final String DEFAULT_PROFILE_MESSAGE = "오늘 하루도 화이팅!";

    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    public MemberJoinSuccessResponse saveMember(MemberJoinRequest request) {
        Member member = createJoinMember(request);
        memberRepository.save(member);
        return MemberJoinSuccessResponse.from(member);
    }

    private Member createJoinMember(MemberJoinRequest request) {
        return Member.builder()
                .name(request.name())
                .email(request.email())
                .major(new Major(request.college(), request.department()))
                .profile(createMemberProfile())
                .password(passwordEncoder.encode(request.password()))
                .build();
    }

    private Profile createMemberProfile() {
        return Profile.builder()
                .image(DEFAULT_PROFILE_IMAGE)
                .message(DEFAULT_PROFILE_MESSAGE)
                .build();
    }

    public void authMember(MemberAuthRequest request) {
        Member member = checkMember(request.email(), request.name());
        mailService.sendMail(member.getEmail());
    }

    @Transactional
    public void changeMemberPassword(ChangePasswordRequest request) {
        Member member = checkMember(request.email(), request.name());
        String newPassword = passwordEncoder.encode(request.password());
        member.updatePassword(newPassword);
    }

    private Member checkMember(String email, String name) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(EntityNotFoundException::new);
        if (!member.isMyName(name)) {
            throw new UnauthorizedException("멤버가 인증되지 않음");
        }
        return member;
    }
}
