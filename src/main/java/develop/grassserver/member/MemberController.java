package develop.grassserver.member;

import develop.grassserver.member.auth.TokenDTO;
import develop.grassserver.member.dto.MemberJoinRequest;
import develop.grassserver.member.dto.MemberJoinSuccessResponse;
import develop.grassserver.member.login.JwtUserService;
import develop.grassserver.member.login.LoginRequest;
import develop.grassserver.utils.ApiUtils;
import develop.grassserver.utils.ApiUtils.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final JwtUserService jwtUserService;

    @PostMapping("/login")
    public ResponseEntity<ApiResult<String>> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenDTO token = jwtUserService.login(loginRequest);
        return ResponseEntity.ok()
                .header("Authorization", token.accessToken())
                .body(ApiUtils.success("로그인 성공"));
    }

    @PostMapping
    public ResponseEntity<ApiResult<MemberJoinSuccessResponse>> signUp(
            @RequestBody MemberJoinRequest request
    ) {
        MemberJoinSuccessResponse response = memberService.saveMember(request);
        return ResponseEntity.ok()
                .body(ApiUtils.success(response));
    }
}
