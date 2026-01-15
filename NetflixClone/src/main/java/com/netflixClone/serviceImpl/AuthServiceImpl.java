package com.netflixClone.serviceImpl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.netflixClone.config.SecurityConfig;
import com.netflixClone.dao.UserRepository;
import com.netflixClone.dto.request.UserRequest;
import com.netflixClone.dto.response.EmailValidationResponse;
import com.netflixClone.dto.response.LoginResponse;
import com.netflixClone.dto.response.MessageResponse;
import com.netflixClone.entity.User;
import com.netflixClone.enums.Role;
import com.netflixClone.exception.BadCredentialsException;
import com.netflixClone.exception.AccountDeactivatedException;
import com.netflixClone.exception.EmailAlreadyExistsException;
import com.netflixClone.exception.EmailNotVerifiedException;
import com.netflixClone.exception.InvalidCredentialsException;
import com.netflixClone.exception.InvalidTokenException;
import com.netflixClone.security.JwtUtil;
import com.netflixClone.service.AuthService;
import com.netflixClone.service.EmailService;
import com.netflixClone.util.ServiceUtils;

import jakarta.validation.Valid;

@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private JwtUtil jwtUtils;
	
	@Autowired
	private ServiceUtils serviceUtils;

	@Override
	public MessageResponse signup(@Valid UserRequest userRequest) {
		if(userRepository.existsByEmail(userRequest.getEmail())) {
			throw new EmailAlreadyExistsException("Email is already exists. Please use a different email.");
		}
		
		User user = new User();
		user.setEmail(userRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		user.setFullName(userRequest.getFullName());
		user.setRole(Role.USER);
		user.setActive(true);
		user.setEmailVerified(false);
		String verificationToken = UUID.randomUUID().toString();
		user.setVerificationToken(verificationToken);
		user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));
		userRepository.save(user);
		emailService.sendVerificationEmail(userRequest.getEmail(), verificationToken);
		
		return new MessageResponse("User registered successfully. Please check your email to verify your account.");
	}

	@Override
	public LoginResponse login(String email, String password) {
		User user = userRepository
				.findByEmail(email)
				.filter(u -> passwordEncoder.matches(password, u.getPassword()))
				.orElseThrow(() -> new BadCredentialsException("Invalid email or password."));
		
		if(!user.isActive()) {
			throw new AccountDeactivatedException("Your account is deactivated. Please contact support.");
		}
		
		if(!user.isEmailVerified()) {
			throw new EmailNotVerifiedException("Email is not verified. Please verify your email before logging in.");
		}
		
		final String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
		
		return new LoginResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
		
	}

	@Override
	public EmailValidationResponse validateEmail(String email) {
		boolean exists = userRepository.existsByEmail(email);
		return new EmailValidationResponse(exists, !exists);
	}

	@Override
	public MessageResponse verifyEmail(String token) {
		User user = userRepository
				.findByVerificationToken(token)
				.orElseThrow(() -> new InvalidTokenException("Invalid or expired verification token."));
		
		if(user.getVerificationTokenExpiry() == null || user.getVerificationTokenExpiry().isBefore(Instant.now())) {
			throw new InvalidTokenException("Verification link has expired. Please request a new one.");
		}
		
		user.setEmailVerified(true);
		user.setVerificationToken(null);
		user.setVerificationTokenExpiry(null);
		userRepository.save(user);
		
		return new MessageResponse("Email verified successfully. You can now login to your account.");
		
	}

	@Override
	public MessageResponse resendVerificationEmail(String email) {
		User user = serviceUtils.getUserByEmailOrThrow(email);
		
		String verificationToken = UUID.randomUUID().toString();
		user.setVerificationToken(verificationToken);
		user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));
		userRepository.save(user);
		emailService.sendVerificationEmail(email, verificationToken);
		
		return new MessageResponse("Verification email resent successfully. Please check your inbox.");
	}

	@Override
	public MessageResponse forgotPassword(String email) {
		User user = serviceUtils.getUserByEmailOrThrow(email);
		
		String resetToken = UUID.randomUUID().toString();
		user.setPasswordResetToken(resetToken);
		user.setPasswordResetTokenExpiry(Instant.now().plusSeconds(3600));
		userRepository.save(user);
		emailService.sendPasswordResetEmail(email, resetToken);
		
		return new MessageResponse("Password reset email sent successfully. Please check your inbox.");
	}

	@Override
	public MessageResponse resetPassword(String token, String newPassword) {
		User user = userRepository
				.findByPasswordResetToken(token)
				.orElseThrow(() -> new InvalidTokenException("Invalid or expired reset token."));
		
		if(user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(Instant.now())) {
			throw new InvalidTokenException("Password reset token has expired. Please request a new one.");
		}
		
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setPasswordResetToken(null);
		user.setPasswordResetTokenExpiry(null);
		userRepository.save(user);
		
		return new MessageResponse("Password has been reset successfully. You can now login with your new password.");
	}

	@Override
	public MessageResponse changePassword(String email, String currentPassword, String newPassword) {
		User user = serviceUtils.getUserByEmailOrThrow(email);
		
		if(!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new InvalidCredentialsException("Current password is incorrect.");
		}
		
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
		
		return new MessageResponse("Password changed successfully.");
	}

	@Override
	public LoginResponse currentUser(String email) {
		User user = serviceUtils.getUserByEmailOrThrow(email);
		
		return new LoginResponse(null, user.getEmail(), user.getFullName(), user.getRole().name());
		
	}

}
