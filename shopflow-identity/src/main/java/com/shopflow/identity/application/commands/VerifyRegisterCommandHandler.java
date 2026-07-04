package com.shopflow.identity.application.commands;

import com.shopflow.identity.domain.exceptions.IdentityDomainException;
import com.shopflow.identity.domain.exceptions.IdentityErrorCode;
import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.domain.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerifyRegisterCommandHandler {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public VerifyRegisterCommandHandler(UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void handle(VerifyRegisterCommand command) {
        String redisKey = "OTP:" + command.email();
        String cachedOTP = (String) redisTemplate.opsForValue()
                                                 .get(redisKey);
        if (cachedOTP == null) {
            throw new IdentityDomainException(IdentityErrorCode.INVALID_CREDENTIALS);
        }
        if (! cachedOTP.equals(command.otp())) {
            throw new IdentityDomainException(IdentityErrorCode.INVALID_CREDENTIALS);
        }
        User user =
                userRepository.findByEmail(command.email())
                              .orElseThrow(() -> new IdentityDomainException(IdentityErrorCode.USER_NOT_FOUND));
        user.verify();
        userRepository.save(user);
        redisTemplate.delete(redisKey);
    }

}
