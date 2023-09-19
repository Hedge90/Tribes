package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.configurations.GameLogicConfiguration;
import com.greenfoxacademy.springwebapp.dtos.RegisteredUserDTO;
import com.greenfoxacademy.springwebapp.dtos.UserDTO;
import com.greenfoxacademy.springwebapp.entities.User;
import com.greenfoxacademy.springwebapp.exception.UserNotFoundException;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import com.greenfoxacademy.springwebapp.security.TribesUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceImplTest {

    UserRepository userRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder;

    TribesUserDetailsService tribesUserDetailsService;

    MapperService mapperService;

    UserServiceImpl userServiceImpl;

    GameLogicConfiguration configuration;

    BeanFactory beanFactory;

    @Autowired
    UserServiceImplTest(BeanFactory beanFactory) {
        userRepository = Mockito.mock(UserRepository.class);
        bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
        mapperService = Mockito.mock(MapperService.class);
        tribesUserDetailsService = Mockito.mock(TribesUserDetailsService.class);
        configuration = Mockito.mock(GameLogicConfiguration.class);
        userServiceImpl = new UserServiceImpl(userRepository, mapperService, bCryptPasswordEncoder, tribesUserDetailsService, configuration);
        this.beanFactory = beanFactory;
    }

    @Test
    void addNewUser_ReturnsUser() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        RegisteredUserDTO registeredUserDTO = new RegisteredUserDTO(fakeUser.getUsername(), fakeUser.getEmail());
        Mockito.when(mapperService.convertUserDTOtoUser(fakeUserDTO)).thenReturn(fakeUser);
        Mockito.when(userRepository.save(fakeUser)).thenReturn(fakeUser);
        Mockito.when(userServiceImpl.addNewUser(fakeUserDTO)).thenReturn(registeredUserDTO);

        RegisteredUserDTO returnOfAddNewUser = userServiceImpl.addNewUser(fakeUserDTO);

        Assertions.assertEquals(fakeUser.getUsername(), returnOfAddNewUser.getUsername());
        Assertions.assertEquals(fakeUser.getEmail(), returnOfAddNewUser.getEmail());
    }

    // [MethodName_StateUnderTest_ExpectedBehavior]
    @Test
    void isUserRegistered_WhenUserExists_ReturnsTrue() {
        // Arrange
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        Mockito.when(userRepository.findUserByUsername(fakeUser.getUsername())).thenReturn(Optional.of(fakeUser));

        // Act
        boolean doesUserExist = userServiceImpl.isUserRegistered(fakeUser.getUsername());

        // Assert
        Assertions.assertTrue(doesUserExist);
    }

    @Test
    void isUserRegistered_WhenUserDoesNotExist_ReturnsFalse() {
        String userName = "name";
        Mockito.when(userRepository.findUserByUsername(userName)).thenReturn(Optional.empty());

        boolean doesUserExist = userServiceImpl.isUserRegistered(userName);

        Assertions.assertFalse(doesUserExist);
    }

    @Test
    void isEmptyOrNull_WithBlankData_ReturnsTrue() {
        String empty = "";

        Assertions.assertTrue(GeneralUtility.isEmptyOrNull(empty));
    }

    @Test
    void isEmptyOrNull_WithNull_ReturnsTrue() {
        Assertions.assertTrue(GeneralUtility.isEmptyOrNull(null));
    }

    @Test
    void isEmptyOrNull_WithValidData_ReturnsFalse() {
        String empty = "alma";

        Assertions.assertFalse(GeneralUtility.isEmptyOrNull(empty));
    }

    @Test
    void findUserById_WithNotExistingUserById_ThrowsUserNotFoundException() {
        Mockito.when(userRepository.findById(anyLong())).thenThrow(new UserNotFoundException(1L));
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userServiceImpl.getUserById(1L);
        });
    }

    @Test
    void findUserById_WithExistingUserById_ReturnsUser() {
        User fakeUser = beanFactory.getBean("fakeUserWithIds", User.class);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(fakeUser));

        User actualUser = userServiceImpl.findUserById(1L).get();

        Assertions.assertEquals(fakeUser, actualUser);
    }
}