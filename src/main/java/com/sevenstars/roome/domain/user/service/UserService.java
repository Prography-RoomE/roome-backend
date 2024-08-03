package com.sevenstars.roome.domain.user.service;

import com.sevenstars.roome.domain.common.repository.ForbiddenWordRepository;
import com.sevenstars.roome.domain.common.service.StorageService;
import com.sevenstars.roome.domain.profile.entity.Profile;
import com.sevenstars.roome.domain.profile.entity.ProfileState;
import com.sevenstars.roome.domain.profile.repository.ProfileElementRepository;
import com.sevenstars.roome.domain.profile.repository.ProfileRepository;
import com.sevenstars.roome.domain.profile.response.ProfileResponse;
import com.sevenstars.roome.domain.profile.service.ProfileService;
import com.sevenstars.roome.domain.user.entity.TermsAgreement;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.entity.UserDeactivationReason;
import com.sevenstars.roome.domain.user.repository.TermsAgreementRepository;
import com.sevenstars.roome.domain.user.repository.UserDeactivationReasonRepository;
import com.sevenstars.roome.domain.user.repository.UserRepository;
import com.sevenstars.roome.domain.user.request.NicknameRequest;
import com.sevenstars.roome.domain.user.request.TermsAgreementRequest;
import com.sevenstars.roome.domain.user.request.UserRequest;
import com.sevenstars.roome.domain.user.response.UserImageResponse;
import com.sevenstars.roome.domain.user.response.UserResponse;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import com.sevenstars.roome.global.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.sevenstars.roome.global.common.response.Result.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private static final String USERS_IMAGES_PATH = "/users/images";
    private final ProfileService profileService;
    private final StorageService storageService;
    private final UserRepository userRepository;
    private final UserDeactivationReasonRepository userDeactivationReasonRepository;
    private final TermsAgreementRepository termsAgreementRepository;
    private final ProfileRepository profileRepository;
    private final ProfileElementRepository profileElementRepository;
    private final ForbiddenWordRepository forbiddenWordRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public User saveOrUpdate(UserRequest request) {
        String serviceId = request.getServiceId();
        String serviceUserId = request.getServiceUserId();
        String email = request.getEmail();

        Optional<User> optionalUser = userRepository.findByServiceIdAndServiceUserId(serviceId, serviceUserId);

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
    public void deactivate(Long id, String reason, String content) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        // Image
        String imageUrl = user.getImageUrl();
        storageService.deleteImage(USERS_IMAGES_PATH, imageUrl);

        // TermsAgreement
        termsAgreementRepository.findByUser(user).ifPresent(termsAgreementRepository::delete);

        // ProfileElement, Profile
        profileRepository.findByUser(user)
                .ifPresent(profile -> {
                            profileElementRepository.deleteAll(profileElementRepository.findByProfile(profile));
                            profileRepository.delete(profile);
                        }
                );

        // RefreshToken
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        // User
        userRepository.delete(user);

        if (StringUtils.hasText(reason) || StringUtils.hasText(content)) {
            userDeactivationReasonRepository.save(new UserDeactivationReason(reason, content));
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public boolean isNicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
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

        User user = userRepository.findById(id)
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

        User user = userRepository.findById(id)
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

        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        validateNickname(id, request);

        user.updateNickname(nickname);
    }

    @Transactional
    public UserImageResponse updateImage(Long id, MultipartFile file) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        storageService.validateImage(file);

        deleteImage(id);

        String imageUrl = storageService.saveImage(USERS_IMAGES_PATH, file);

        user.updateImageUrl(imageUrl);

        return new UserImageResponse(imageUrl);
    }

    @Transactional
    public void deleteImage(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        String imageUrl = user.getImageUrl();

        user.deleteImageUrl();

        storageService.deleteImage(USERS_IMAGES_PATH, imageUrl);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getUserProfile(String nickname) {

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        Long id = user.getId();

        ProfileResponse response = profileService.getProfile(id);

        if (!ProfileState.COMPLETE.equals(response.getState())) {
            throw new CustomClientErrorException(PROFILE_NOT_FOUND);
        }

        return response;
    }
}
