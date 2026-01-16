package com.netflixClone.service;

import org.jspecify.annotations.Nullable;

import com.netflixClone.dto.request.UserRequest;
import com.netflixClone.dto.response.MessageResponse;
import com.netflixClone.dto.response.PageResponse;
import com.netflixClone.dto.response.UserResponse;

public interface UserService {

	MessageResponse createUser(UserRequest userRequest);

	MessageResponse updateUser(Long id, UserRequest userRequest);

	PageResponse<UserResponse> getAllUsers(int page, int size, String search);

	MessageResponse deleteUser(Long id, String currentUserEmail);

	MessageResponse toggleUserStatus(Long id, String currentUserEmail);

	MessageResponse changeUserRole(Long id, UserRequest userRequest);
	
	
	

}
