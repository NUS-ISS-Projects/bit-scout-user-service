package com.bitScout.userService.controller;

import com.bitScout.userService.dto.LoginRequest;
import com.bitScout.userService.dto.UpdateEmailPasswordRequest;
import com.bitScout.userService.dto.UpdateUserRequest;
import com.bitScout.userService.model.User;
import com.bitScout.userService.service.AuthService;
import com.bitScout.userService.service.FirestoreService;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserControllerTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private AuthService authService;

	@Mock
	private FirestoreService firestoreService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testCreateUser_Success() throws FirebaseAuthException, ExecutionException, InterruptedException {
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");
		when(authService.createUser(any(User.class))).thenReturn(user);

		ResponseEntity<?> response = userController.createUser(user);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(user, response.getBody());
	}

	@Test
	public void testCreateUser_EmailAlreadyExists()
			throws FirebaseAuthException, ExecutionException, InterruptedException {
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");

		FirebaseAuthException exception = mock(FirebaseAuthException.class);
		when(exception.getAuthErrorCode()).thenReturn(AuthErrorCode.EMAIL_ALREADY_EXISTS);
		when(exception.getMessage()).thenReturn("Email already exists");

		when(authService.createUser(any(User.class))).thenThrow(exception);

		ResponseEntity<?> response = userController.createUser(user);

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertEquals("Email already exists.", response.getBody());
	}

	@Test
	public void testLogin_Success() throws Exception {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("test@example.com");
		loginRequest.setPassword("password");
		when(authService.loginWithEmailPassword(anyString(), anyString())).thenReturn("token");

		ResponseEntity<?> response = userController.login(loginRequest);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("token", response.getBody());
	}

	@Test
	public void testLogin_InvalidCredentials() throws Exception {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("test@example.com");
		loginRequest.setPassword("password");
		when(authService.loginWithEmailPassword(anyString(), anyString()))
				.thenThrow(new Exception("INVALID_LOGIN_CREDENTIALS"));

		ResponseEntity<?> response = userController.login(loginRequest);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertEquals("Login failed: Invalid email or password.", response.getBody());
	}

	@Test
	public void testUpdateUserDetails_Success() throws ExecutionException, InterruptedException {
		UpdateUserRequest updateUserRequest = new UpdateUserRequest();
		updateUserRequest.setName("New Name");
		updateUserRequest.setAvatar("avatar-url");
		updateUserRequest.setIntroduction("New Introduction");

		doNothing().when(firestoreService).updateUser(anyString(), anyString(), anyString(), anyString());

		ResponseEntity<?> response = userController.updateUserDetails("userId", updateUserRequest);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("User details updated successfully.", response.getBody());
	}

	@Test
	public void testUpdateUserEmailPassword_Success()
			throws FirebaseAuthException, ExecutionException, InterruptedException {
		UpdateEmailPasswordRequest updateEmailPasswordRequest = new UpdateEmailPasswordRequest();
		updateEmailPasswordRequest.setOldPassword("oldPassword");
		updateEmailPasswordRequest.setNewPassword("newPassword");
		updateEmailPasswordRequest.setNewEmail("new@example.com");

		when(authService.authenticateUser(anyString(), anyString())).thenReturn(true);
		doNothing().when(authService).updateUserEmailPassword(anyString(), anyString(), anyString());
		doNothing().when(firestoreService).updateUserEmailPassword(anyString(), anyString(), anyString());

		ResponseEntity<?> response = userController.updateUserEmailPassword("userId", updateEmailPasswordRequest);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("User details updated successfully.", response.getBody());
	}
}
