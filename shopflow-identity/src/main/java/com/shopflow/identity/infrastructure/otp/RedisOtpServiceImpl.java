package com.shopflow.identity.infrastructure.otp;

import com.shopflow.identity.application.services.OtpService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class RedisOtpServiceImpl implements OtpService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    public RedisOtpServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generateAndStoreOtp(String email) {
        int otpInt = 100000 + secureRandom.nextInt(900000);
        String otp = String.valueOf(otpInt);
        String redisKey = "OTP" + email;
        redisTemplate.opsForValue()
                     .set(redisKey, otp, Duration.ofMinutes(5));
        return otp;
    }

}
