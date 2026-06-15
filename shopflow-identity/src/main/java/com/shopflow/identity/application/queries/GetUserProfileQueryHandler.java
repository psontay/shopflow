package com.shopflow.identity.application.queries;

import com.shopflow.identity.domain.exceptions.UserDomainException;
import com.shopflow.identity.domain.exceptions.UserErrorCode;
import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetUserProfileQueryHandler {

    private final UserRepository userRepository;

    public GetUserProfileQueryHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse handle(UUID userId) {
        User user =
                userRepository.findById(userId)
                              .orElseThrow(() -> new UserDomainException(UserErrorCode.USER_NOT_FOUND));
        return new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(),
                                       user.getUserStatus());
    }

}
