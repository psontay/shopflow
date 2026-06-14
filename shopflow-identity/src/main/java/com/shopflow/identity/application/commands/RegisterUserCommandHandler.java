package com.shopflow.identity.application.commands;

import com.shopflow.identity.domain.exceptions.UserDomainException;
import com.shopflow.identity.domain.exceptions.UserErrorCode;
import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.domain.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegisterUserCommandHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserCommandHandler(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UUID handle(RegisterUserCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            throw new UserDomainException(UserErrorCode.USER_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new UserDomainException(UserErrorCode.EMAIL_ALREADY_IN_USE);
        }
        String hashedPassword = passwordEncoder.encode(command.rawPassword());
        UUID newUserId = UUID.randomUUID();
        User newUser = new User(newUserId, command.username(), command.email(), hashedPassword);
        userRepository.save(newUser);
        return newUser.getId();
    }

}
