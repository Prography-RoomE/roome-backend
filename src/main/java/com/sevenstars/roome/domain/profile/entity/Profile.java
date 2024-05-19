package com.sevenstars.roome.domain.profile.entity;

import com.sevenstars.roome.domain.common.entity.BaseTimeEntity;
import com.sevenstars.roome.domain.profile.entity.activity.Activity;
import com.sevenstars.roome.domain.profile.entity.color.Color;
import com.sevenstars.roome.domain.profile.entity.device.DeviceLockPreference;
import com.sevenstars.roome.domain.profile.entity.hint.HintUsagePreference;
import com.sevenstars.roome.domain.profile.entity.position.HorrorThemePosition;
import com.sevenstars.roome.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.sevenstars.roome.domain.profile.entity.Mbti.NONE;
import static com.sevenstars.roome.domain.profile.entity.ProfileState.ROOM_COUNT;
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

    public void updateHorrorThemePosition(HorrorThemePosition position) {
        this.horrorThemePosition = position;
    }

    public void updateHintUsagePreference(HintUsagePreference preference) {
        this.hintUsagePreference = preference;
    }

    public void updateDeviceLockPreference(DeviceLockPreference preference) {
        this.deviceLockPreference = preference;
    }

    public void updateActivity(Activity activity) {
        this.activity = activity;
    }

    public void updateColor(Color color) {
        this.color = color;
    }
}
