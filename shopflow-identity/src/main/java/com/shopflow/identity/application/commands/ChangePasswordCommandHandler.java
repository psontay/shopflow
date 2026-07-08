package com.shopflow.identity.application.commands;

import com.shopflow.identity.application.outbox.OutboxRepository;
import com.shopflow.identity.domain.exceptions.UserDomainException;
import com.shopflow.identity.domain.exceptions.UserErrorCode;
import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangePasswordCommandHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OutboxRepository outboxRepository;

    public ChangePasswordCommandHandler(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                        OutboxRepository outboxRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public void handle(ChangePasswordCommand command) {
        User user = userRepository.findById(command.userId())
                                  .orElseThrow(() -> new UserDomainException(UserErrorCode.INVALID_CREDENTIALS));
        if (! passwordEncoder.matches(command.oldPassword(), user.getHashedPassword())) {
            throw new UserDomainException(UserErrorCode.OLD_PASSWORD_NOT_MATCH);
        }

        if (passwordEncoder.matches(command.newPassword(), user.getHashedPassword())) {
            throw new UserDomainException(UserErrorCode.NEW_PASSWORD_SAME_AS_OLD);
        }

        String newHashedPassword = passwordEncoder.encode(command.newPassword());
        user.changePassword(newHashedPassword);

        userRepository.save(user);

        outboxRepository.saveEvents(user.getDomainEvents());
    }

}
