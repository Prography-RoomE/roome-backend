package com.sevenstars.roome.domain.profile.entity;

import com.sevenstars.roome.domain.common.entity.BaseTimeEntity;
import com.sevenstars.roome.domain.profile.entity.color.Color;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

import static com.sevenstars.roome.domain.profile.entity.Mbti.NONE;
import static com.sevenstars.roome.domain.profile.entity.ProfileState.*;
import static com.sevenstars.roome.global.common.response.Result.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Profile extends BaseTimeEntity {

    private static final int MAX_COUNT = 99999;

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

    private Integer minCount;

    private Integer maxCount;

    private Boolean isCountRange;

    @Column(columnDefinition = "VARCHAR(255)")
    @Enumerated(STRING)
    private Mbti mbti;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "color_id")
    private Color color;

    public Profile(User user) {
        this.user = user;
        this.state = ROOM_COUNT;
        this.count = 0;
        this.minCount = 0;
        this.maxCount = 0;
        this.isCountRange = false;
        this.mbti = NONE;
        this.color = null;
    }

    public void clear() {
        this.state = ROOM_COUNT;
        this.count = 0;
        this.minCount = 0;
        this.maxCount = 0;
        this.isCountRange = false;
        this.mbti = NONE;
        this.color = null;
    }

    public String getCount() {
        if (isCountRange) {
            if (maxCount >= MAX_COUNT) {
                return minCount + "~";
            }
            return minCount + "~" + maxCount;
        }
        return count.toString();
    }

    public void updateRoomCount(Integer count) {
        updateRoomCountState();
        if (count < 0) {
            throw new CustomClientErrorException(PROFILE_ROOM_COUNT_POSITIVE_OR_ZERO);
        }
        if (count > 99999) {
            throw new CustomClientErrorException(PROFILE_ROOM_COUNT_EXCEEDED);
        }
        this.count = count;
        this.minCount = 0;
        this.maxCount = 0;
        this.isCountRange = false;
    }

    public void updateRoomCountRange(Integer minCount, Integer maxCount) {
        updateRoomCountState();
        if (minCount > maxCount) {
            throw new CustomClientErrorException(PROFILE_INVALID_ROOM_COUNT_RANGE);
        }
        if (minCount < 0) {
            throw new CustomClientErrorException(PROFILE_ROOM_COUNT_POSITIVE_OR_ZERO);
        }
        if (minCount > 99999 || maxCount > 99999) {
            throw new CustomClientErrorException(PROFILE_ROOM_COUNT_EXCEEDED);
        }
        this.count = 0;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.isCountRange = true;
    }

    public void updateMbti(Mbti mbti) {
        updateMbtiState();
        this.mbti = mbti;
    }

    public void updateColor(Color color) {
        updateColorState();
        this.color = color;
    }

    public void updateRoomCountState() {
        updateStateIfMatch(ROOM_COUNT);
    }

    public void updateMbtiState() {
        updateStateIfMatch(MBTI);
    }

    public void updatePreferredGenresState() {
        updateStateIfMatch(PREFERRED_GENRES);
    }

    public void updateUserStrengthsState() {
        updateStateIfMatch(USER_STRENGTHS);
    }

    public void updateThemeImportantFactorsState() {
        updateStateIfMatch(THEME_IMPORTANT_FACTORS);
    }

    public void updateHorrorThemePositionState() {
        updateStateIfMatch(HORROR_THEME_POSITION);
    }

    public void updateHintUsagePreferenceState() {
        updateStateIfMatch(HINT_USAGE_PREFERENCE);
    }

    public void updateDeviceLockPreferenceState() {
        updateStateIfMatch(DEVICE_LOCK_PREFERENCE);
    }

    public void updateActivityState() {
        updateStateIfMatch(ACTIVITY);
    }

    public void updateThemeDislikedFactorsState() {
        updateStateIfMatch(THEME_DISLIKED_FACTORS);
    }

    public void updateColorState() {
        updateStateIfMatch(COLOR);
    }

    public void updateStateToComplete() {
        this.state = COMPLETE;
    }

    private void updateStateIfMatch(ProfileState currentState) {
        if (this.state.equals(currentState)) {
            Arrays.stream(ProfileState.values())
                    .filter(state -> state.getOrder() == this.state.getOrder() + 1)
                    .findAny().ifPresent(state -> this.state = state);
        }
    }
}
