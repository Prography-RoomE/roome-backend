package com.sevenstars.roome.domain.user.entity;

import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.sevenstars.roome.global.common.response.Result.*;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class TermsAgreement {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Boolean ageOverFourteen;

    private Boolean serviceAgreement;

    private Boolean personalInfoAgreement;

    private Boolean marketingAgreement;

    public TermsAgreement(User user) {
        this.user = user;
        this.ageOverFourteen = false;
        this.serviceAgreement = false;
        this.personalInfoAgreement = false;
        this.marketingAgreement = false;
    }

    public void update(Boolean ageOverFourteen,
                       Boolean serviceAgreement,
                       Boolean personalInfoAgreement,
                       Boolean marketingAgreement) {

        if (!ageOverFourteen) {
            throw new CustomClientErrorException(USER_AGE_NOT_OVER_FOURTEEN);
        }

        if (!serviceAgreement) {
            throw new CustomClientErrorException(USER_DOES_NOT_AGREE_TERMS_OF_SERVICE);
        }

        if (!personalInfoAgreement) {
            throw new CustomClientErrorException(USER_DOES_NOT_AGREE_PERSONAL_INFO);
        }

        this.ageOverFourteen = true;
        this.serviceAgreement = true;
        this.personalInfoAgreement = true;
        this.marketingAgreement = marketingAgreement;
    }
}
