package com.dbizz.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbizz.model.User;
import com.dbizz.repo.UserRepo;
import com.dbizz.util.PasswordUtil;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepo userRepoMock;

    private @Captor ArgumentCaptor<User> userCaptor;

    private UserService userService;

    private static final int validId = 1;
    private static final int invalidId = -1;
    private static final String validUsername = "validUser";
    private static final String invalidUsername = "ab";
    private static final String validEmail = "email@example.com";
    private static final String invalidEmail = "invalidEmail";
    private static final String validPhoneNo = "+8618340322352";
    private static final String invalidPhoneNo = "invalidPhone";
    private static final String validPassword = "ValidPass123!";
    private static final String invalidPassword = "pass";
    private static User user;
    private static User expectedUserToSave;

    @BeforeAll
    static void init() {
        user = new User(validId, validUsername, validEmail, validPhoneNo, validPassword, null);
        expectedUserToSave = new User(0, validUsername, validEmail, validPhoneNo,
                validPassword, null);
    }

    @BeforeEach
    void setup() {
        userService = new UserService(userRepoMock);
    }

    @Test
    void registerUser_whenUserIsNull_throwsException() {

        assertThrows(Exception.class, () -> {
            userService.register(null);
        });

        verifyNoInteractions(userRepoMock);
    }

    @Test
    void registerUser_whenUsernameIsNull_throwsException() {

        assertThrows(Exception.class, () -> {
            userService.register(new User(0, null, "email@example.com", "123456789", "password", null));
        });

        verifyNoInteractions(userRepoMock);
    }

    @Test
    void registerUser_whenUsernameDoesNotMeetCriteria_throwsException() {

        assertThrows(Exception.class, () -> {
            userService.register(new User(0, "ab", "email@example.com", "123456789", "password", null));
        });

        verifyNoInteractions(userRepoMock);
    }

    @Test
    void registerUser_whenPasswordIsNull_throwsException() {

        assertThrows(Exception.class, () -> {
            userService.register(new User(0, "validUser", "email@example.com", null, "password", null));
        });
        verifyNoInteractions(userRepoMock);
    }

    @Test
    void registerUser_whenPasswordDoesNotMeetCriteria_throwsException() {

        assertThrows(Exception.class, () -> {
            userService.register(new User(0, "validUser", "email@example.com", "123456789", "pass", null));
        });
        verifyNoInteractions(userRepoMock);
    }

    @Test
    void registerUser_whenEmailIsNull_throwsException() {

        assertThrows(Exception.class, () -> {
            userService.register(new User(0, "validUser", null, "123456789", "password", null));
        });
        verifyNoInteractions(userRepoMock);
    }

    @Test
    void registerUser_whenEmailIsInvalid_throwsException() {
        assertThrows(Exception.class, () -> {
            userService.register(new User(0, "validUser", "invalidEmail", "123456789", "password", null));
        });
        verifyNoInteractions(userRepoMock);
    }

    @Test
    void registerUser_whenPhoneNoIsNull_throwsException() {
        assertThrows(Exception.class, () -> {
            userService.register(new User(0, "validUser", "email@example.com", null, "password", null));
        });
        verifyNoInteractions(userRepoMock);
    }

    @Test
    void registerUser_whenPhoneNoIsInvalid_throwsException() {
        assertThrows(Exception.class, () -> {
            userService.register(new User(0, "validUser", "email@example.com", "invalidPhone", "password", null));
        });
        verifyNoInteractions(userRepoMock);
    }

    @Test
    void registerUser_whenRepoError_throwsException() throws Exception {
        when(userRepoMock.create(any())).thenThrow(new Exception());

        assertThrows(Exception.class, () -> userService.register(user));

        verify(userRepoMock, only()).create(any());
    }

    @Test
    void registerUser_whenUserIsNotCreated_returnInvalidUserId() throws Exception {
        when(userRepoMock.create(any())).thenReturn(0);

        int createdId = assertDoesNotThrow(() -> userService.register(user));
        assertEquals(0, createdId);

        verify(userRepoMock, only()).create(argThat(actualUser -> {
            return actualUser.username().equals(expectedUserToSave.username()) &&
                    actualUser.email().equals(expectedUserToSave.email()) &&
                    actualUser.phoneNo().equals(expectedUserToSave.phoneNo()) &&
                    PasswordUtil.equals(expectedUserToSave.password(), actualUser.password());
        }));
    }

    @Test
    void registerUser_whenValidDetails_returnsSuccessfully() throws Exception {
        when(userRepoMock.create(any())).thenReturn(validId);

        int createdId = assertDoesNotThrow(() -> userService.register(user));
        assertThat(createdId).isGreaterThan(0);

        verify(userRepoMock, only()).create(argThat(actualUser -> {
            return actualUser.username().equals(expectedUserToSave.username()) &&
                    actualUser.email().equals(expectedUserToSave.email()) &&
                    actualUser.phoneNo().equals(expectedUserToSave.phoneNo()) &&
                    PasswordUtil.equals(expectedUserToSave.password(), actualUser.password());
        }));
    }

    @Test
    void registerUser_passwordIsHashedBeforeSaving() throws Exception {
        when(userRepoMock.create(userCaptor.capture())).thenReturn(validId);

        int createdId = assertDoesNotThrow(() -> userService.register(user));
        assertThat(createdId).isGreaterThan(0);
        User capturedUser = userCaptor.getValue();
        assertTrue(PasswordUtil.equals(user.password(), capturedUser.password()));

        verify(userRepoMock, only()).create(argThat(actualUser -> {
            return actualUser.username().equals(expectedUserToSave.username()) &&
                    actualUser.email().equals(expectedUserToSave.email()) &&
                    actualUser.phoneNo().equals(expectedUserToSave.phoneNo()) &&
                    PasswordUtil.equals(expectedUserToSave.password(), actualUser.password());
        }));
    }

    @Test
    void authenticateUser_whenUserIsNull_throwsException() {
        assertThrows(Exception.class, () -> {
            userService.login(null);
        });
        verifyNoInteractions(userRepoMock);
    }

    @Test
    void authenticateUser_whenPasswordIsNull_throwsException() {
        assertThrows(Exception.class, () -> {
            userService.login(new User(0, "validUser", validEmail, validPhoneNo, null, null));
        });
        verifyNoInteractions(userRepoMock);
    }

    @Test
    void authenticateUser_whenEmailAndPhoneNoIsNull_throwsException() {
        assertThrows(Exception.class, () -> {
            userService.login(new User(0, "validUser", null, null, validPassword, null));
        });
        verifyNoInteractions(userRepoMock);
    }

    @Test
    void authenticateUser_whenNoEmailMatches_returnsNullUser() throws Exception {
        when(userRepoMock.findByEmail(validEmail)).thenReturn(null);

        User returnedUser = assertDoesNotThrow(() -> userService.login(
                new User(0, null, validEmail, null, validPassword, null)));
        assertEquals(null, returnedUser);

        verify(userRepoMock, only()).findByEmail(validEmail);
    }

    @Test
    void authenticateUser_whenNoPhoneNoMatches_returnsNullUser() throws Exception {
        when(userRepoMock.findByPhoneNo(validPhoneNo)).thenReturn(null);

        User returnedUser = assertDoesNotThrow(() -> userService.login(
                new User(0, null, null, validPhoneNo, validPassword, null)));
        assertEquals(null, returnedUser);

        verify(userRepoMock, only()).findByPhoneNo(validPhoneNo);
    }

    @Test
    void authenticateUser_whenRepoError_throwsException() throws Exception {
        when(userRepoMock.findByPhoneNo(validPhoneNo)).thenThrow(new Exception());
        assertThrows(Exception.class, () -> {
            userService.login(new User(0, "validUser", null, validPhoneNo, validPassword, null));
        });
        verify(userRepoMock, only()).findByPhoneNo(validPhoneNo);
    }

    @Test
    void authenticateUser_whenEmailMatchesAndPasswordDoesNotMatch_returnsNullUser() throws Exception {
        when(userRepoMock.findByEmail(validEmail)).thenReturn(new User(1, "validUser", validEmail, validPhoneNo,
                PasswordUtil.hashPassword("SomeOtherPassword"), null));

        User returnedUser = assertDoesNotThrow(() -> userService.login(
                new User(0, null, validEmail, null, invalidPassword, null)));
        assertEquals(null, returnedUser);

        verify(userRepoMock, only()).findByEmail(validEmail);
    }

    @Test
    void authenticateUser_whenPhoneNoMatchesPasswordDoesNotMatch_returnsNullUser() throws Exception {
        when(userRepoMock.findByPhoneNo(validPhoneNo)).thenReturn(new User(1, "validUser", validEmail, validPhoneNo,
                PasswordUtil.hashPassword("SomeOtherPassword"), null));

        User returnedUser = assertDoesNotThrow(() -> userService.login(
                new User(0, null, null, validPhoneNo, invalidPassword, null)));
        assertEquals(null, returnedUser);

        verify(userRepoMock, only()).findByPhoneNo(validPhoneNo);
    }

    @Test
    void authenticateUser_whenEmailAndPasswordMatch_returnsMatchedUser() throws Exception {
        User expectedUser = new User(1, "validUser", validEmail, validPhoneNo,
                PasswordUtil.hashPassword(validPassword), null);
        when(userRepoMock.findByEmail(validEmail)).thenReturn(expectedUser);

        User returnedUser = assertDoesNotThrow(() -> userService.login(
                new User(0, null, validEmail, null, validPassword, null)));
        assertEquals(expectedUser, returnedUser);

        verify(userRepoMock, only()).findByEmail(validEmail);
    }

    @Test
    void authenticateUser_whenPhoneNoAndPasswordMatch_returnsMatchedUser() throws Exception {
        User expectedUser = new User(1, "validUser", validEmail, validPhoneNo,
                PasswordUtil.hashPassword(validPassword), null);
        when(userRepoMock.findByPhoneNo(validPhoneNo)).thenReturn(expectedUser);

        User returnedUser = assertDoesNotThrow(() -> userService.login(
                new User(0, null, null, validPhoneNo, validPassword, null)));
        assertEquals(expectedUser, returnedUser);

        verify(userRepoMock, only()).findByPhoneNo(validPhoneNo);
    }
}
