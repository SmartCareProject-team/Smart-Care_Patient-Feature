package com.smartcare.backend.auth;

import com.smartcare.backend.config.ConflictException;
import com.smartcare.backend.config.UnauthorizedException;
import com.smartcare.backend.patient.Patient;
import com.smartcare.backend.patient.PatientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PatientRepository patientRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ConflictException("Email already used");
        }
        if (patientRepository.existsByCin(req.cin())) {
            throw new ConflictException("CIN already used");
        }

        User user = new User();
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRole(Role.PATIENT);
        user = userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setFirstName(req.firstName());
        patient.setLastName(req.lastName());
        patient.setCin(req.cin());
        patient.setBirthDate(req.birthDate());
        patient.setPhone(req.phone());
        patient = patientRepository.save(patient);

        return new LoginResponse(jwtService.generateToken(user), user.getRole(), user.getId(), patient.getId());
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        Long patientId = patientRepository.findByUserId(user.getId())
                .map(Patient::getId)
                .orElse(null);

        return new LoginResponse(jwtService.generateToken(user), user.getRole(), user.getId(), patientId);
    }
}