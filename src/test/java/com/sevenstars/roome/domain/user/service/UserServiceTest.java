package com.sevenstars.roome.domain.user.service;

import com.sevenstars.roome.domain.common.entity.ForbiddenWord;
import com.sevenstars.roome.domain.common.repository.ForbiddenWordRepository;
import com.sevenstars.roome.domain.user.entity.TermsAgreement;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.repository.TermsAgreementRepository;
import com.sevenstars.roome.domain.user.repository.UserRepository;
import com.sevenstars.roome.domain.user.request.NicknameRequest;
import com.sevenstars.roome.domain.user.request.TermsAgreementRequest;
import com.sevenstars.roome.domain.user.request.UserRequest;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.sevenstars.roome.domain.user.entity.UserState.*;

@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TermsAgreementRepository termsAgreementRepository;

    @Autowired
    ForbiddenWordRepository forbiddenWordRepository;

    @DisplayName("닉네임에 금칙어가 포함되어 있는지 확인한다.")
    @Test
    void isNicknameForbiddenTest() {

        forbiddenWordRepository.save(new ForbiddenWord("놈"));
        forbiddenWordRepository.save(new ForbiddenWord("새끼"));

        Assertions.assertThat(userService.isNicknameForbidden("x놈")).isTrue();
        Assertions.assertThat(userService.isNicknameForbidden("X놈")).isTrue();
        Assertions.assertThat(userService.isNicknameForbidden("x새끼")).isTrue();
        Assertions.assertThat(userService.isNicknameForbidden("X새끼")).isTrue();
        Assertions.assertThat(userService.isNicknameForbidden("18놈")).isTrue();
        Assertions.assertThat(userService.isNicknameForbidden("개18놈")).isTrue();
    }

    @DisplayName("사용자 생성시 약관 동의가 생성된다. 사용자 상태가 약관 동의로 초기화된다.")
    @Test
    void saveOrUpdateTest() {

        // Given
        String serviceId = "google";
        String serviceUserId = "googleId";
        String email = "test@gmail.com";
        UserRequest request = new UserRequest(serviceId, serviceUserId, email);

        // When
        User user = userService.saveOrUpdate(request);

        // Then
        Optional<TermsAgreement> optionalTermsAgreement = termsAgreementRepository.findByUser(user);
        Assertions.assertThat(optionalTermsAgreement).isPresent();
        Assertions.assertThat(optionalTermsAgreement.get().getAgeOverFourteen()).isFalse();
        Assertions.assertThat(optionalTermsAgreement.get().getServiceAgreement()).isFalse();
        Assertions.assertThat(optionalTermsAgreement.get().getPersonalInfoAgreement()).isFalse();
        Assertions.assertThat(optionalTermsAgreement.get().getMarketingAgreement()).isFalse();

        Assertions.assertThat(user.getServiceId()).isEqualTo(serviceId);
        Assertions.assertThat(user.getServiceUserId()).isEqualTo(serviceUserId);
        Assertions.assertThat(user.getEmail()).isEqualTo(email);
        Assertions.assertThat(user.getNickname()).isEqualTo("");
        Assertions.assertThat(user.getWithdrawal()).isFalse();
        Assertions.assertThat(user.getState()).isEqualTo(TERMS_AGREEMENT);
    }

    @DisplayName("약관 동의 저장시 필수 항목에 동의가 되면 성공한다. 사용자 상태가 닉네임으로 변경된다.")
    @Test
    void updateTermsAgreementTest1() {

        // Given
        String serviceId = "google";
        String serviceUserId = "googleId";
        String email = "test@gmail.com";
        User user = new User(serviceId, serviceUserId, email);
        userRepository.save(user);

        TermsAgreement termsAgreement = new TermsAgreement(user);
        termsAgreementRepository.save(termsAgreement);

        TermsAgreementRequest request = new TermsAgreementRequest();
        request.setAgeOverFourteen(true);
        request.setServiceAgreement(true);
        request.setPersonalInfoAgreement(true);
        request.setMarketingAgreement(false);

        // When
        userService.updateTermsAgreement(user.getId(), request);

        // Then
        Optional<TermsAgreement> optionalTermsAgreement = termsAgreementRepository.findByUser(user);
        Assertions.assertThat(optionalTermsAgreement).isPresent();
        Assertions.assertThat(optionalTermsAgreement.get().getAgeOverFourteen()).isTrue();
        Assertions.assertThat(optionalTermsAgreement.get().getServiceAgreement()).isTrue();
        Assertions.assertThat(optionalTermsAgreement.get().getPersonalInfoAgreement()).isTrue();
        Assertions.assertThat(optionalTermsAgreement.get().getMarketingAgreement()).isFalse();
        Assertions.assertThat(user.getState()).isEqualTo(NICKNAME);
    }

    @DisplayName("약관 동의 저장시 필수 항목에 동의가 안되면 실패한다.")
    @Test
    void updateTermsAgreementTest2() {

        // Given
        String serviceId = "google";
        String serviceUserId = "googleId";
        String email = "test@gmail.com";
        User user = new User(serviceId, serviceUserId, email);
        userRepository.save(user);

        TermsAgreement termsAgreement = new TermsAgreement(user);
        termsAgreementRepository.save(termsAgreement);

        TermsAgreementRequest request = new TermsAgreementRequest();
        request.setAgeOverFourteen(false);
        request.setServiceAgreement(true);
        request.setPersonalInfoAgreement(true);
        request.setMarketingAgreement(false);

        // When & Then
        Assertions.assertThatThrownBy(() -> userService.updateTermsAgreement(user.getId(), request))
                .isInstanceOf(CustomClientErrorException.class);

        request.setAgeOverFourteen(true);
        request.setServiceAgreement(false);
        request.setPersonalInfoAgreement(true);
        request.setMarketingAgreement(false);

        Assertions.assertThatThrownBy(() -> userService.updateTermsAgreement(user.getId(), request))
                .isInstanceOf(CustomClientErrorException.class);

        request.setAgeOverFourteen(true);
        request.setServiceAgreement(true);
        request.setPersonalInfoAgreement(false);
        request.setMarketingAgreement(false);

        Assertions.assertThatThrownBy(() -> userService.updateTermsAgreement(user.getId(), request))
                .isInstanceOf(CustomClientErrorException.class);

        Assertions.assertThat(user.getState()).isEqualTo(TERMS_AGREEMENT);
    }

    @DisplayName("사용자 닉네임에 빈 문자열, 한글, 영어, 숫자를 제외한 문자는 입력할 수 없다.")
    @Test
    void validateNicknameTest1() {

        // Given
        String serviceId = "google";
        String serviceUserId = "googleId";
        String email = "test@gmail.com";
        User user = new User(serviceId, serviceUserId, email);
        userRepository.save(user);

        // When & Then
        Assertions.assertThatThrownBy(() -> user.validateNickname(null))
                .isInstanceOf(CustomClientErrorException.class);

        Assertions.assertThatThrownBy(() -> user.validateNickname(""))
                .isInstanceOf(CustomClientErrorException.class);

        Assertions.assertThatThrownBy(() -> user.validateNickname("한글특수문자!"))
                .isInstanceOf(CustomClientErrorException.class);

        Assertions.assertThatThrownBy(() -> user.validateNickname("8자리초과닉네임입니다"))
                .isInstanceOf(CustomClientErrorException.class);
    }

    @DisplayName("중복된 닉네임을 입력할 수 없다.")
    @Test
    void validateNicknameTest2() {

        // Given
        String serviceId = "google";
        String serviceUserId = "googleId";
        String email = "test@gmail.com";
        String nickname = "닉네임";
        User user = new User(serviceId, serviceUserId, email);
        user.updateNickname(nickname);
        userRepository.save(user);

        NicknameRequest request = new NicknameRequest();
        request.setNickname(nickname);

        // When & Then
        Assertions.assertThatThrownBy(() -> userService.validateNickname(user.getId(), request))
                .isInstanceOf(CustomClientErrorException.class);
    }

    @DisplayName("금칙어 닉네임을 입력할 수 없다.")
    @Test
    void validateNicknameTest3() {

        // Given
        String nickname = "닉네임";
        forbiddenWordRepository.save(new ForbiddenWord(nickname));

        String serviceId = "google";
        String serviceUserId = "googleId";
        String email = "test@gmail.com";
        User user = new User(serviceId, serviceUserId, email);
        userRepository.save(user);

        NicknameRequest request = new NicknameRequest();
        request.setNickname("A" + nickname + "A");

        // When & Then
        Assertions.assertThatThrownBy(() -> userService.validateNickname(user.getId(), request))
                .isInstanceOf(CustomClientErrorException.class);
    }

    @DisplayName("닉네임 저장시 사용자 상태가 완료로 변경된다.")
    @Test
    void validateNicknameTest4() {

        // Given
        String serviceId = "google";
        String serviceUserId = "googleId";
        String email = "test@gmail.com";
        String nickname = "닉네임";
        User user = new User(serviceId, serviceUserId, email);
        user.updateState(NICKNAME);
        userRepository.save(user);

        NicknameRequest request = new NicknameRequest();
        request.setNickname(nickname);

        // When
        userService.updateNickname(user.getId(), request);

        // Then
        Optional<User> optionalUser = userRepository.findById(user.getId());

        Assertions.assertThat(optionalUser).isPresent();
        Assertions.assertThat(optionalUser.get().getNickname()).isEqualTo(nickname);
        Assertions.assertThat(optionalUser.get().getState()).isEqualTo(REGISTRATION_COMPLETED);
    }
}
