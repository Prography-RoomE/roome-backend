package com.sevenstars.roome.domain.user.service;

import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.repository.UserRepository;
import com.sevenstars.roome.domain.user.request.UserRequest;
import com.sevenstars.roome.domain.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.sevenstars.roome.global.common.response.ExceptionMessage.INVALID_ID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User saveOrUpdate(UserRequest request) {
        String serviceId = request.getServiceId();
        String serviceUserId = request.getServiceUserId();
        String email = request.getEmail();

        Optional<User> optionalUser = userRepository.findByServiceIdAndServiceUserIdAndWithdrawalFalse(serviceId, serviceUserId);

        User user;
        if (optionalUser.isEmpty()) {
            user = new User(serviceId, serviceUserId, email);
            userRepository.save(user);
            return user;
        } else {
            user = optionalUser.get();
            user.update(email);
        }

        return user;
    }

    @Transactional
    public void withdraw(Long id) {
        User user = userRepository.findByIdAndWithdrawalFalse(id)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_ID.getMessage()));

        user.withdraw();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        User user = userRepository.findByIdAndWithdrawalFalse(id)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_ID.getMessage()));

        return new UserResponse(user.getEmail(), user.getNickname());
    }
}
