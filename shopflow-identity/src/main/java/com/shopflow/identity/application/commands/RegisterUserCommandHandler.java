package com.shopflow.identity.application.commands;

import com.shopflow.identity.application.outbox.OutboxService;
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
    private final OutboxService outboxService;

    public RegisterUserCommandHandler(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                      OutboxService outboxService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.outboxService = outboxService;
    }

    @Transactional
    public UUID handle(RegisterUserCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            throw new UserDomainException(UserErrorCode.USER_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new UserDomainException(UserErrorCode.EMAIL_ALREADY_IN_USE);
        }
        if (! command.rawPassword()
                     .equals(command.confirmPassword())) {
            throw new UserDomainException(UserErrorCode.CONFIRM_PASSWORD_NOT_MATCH);
        }
        String hashedPassword = passwordEncoder.encode(command.rawPassword());
        UUID newUserId = UUID.randomUUID();
        User newUser = new User(newUserId, command.username(), command.email(), hashedPassword);
        userRepository.save(newUser);
        outboxService.saveEvents(newUser.getDomainEvents());
        return newUser.getId();
    }

}
