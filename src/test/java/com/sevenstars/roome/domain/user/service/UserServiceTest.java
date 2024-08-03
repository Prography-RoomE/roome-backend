package com.sevenstars.roome.domain.user.service;

import com.sevenstars.roome.domain.common.entity.ForbiddenWord;
import com.sevenstars.roome.domain.common.repository.ForbiddenWordRepository;
import com.sevenstars.roome.domain.review.entity.Review;
import com.sevenstars.roome.domain.review.repository.ReviewRepository;
import com.sevenstars.roome.domain.user.entity.TermsAgreement;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.entity.UserDeactivationReason;
import com.sevenstars.roome.domain.user.repository.TermsAgreementRepository;
import com.sevenstars.roome.domain.user.repository.UserDeactivationReasonRepository;
import com.sevenstars.roome.domain.user.repository.UserRepository;
import com.sevenstars.roome.domain.user.request.NicknameRequest;
import com.sevenstars.roome.domain.user.request.TermsAgreementRequest;
import com.sevenstars.roome.domain.user.request.UserRequest;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.sevenstars.roome.domain.user.entity.UserState.*;
import static org.assertj.core.api.Assertions.*;

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
    @Autowired
    UserDeactivationReasonRepository userDeactivationReasonRepository;
    @Autowired
    ReviewRepository reviewRepository;

    @DisplayName("닉네임에 금칙어가 포함되어 있는지 확인한다.")
    @Test
    void isNicknameForbiddenTest() {

        forbiddenWordRepository.save(new ForbiddenWord("놈"));
        forbiddenWordRepository.save(new ForbiddenWord("새끼"));

        assertThat(userService.isNicknameForbidden("x놈")).isTrue();
        assertThat(userService.isNicknameForbidden("X놈")).isTrue();
        assertThat(userService.isNicknameForbidden("x새끼")).isTrue();
        assertThat(userService.isNicknameForbidden("X새끼")).isTrue();
        assertThat(userService.isNicknameForbidden("18놈")).isTrue();
        assertThat(userService.isNicknameForbidden("개18놈")).isTrue();
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
        assertThat(optionalTermsAgreement).isPresent();
        assertThat(optionalTermsAgreement.get().getAgeOverFourteen()).isFalse();
        assertThat(optionalTermsAgreement.get().getServiceAgreement()).isFalse();
        assertThat(optionalTermsAgreement.get().getPersonalInfoAgreement()).isFalse();
        assertThat(optionalTermsAgreement.get().getMarketingAgreement()).isFalse();

        assertThat(user.getServiceId()).isEqualTo(serviceId);
        assertThat(user.getServiceUserId()).isEqualTo(serviceUserId);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo("");
        assertThat(user.getState()).isEqualTo(TERMS_AGREEMENT);
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
        assertThat(optionalTermsAgreement).isPresent();
        assertThat(optionalTermsAgreement.get().getAgeOverFourteen()).isTrue();
        assertThat(optionalTermsAgreement.get().getServiceAgreement()).isTrue();
        assertThat(optionalTermsAgreement.get().getPersonalInfoAgreement()).isTrue();
        assertThat(optionalTermsAgreement.get().getMarketingAgreement()).isFalse();
        assertThat(user.getState()).isEqualTo(NICKNAME);
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
        assertThatThrownBy(() -> userService.updateTermsAgreement(user.getId(), request))
                .isInstanceOf(CustomClientErrorException.class);

        request.setAgeOverFourteen(true);
        request.setServiceAgreement(false);
        request.setPersonalInfoAgreement(true);
        request.setMarketingAgreement(false);

        assertThatThrownBy(() -> userService.updateTermsAgreement(user.getId(), request))
                .isInstanceOf(CustomClientErrorException.class);

        request.setAgeOverFourteen(true);
        request.setServiceAgreement(true);
        request.setPersonalInfoAgreement(false);
        request.setMarketingAgreement(false);

        assertThatThrownBy(() -> userService.updateTermsAgreement(user.getId(), request))
                .isInstanceOf(CustomClientErrorException.class);

        assertThat(user.getState()).isEqualTo(TERMS_AGREEMENT);
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
        assertThatThrownBy(() -> user.validateNickname(null))
                .isInstanceOf(CustomClientErrorException.class);

        assertThatThrownBy(() -> user.validateNickname(""))
                .isInstanceOf(CustomClientErrorException.class);

        assertThatThrownBy(() -> user.validateNickname("한글특수문자!"))
                .isInstanceOf(CustomClientErrorException.class);

        assertThatThrownBy(() -> user.validateNickname("8자리초과닉네임입니다"))
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
        assertThatThrownBy(() -> userService.validateNickname(user.getId(), request))
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
        assertThatThrownBy(() -> userService.validateNickname(user.getId(), request))
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

        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get().getNickname()).isEqualTo(nickname);
        assertThat(optionalUser.get().getState()).isEqualTo(REGISTRATION_COMPLETED);
    }

    @DisplayName("탈퇴시 사용자 정보는 삭제되고 후기는 남아있어야한다.")
    @Test
    void deactivateTest() {

        // Given
        String serviceId = "google";
        String serviceUserId = "googleId";
        String email = "test@gmail.com";
        String nickname = "닉네임";
        User user = new User(serviceId, serviceUserId, email);
        user.updateNickname(nickname);
        user.updateState(REGISTRATION_COMPLETED);
        userRepository.save(user);

        TermsAgreement termsAgreement = new TermsAgreement(user);
        termsAgreement.update(true, true, true, false);
        termsAgreementRepository.save(termsAgreement);

        List<Review> reviews = List.of(new Review(user, 5.0, "제로월드 홍대점", "층간 소음"),
                new Review(user, 4.5, "티켓 투 이스케이프", "갤럭시 익스프레스"),
                new Review(user, 4.5, "오아시스", "배드 타임 (BÆD TIME)"));

        reviewRepository.saveAll(reviews);

        // When
        userService.deactivate(user.getId(), "기타", "그냥");

        // Then
        List<UserDeactivationReason> reasons = userDeactivationReasonRepository.findAll();
        assertThat(reasons.size()).isEqualTo(1);
        assertThat(reasons.get(0).getReason()).isEqualTo("기타");
        assertThat(reasons.get(0).getContent()).isEqualTo("그냥");

        assertThat(userRepository.findById(user.getId())).isEmpty();

        List<Review> foundReviews = reviewRepository.findAll();
        assertThat(foundReviews.size()).isEqualTo(3);
        assertThat(foundReviews.get(0).getUser()).isNull();
        assertThat(foundReviews.get(0).getThemeName()).isEqualTo(reviews.get(0).getThemeName());

        assertThat(foundReviews.get(1).getUser()).isNull();
        assertThat(foundReviews.get(1).getThemeName()).isEqualTo(reviews.get(1).getThemeName());

        assertThat(foundReviews.get(2).getUser()).isNull();
        assertThat(foundReviews.get(2).getThemeName()).isEqualTo(reviews.get(2).getThemeName());
    }
}
