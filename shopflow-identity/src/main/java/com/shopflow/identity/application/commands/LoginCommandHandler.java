package com.shopflow.identity.application.commands;

import com.shopflow.identity.domain.exceptions.UserDomainException;
import com.shopflow.identity.domain.exceptions.UserErrorCode;
import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.domain.models.UserStatus;
import com.shopflow.identity.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public class LoginCommandHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginCommandHandler(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UUID handle(LoginCommand command) {
        if (! userRepository.existsByUsername(command.username())) {
            throw new UserDomainException(UserErrorCode.USER_NOT_FOUND);
        }
        User user =
                userRepository.findByUsername(command.username())
                              .orElseThrow(() -> new UserDomainException(UserErrorCode.USER_NOT_FOUND));
        if (user.getUserStatus() == UserStatus.INACTIVE) {
            throw new UserDomainException(UserErrorCode.INVALID_USER_STATE);
        }
        if (! passwordEncoder.matches(command.password(), user.getHashedPassword())) {
            throw new UserDomainException(UserErrorCode.PASSWORD_NOT_MATCH);
        }
        return UUID.randomUUID();
    }

}
