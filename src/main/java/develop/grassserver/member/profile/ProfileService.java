package develop.grassserver.member.profile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import develop.grassserver.member.Member;
import develop.grassserver.member.MemberRepository;
import develop.grassserver.member.profile.banner.Banner;
import develop.grassserver.member.profile.banner.BannerRepository;
import develop.grassserver.member.profile.dto.DefaultProfileImagesResponse;
import develop.grassserver.member.profile.exeption.ImageUploadFailedException;
import develop.grassserver.member.profile.image.DefaultImage;
import develop.grassserver.member.profile.image.Image;
import develop.grassserver.member.profile.image.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private static final String PROFILE_BANNER_SAVE_PATH_PREFIX = "profile/banners/";
    private static final String PROFILE_IMAGE_SAVE_PATH_PREFIX = "profile/images/";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3Client;
    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;
    private final BannerRepository bannerRepository;

    public DefaultProfileImagesResponse getDefaultProfileImages() {
        List<DefaultImage> images = DefaultImage.getDefaultImages();
        return DefaultProfileImagesResponse.from(images);
    }

    @Transactional
    public void saveBannerImage(MultipartFile image, Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(EntityNotFoundException::new);

        deleteExistingBanner(findMember);

        Banner banner = getUploadedBanner(image, findMember);
        bannerRepository.save(banner);
    }

    private void deleteExistingBanner(Member member) {
        Optional<Banner> optionalBanner = bannerRepository.findByMember(member);
        if (optionalBanner.isPresent()) {
            Banner banner = optionalBanner.get();
            String fileName = extractFileNameFromUrl(banner.getUrl());
            try {
                amazonS3Client.deleteObject(bucket, PROFILE_BANNER_SAVE_PATH_PREFIX + fileName);
            } catch (Exception e) {
                log.error("Failed to delete banner file from S3: {}", e.getMessage());
            }
            bannerRepository.delete(banner);
        }
    }

    private Banner getUploadedBanner(MultipartFile image, Member member) {
        String url = uploadBanner(image, member);
        return Banner.builder()
                .url(url)
                .member(member)
                .build();
    }

    private String uploadBanner(MultipartFile image, Member member) {
        String fileName = PROFILE_BANNER_SAVE_PATH_PREFIX + "profileBanner-" + member.getId();
        try (InputStream inputStream = image.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, null));
        } catch (IOException e) {
            throw new ImageUploadFailedException();
        }
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    @Transactional
    public void saveProfileImage(MultipartFile image, Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(EntityNotFoundException::new);

        deleteExistingProfileImage(findMember);

        Image uploadImage = getUploadedProfileImage(image, findMember);
        imageRepository.save(uploadImage);
    }

    private void deleteExistingProfileImage(Member member) {
        Optional<Image> optionalImage = imageRepository.findByMember(member);
        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();
            String fileName = extractFileNameFromUrl(image.getUrl());
            try {
                amazonS3Client.deleteObject(bucket, PROFILE_IMAGE_SAVE_PATH_PREFIX + fileName);
            } catch (Exception e) {
                log.error("Failed to delete profile image file from S3: {}", e.getMessage());
            }
            imageRepository.delete(image);
        }
    }

    private Image getUploadedProfileImage(MultipartFile image, Member member) {
        String url = uploadProfileImage(image, member);
        return Image.builder()
                .url(url)
                .member(member)
                .build();
    }

    private String uploadProfileImage(MultipartFile image, Member member) {
        String fileName = PROFILE_IMAGE_SAVE_PATH_PREFIX + "profileImage-" + member.getId();
        try (InputStream inputStream = image.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, null));
        } catch (IOException e) {
            throw new ImageUploadFailedException();
        }
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private String extractFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
