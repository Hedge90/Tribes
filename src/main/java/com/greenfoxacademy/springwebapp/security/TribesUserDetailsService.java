package com.greenfoxacademy.springwebapp.security;

import com.greenfoxacademy.springwebapp.entities.User;
import com.greenfoxacademy.springwebapp.exception.InvalidTokenException;
import com.greenfoxacademy.springwebapp.exception.UserNotFoundException;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class TribesUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    private JwtUtil jwtUtil;

    @Autowired
    public TribesUserDetailsService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findUserByUsername(username);

        user.orElseThrow(() -> new UserNotFoundException());

        return user.map(UserDetailsImpl::new).get();
    }

    public UserDetailsImpl extractUserDetailsFromRequest(HttpServletRequest request) {
        String jwt = jwtUtil.extractTokenFromHeaderAuthorization(request);
        String username = null;
        if (jwt != null) {
            username = jwtUtil.extractUsername(jwt);
        }
        UserDetailsImpl userDetails = null;
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            userDetails = loadUserByUsername(username);
            if (!jwtUtil.validateToken(jwt, userDetails)) {
                throw new InvalidTokenException(jwt);
            }
        }
        return userDetails;
    }

    public User getUserByUsernameFromRequest(HttpServletRequest request) {
        Optional<User> user = userRepository.findUserByUsername(jwtUtil.extractUsernameFromRequest(request));
        return user.orElseThrow(() -> new UserNotFoundException(user.get().getUsername()));
    }

    public User getUserByEmailVerificationToken(String token) {
        Optional<User> optionalUser = userRepository.findUserByVerificationToken(token);
        return optionalUser.get();
    }
}