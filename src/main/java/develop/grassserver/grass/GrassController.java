package develop.grassserver.grass;

import develop.grassserver.member.Member;
import develop.grassserver.utils.annotation.LoginMember;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "스트릭(잔디) APIs", description = "공부시간 등록/조회, 잔디 조회, 출석 등을 담당하는 APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/grass")
public class GrassController {
    @GetMapping
    public ResponseEntity<String> test(@LoginMember Member member) {
        return ResponseEntity.ok("Hello" + member.getName());
    }
}
