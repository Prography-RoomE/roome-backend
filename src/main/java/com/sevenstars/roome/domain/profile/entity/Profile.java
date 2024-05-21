package com.sevenstars.roome.domain.profile.entity;

import com.sevenstars.roome.domain.common.entity.BaseTimeEntity;
import com.sevenstars.roome.domain.profile.entity.activity.Activity;
import com.sevenstars.roome.domain.profile.entity.color.Color;
import com.sevenstars.roome.domain.profile.entity.device.DeviceLockPreference;
import com.sevenstars.roome.domain.profile.entity.hint.HintUsagePreference;
import com.sevenstars.roome.domain.profile.entity.position.HorrorThemePosition;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

import static com.sevenstars.roome.domain.profile.entity.Mbti.NONE;
import static com.sevenstars.roome.domain.profile.entity.ProfileState.*;
import static com.sevenstars.roome.global.common.response.Result.PROFILE_ROOM_COUNT_POSITIVE_OR_ZERO;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Profile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "VARCHAR(255)")
    @Enumerated(STRING)
    private ProfileState state;

    private Integer count;

    private Boolean isPlusEnabled;

    @Column(columnDefinition = "VARCHAR(255)")
    @Enumerated(STRING)
    private Mbti mbti;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "horror_theme_position_id")
    private HorrorThemePosition horrorThemePosition;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "hint_usage_preference_id")
    private HintUsagePreference hintUsagePreference;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "device_lock_preference_id")
    private DeviceLockPreference deviceLockPreference;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "color_id")
    private Color color;

    public Profile(User user) {
        this.user = user;
        this.state = ROOM_COUNT;
        this.count = 0;
        this.isPlusEnabled = false;
        this.mbti = NONE;
        this.horrorThemePosition = null;
        this.hintUsagePreference = null;
        this.deviceLockPreference = null;
        this.activity = null;
        this.color = null;
    }

    public void clear() {
        this.state = ROOM_COUNT;
        this.count = 0;
        this.isPlusEnabled = false;
        this.mbti = NONE;
        this.horrorThemePosition = null;
        this.hintUsagePreference = null;
        this.deviceLockPreference = null;
        this.activity = null;
        this.color = null;
    }

    public void updateRoomCount(Integer count, Boolean isPlusEnabled) {
        updateStateIfMatch(ROOM_COUNT);
        if (count < 0) {
            throw new CustomClientErrorException(PROFILE_ROOM_COUNT_POSITIVE_OR_ZERO);
        }
        this.count = count;
        this.isPlusEnabled = isPlusEnabled;
    }

    public void updatePreferredGenres() {
        updateStateIfMatch(PREFERRED_GENRES);
    }

    public void updateMbti(Mbti mbti) {
        updateStateIfMatch(MBTI);
        this.mbti = mbti;
    }

    public void updateUserStrengths() {
        updateStateIfMatch(USER_STRENGTHS);
    }

    public void updateThemeImportantFactors() {
        updateStateIfMatch(THEME_IMPORTANT_FACTORS);
    }

    public void updateHorrorThemePosition(HorrorThemePosition position) {
        updateStateIfMatch(HORROR_THEME_POSITION);
        this.horrorThemePosition = position;
    }

    public void updateHintUsagePreference(HintUsagePreference preference) {
        updateStateIfMatch(HINT_USAGE_PREFERENCE);
        this.hintUsagePreference = preference;
    }

    public void updateDeviceLockPreference(DeviceLockPreference preference) {
        updateStateIfMatch(DEVICE_LOCK_PREFERENCE);
        this.deviceLockPreference = preference;
    }

    public void updateActivity(Activity activity) {
        updateStateIfMatch(ACTIVITY);
        this.activity = activity;
    }

    public void updateThemeDislikedFactors() {
        updateStateIfMatch(THEME_DISLIKED_FACTORS);
    }

    public void updateColor(Color color) {
        updateStateIfMatch(COLOR);
        this.color = color;
    }

    private void updateStateIfMatch(ProfileState currentState) {
        if (this.state.equals(currentState)) {
            Arrays.stream(ProfileState.values())
                    .filter(state -> state.getOrder() == this.state.getOrder() + 1)
                    .findAny().ifPresent(state -> this.state = state);
        }
    }
}
