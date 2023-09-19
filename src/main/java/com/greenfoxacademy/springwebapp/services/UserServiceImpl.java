package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.configurations.GameLogicConfiguration;
import com.greenfoxacademy.springwebapp.dtos.RegisteredUserDTO;
import com.greenfoxacademy.springwebapp.dtos.UserDTO;
import com.greenfoxacademy.springwebapp.entities.*;
import com.greenfoxacademy.springwebapp.enums.BuildingTypes;
import com.greenfoxacademy.springwebapp.enums.ResourceType;
import com.greenfoxacademy.springwebapp.exception.UserAlreadyVerifiedException;
import com.greenfoxacademy.springwebapp.exception.UserNotFoundException;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import com.greenfoxacademy.springwebapp.security.TribesUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    private MapperService mapperService;

    private BCryptPasswordEncoder encoder;

    private TribesUserDetailsService tribesUserDetailsService;

    private GameLogicConfiguration configuration;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MapperService mapperService, BCryptPasswordEncoder encoder, TribesUserDetailsService tribesUserDetailsService, GameLogicConfiguration configuration) {
        this.userRepository = userRepository;
        this.mapperService = mapperService;
        this.encoder = encoder;
        this.tribesUserDetailsService = tribesUserDetailsService;
        this.configuration = configuration;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public RegisteredUserDTO addNewUser(UserDTO userDTO) {
        User user = mapperService.convertUserDTOtoUser(userDTO);

        user.setKingdom(new Kingdom(userDTO.getKingdomname(), user));

        Kingdom kingdom = user.getKingdom();

        new Resource(ResourceType.GOLD, configuration.getGoldResourceStarter(), 0, kingdom);
        new Resource(ResourceType.FOOD, configuration.getFoodResourceStarter(), 0, kingdom);

        Building townHall = new Building(kingdom, BuildingTypes.TOWNHALL, configuration.getTownhallHp());
        townHall.setFinishedAt(townHall.getStartedAt());
        Building farm = new Building(kingdom, BuildingTypes.FARM, configuration.getFarmHp());
        farm.setFinishedAt(farm.getStartedAt());
        farm.setPosition(2);
        Building mine = new Building(kingdom, BuildingTypes.MINE, configuration.getMineHp());
        mine.setFinishedAt(mine.getStartedAt());
        mine.setPosition(1);
        Building academy = new Building(kingdom, BuildingTypes.ACADEMY, configuration.getAcademyHp());
        academy.setFinishedAt(academy.getStartedAt());
        academy.setPosition(6);

        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);

        return mapperService.convertUserToRegisteredUserDTO(user);
    }

    public boolean isUserRegistered(String desiredUsername) {
        return !userRepository.findUserByUsername(desiredUsername).isEmpty();
    }

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public void saveVerificationTokenForUser(UserDTO userDTO, String token) {
        User user = findUserByUsername(userDTO.getUsername()).get();
        user.setVerificationToken(token);
        userRepository.save(user);
    }

    public void verifyUser(String verificationToken) {
        User user = tribesUserDetailsService.getUserByEmailVerificationToken(verificationToken);
        if (user.isVerified()) {
            throw new UserAlreadyVerifiedException();
        } else {
            user.setVerified(true);
            userRepository.save(user);
        }
    }
}