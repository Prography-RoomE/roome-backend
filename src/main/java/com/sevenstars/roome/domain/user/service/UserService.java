package com.sevenstars.roome.domain.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sevenstars.roome.domain.common.repository.ForbiddenWordRepository;
import com.sevenstars.roome.domain.profile.entity.Profile;
import com.sevenstars.roome.domain.profile.repository.ProfileRepository;
import com.sevenstars.roome.domain.user.entity.TermsAgreement;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.repository.TermsAgreementRepository;
import com.sevenstars.roome.domain.user.repository.UserRepository;
import com.sevenstars.roome.domain.user.request.NicknameRequest;
import com.sevenstars.roome.domain.user.request.TermsAgreementRequest;
import com.sevenstars.roome.domain.user.request.UserRequest;
import com.sevenstars.roome.domain.user.response.UserImageResponse;
import com.sevenstars.roome.domain.user.response.UserResponse;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import com.sevenstars.roome.global.common.exception.CustomServerErrorException;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.sevenstars.roome.global.common.response.Result.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private static final String USER_IMAGE_PATH = "/users/images";
    private final UserRepository userRepository;
    private final TermsAgreementRepository termsAgreementRepository;
    private final ProfileRepository profileRepository;
    private final ForbiddenWordRepository forbiddenWordRepository;
    private final AmazonS3 amazonS3;
    private final Tika tika = new Tika();
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public User saveOrUpdate(UserRequest request) {
        String serviceId = request.getServiceId();
        String serviceUserId = request.getServiceUserId();
        String email = request.getEmail();

        Optional<User> optionalUser = userRepository.findByServiceIdAndServiceUserIdAndWithdrawalIsFalse(serviceId, serviceUserId);

        User user;
        if (optionalUser.isEmpty()) {
            user = new User(serviceId, serviceUserId, email);
            TermsAgreement termsAgreement = new TermsAgreement(user);
            Profile profile = new Profile(user);

            userRepository.save(user);
            termsAgreementRepository.save(termsAgreement);
            profileRepository.save(profile);

            return user;
        } else {
            user = optionalUser.get();
            user.updateEmail(email);

            if (profileRepository.findByUser(user).isEmpty()) {
                Profile profile = new Profile(user);
                profileRepository.save(profile);
            }

            if (termsAgreementRepository.findByUser(user).isEmpty()) {
                TermsAgreement termsAgreement = new TermsAgreement(user);
                termsAgreementRepository.save(termsAgreement);
            }
        }

        return user;
    }

    @Transactional
    public void withdraw(Long id) {
        User user = userRepository.findByIdAndWithdrawalIsFalse(id)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        user.withdraw();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        User user = userRepository.findByIdAndWithdrawalIsFalse(id)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public boolean isNicknameExists(String nickname) {
        return userRepository.existsByNicknameAndWithdrawalIsFalse(nickname);
    }

    @Transactional(readOnly = true)
    public boolean isNicknameForbidden(String nickname) {
        return forbiddenWordRepository.hasForbiddenWordsInNickname(nickname);
    }

    @Transactional
    public void updateTermsAgreement(Long id, TermsAgreementRequest request) {

        Boolean ageOverFourteen = request.getAgeOverFourteen();
        Boolean serviceAgreement = request.getServiceAgreement();
        Boolean personalInfoAgreement = request.getPersonalInfoAgreement();
        Boolean marketingAgreement = request.getMarketingAgreement();

        User user = userRepository.findByIdAndWithdrawalIsFalse(id)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        TermsAgreement termsAgreement = termsAgreementRepository.findByUser(user)
                .orElseThrow(() -> new CustomClientErrorException(USER_TERMS_AGREEMENT_NOT_FOUND));

        user.updateTermsAgreement(termsAgreement,
                ageOverFourteen,
                serviceAgreement,
                personalInfoAgreement,
                marketingAgreement);
    }

    @Transactional(readOnly = true)
    public void validateNickname(Long id, NicknameRequest request) {

        String nickname = request.getNickname();

        User user = userRepository.findByIdAndWithdrawalIsFalse(id)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        user.validateNickname(nickname);

        if (isNicknameExists(nickname)) {
            throw new CustomClientErrorException(USER_NICKNAME_ALREADY_USING);
        }

        if (isNicknameForbidden(nickname)) {
            throw new CustomClientErrorException(USER_NICKNAME_NOT_ALLOWED);
        }
    }

    @Transactional
    public void updateNickname(Long id, NicknameRequest request) {

        String nickname = request.getNickname();

        User user = userRepository.findByIdAndWithdrawalIsFalse(id)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        validateNickname(id, request);

        user.updateNickname(nickname);
    }

    @Transactional
    public UserImageResponse updateImage(Long id, MultipartFile file) {

        User user = userRepository.findByIdAndWithdrawalIsFalse(id)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        String originalFilename = file.getOriginalFilename();

        if (!StringUtils.hasText(originalFilename) || !isImage(file)) {
            throw new CustomClientErrorException(INVALID_IMAGE_FILE);
        }

        String extension = getExtension(originalFilename).toLowerCase();

        if (!extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("png")) {
            throw new CustomClientErrorException(INVALID_IMAGE_FILE_EXTENSION);
        }

        String userImageBucket = bucket + USER_IMAGE_PATH;
        String fileName = UUID.randomUUID() + "." + extension;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        deleteImage(id);

        try {
            amazonS3.putObject(userImageBucket, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new CustomServerErrorException(FILE_UPLOAD_FAILED);
        }

        String imageUrl = amazonS3.getUrl(userImageBucket, fileName).toString();
        user.updateImageUrl(imageUrl);

        return new UserImageResponse(imageUrl);
    }

    @Transactional
    public void deleteImage(Long id) {

        User user = userRepository.findByIdAndWithdrawalIsFalse(id)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        String imageUrl = user.getImageUrl();

        if (StringUtils.hasText(imageUrl)) {
            String existFileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            String userImageBucket = bucket + USER_IMAGE_PATH;
            amazonS3.deleteObject(userImageBucket, existFileName);
            user.deleteImageUrl();
        }
    }

    private boolean isImage(MultipartFile file) {
        try {
            String mimeType = tika.detect(file.getInputStream());
            return mimeType.startsWith("image/");
        } catch (IOException e) {
            return false;
        }
    }

    private String getExtension(String filename) {
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            throw new CustomClientErrorException(FILE_EXTENSION_DOES_NOT_EXIST);
        }

        String extension = filename.substring(index + 1);
        if (!StringUtils.hasText(extension)) {
            throw new CustomClientErrorException(FILE_EXTENSION_DOES_NOT_EXIST);
        }

        return extension;
    }
}
