package com.netflixClone.service;

import org.jspecify.annotations.Nullable;

import com.netflixClone.dto.request.UserRequest;
import com.netflixClone.dto.response.EmailValidationResponse;
import com.netflixClone.dto.response.LoginResponse;
import com.netflixClone.dto.response.MessageResponse;

import jakarta.validation.Valid;

public interface AuthService {

	MessageResponse signup(@Valid UserRequest userRequest);

	LoginResponse login(String email, String password);

	EmailValidationResponse validateEmail(String email);

	MessageResponse verifyEmail(String token);

	MessageResponse resendVerificationEmail(String email);

	MessageResponse forgotPassword(String email);

	MessageResponse resetPassword(String token, String newPassword);

	MessageResponse changePassword(String email, String currentPassword, String newPassword);

	LoginResponse currentUser(String email);
	
	
	

}
