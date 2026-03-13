package com.ey.EY.service;

import com.ey.EY.dto.PhoneResponse;
import com.ey.EY.dto.UserRequest;
import com.ey.EY.dto.UserResponse;
import com.ey.EY.exception.EmailAlreadyExistsException;
import com.ey.EY.exception.TokenGenerationException;
import com.ey.EY.model.Phone;
import com.ey.EY.model.User;
import com.ey.EY.repository.PhoneRepository;
import com.ey.EY.repository.UserRepository;
import com.ey.EY.config.security.JwtService;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PhoneRepository phoneRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, PhoneRepository phoneRepository,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.phoneRepository = phoneRepository;
        this.jwtService = jwtService;
    }

    public UserResponse register(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        LocalDateTime now = LocalDateTime.now();

        String token;
        try {
            token = jwtService.generateToken(request.email());
        } catch (JOSEException e) {
            throw new TokenGenerationException();
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now);
        user.setToken(token);
        user.setIsactive(true);

        User saved = userRepository.save(user);

        List<Phone> phones = List.of();
        if (request.phones() != null && !request.phones().isEmpty()) {
            phones = request.phones().stream()
                    .map(p -> new Phone(p.number(), p.citycode(), p.contrycode(), saved))
                    .toList();
            phoneRepository.saveAll(phones);
        }

        List<PhoneResponse> phoneResponses = phones.stream()
                .map(p -> new PhoneResponse(p.getNumber(), p.getCitycode(), p.getContrycode()))
                .toList();

        return new UserResponse(saved.getId(), saved.getName(), saved.getEmail(),
                phoneResponses, saved.getCreated(), saved.getModified(), saved.getLastLogin(),
                saved.getToken(), saved.isIsactive());
    }
}
