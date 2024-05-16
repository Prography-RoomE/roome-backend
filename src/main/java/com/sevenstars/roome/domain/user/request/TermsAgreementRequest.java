package com.sevenstars.roome.domain.user.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TermsAgreementRequest {
    @NotNull
    private Boolean ageOverFourteen;
    @NotNull
    private Boolean serviceAgreement;
    @NotNull
    private Boolean personalInfoAgreement;
    @NotNull
    private Boolean marketingAgreement;
}
