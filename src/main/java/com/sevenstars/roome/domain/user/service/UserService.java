package com.sevenstars.roome.domain.user.service;

import com.sevenstars.roome.domain.common.repository.ForbiddenWordRepository;
import com.sevenstars.roome.domain.user.entity.TermsAgreement;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.repository.TermsAgreementRepository;
import com.sevenstars.roome.domain.user.repository.UserRepository;
import com.sevenstars.roome.domain.user.request.NicknameRequest;
import com.sevenstars.roome.domain.user.request.TermsAgreementRequest;
import com.sevenstars.roome.domain.user.request.UserRequest;
import com.sevenstars.roome.domain.user.response.UserResponse;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.sevenstars.roome.global.common.response.Result.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final TermsAgreementRepository termsAgreementRepository;
    //TODO
    //private final ProfileRepository profileRepository;
    private final ForbiddenWordRepository forbiddenWordRepository;

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
            //TODO
            //Profile profile = new Profile(user);

            userRepository.save(user);
            termsAgreementRepository.save(termsAgreement);
            //TODO
            //profileRepository.save(profile);

            return user;
        } else {
            user = optionalUser.get();
            user.updateEmail(email);
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
}
