package com.shopflow.identity.application.queries;

import com.shopflow.identity.application.security.TokenProviderPort;
import com.shopflow.identity.domain.exceptions.UserDomainException;
import com.shopflow.identity.domain.exceptions.UserErrorCode;
import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.domain.models.UserStatus;
import com.shopflow.identity.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
public class AuthenticateUserQueryHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProviderPort tokenProviderPort;

    public AuthenticateUserQueryHandler(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                        TokenProviderPort tokenProviderPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProviderPort = tokenProviderPort;
    }

    public String handle(AuthenticateUserQuery query) {
        User user = userRepository.findByUsername(query.username())
                                  .orElseThrow(() -> new UserDomainException(
                                          UserErrorCode.USER_NOT_FOUND));

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new UserDomainException(UserErrorCode.INVALID_USER_STATE);
        }

        boolean isPasswordMatch = passwordEncoder.matches(query.rawPassword(), user.getHashedPassword());

        if (! isPasswordMatch) {
            throw new UserDomainException(UserErrorCode.INVALID_CREDENTIALS);
        }
        return tokenProviderPort.generateAccessToken(user);
    }

}
