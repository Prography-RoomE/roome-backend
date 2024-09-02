package com.sevenstars.roome.domain.user.entity;

import com.sevenstars.roome.domain.common.entity.BaseTimeEntity;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.sevenstars.roome.domain.user.entity.UserState.*;
import static com.sevenstars.roome.global.common.response.Result.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(255)")
    @Enumerated(STRING)
    private UserState state;

    private String serviceId;

    private String serviceUserId;

    private String email;

    private String nickname;

    private String imageUrl;

    public User(String serviceId, String serviceUserId, String email) {
        this.state = TERMS_AGREEMENT;
        this.serviceId = serviceId;
        this.serviceUserId = serviceUserId;
        this.email = email;
        this.nickname = "";
        this.imageUrl = "";
    }

    public void updateNickname(String nickname) {

        if (NICKNAME.equals(state)) {
            this.state = REGISTRATION_COMPLETED;
        }
        this.nickname = nickname;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void deleteImageUrl() {
        this.imageUrl = "";
    }

    public void updateState(UserState state) {
        this.state = state;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void validateNickname(String nickname) {

        if (nickname == null || nickname.isEmpty()) {
            throw new CustomClientErrorException(USER_NICKNAME_EMPTY);
        }

        if (!nickname.matches("^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]+$")) {
            throw new CustomClientErrorException(USER_NICKNAME_CAN_CONTAIN_KOREAN_ENGLISH_OR_NUMBERS);
        }

        if (nickname.length() > 8) {
            throw new CustomClientErrorException(USER_NICKNAME_LENGTH_EXCEEDED);
        }
    }

    public void updateTermsAgreement(TermsAgreement termsAgreement,
                                     Boolean ageOverFourteen,
                                     Boolean serviceAgreement,
                                     Boolean personalInfoAgreement,
                                     Boolean marketingAgreement) {
        termsAgreement.update(ageOverFourteen,
                serviceAgreement,
                personalInfoAgreement,
                marketingAgreement);
        if (this.state.equals(TERMS_AGREEMENT)) {
            this.state = NICKNAME;
        }
    }
}
