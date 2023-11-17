package com.habbatul.challange4.service;

import com.habbatul.challange4.entity.User;
import com.habbatul.challange4.entity.security.Roles;
import com.habbatul.challange4.enums.ERole;
import com.habbatul.challange4.model.requests.authreq.LoginRequest;
import com.habbatul.challange4.model.requests.authreq.SignupRequest;
import com.habbatul.challange4.model.responses.JwtResponse;
import com.habbatul.challange4.model.responses.WebResponse;
import com.habbatul.challange4.model.security.UserDetailsImpl;
import com.habbatul.challange4.repository.RoleRepository;
import com.habbatul.challange4.repository.UserRepository;
import com.habbatul.challange4.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtils;

    @Value("${jwt.expiration.ms}")
    private int jwtExpirationMs;

    @Transactional(readOnly = true)
    @Override
    public JwtResponse authenticateUser(LoginRequest login, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        //set cookies ke browser
        Cookie cookie = new Cookie("token", jwt);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) TimeUnit.MILLISECONDS.toSeconds(jwtExpirationMs));
        //atur domain yang diinginkan
        cookie.setDomain("localhost");
        cookie.setPath("/");
        response.addCookie(cookie);

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
                userDetails.getEmail(), roles);
    }

    @Transactional
    @Override
    public WebResponse<String> registerUser(SignupRequest signupRequest) {
        Boolean usernameExist = userRepository.existsByUsername(signupRequest.getUsername());
        if(Boolean.TRUE.equals(usernameExist)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username telah ada");
        }

        Boolean emailExist = userRepository.existsByEmailAddress(signupRequest.getEmail());
        if(Boolean.TRUE.equals(emailExist)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email telah ada");
        }

        User user = User.builder().username(signupRequest.getUsername()).
                emailAddress(signupRequest.getEmail()).
                password(passwordEncoder.encode(signupRequest.getPassword())).
                build();

        Set<String> strRoles = signupRequest.getRole();
        Set<Roles> roles = new HashSet<>();

        try {
            if (strRoles == null) {
                Roles role = roleRepository.findByRoleName(ERole.CUSTOMER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                roles.add(role);
            } else {
                strRoles.forEach(role -> {
                    Roles roles1 = roleRepository.findByRoleName(ERole.valueOf(role))
                            .orElseThrow(() -> new RuntimeException("Error: Role " + role + " is not found"));
                    roles.add(roles1);
                });
            }
            user.setRoles(roles);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ada kesalahan pada Role");
        }

        userRepository.save(user);

        return WebResponse.<String>builder()
                .data("User registered successfully")
                .build();
    }
}

