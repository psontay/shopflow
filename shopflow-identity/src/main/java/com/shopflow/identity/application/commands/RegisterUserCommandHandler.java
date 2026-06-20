package com.shopflow.identity.application.commands;

import com.shopflow.identity.application.outbox.OutboxService;
import com.shopflow.identity.domain.exceptions.UserDomainException;
import com.shopflow.identity.domain.exceptions.UserErrorCode;
import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
public class RegisterUserCommandHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OutboxService outboxService;

    private final RedisTemplate<Object, Object> redisTemplate;

    public RegisterUserCommandHandler(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                      OutboxService outboxService, RedisTemplate<Object, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.outboxService = outboxService;
        this.redisTemplate = redisTemplate;
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
        SecureRandom secureRandom = new SecureRandom();
        int verificationOtpInt = secureRandom.nextInt(1000000);
        String verificationOtp = String.valueOf(verificationOtpInt);
        String email = command.email();
        String redisKey = "OTP:" + email;
        redisTemplate.opsForValue()
                     .set(redisKey, verificationOtp, java.time.Duration.ofMinutes(5));
        String hashedPassword = passwordEncoder.encode(command.rawPassword());
        UUID newUserId = UUID.randomUUID();

        User newUser = new User(newUserId, command.username(), command.email(), hashedPassword);
        userRepository.save(newUser);
        outboxService.saveEvents(newUser.getDomainEvents());
        return newUser.getId();
    }

}
