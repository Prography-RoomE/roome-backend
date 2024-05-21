package com.sevenstars.roome.domain.profile.entity.important;

import com.sevenstars.roome.domain.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class ThemeImportantFactor {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "important_factor_id")
    private ImportantFactor importantFactor;

    public ThemeImportantFactor(Profile profile, ImportantFactor importantFactor) {
        this.profile = profile;
        this.importantFactor = importantFactor;
    }
}
