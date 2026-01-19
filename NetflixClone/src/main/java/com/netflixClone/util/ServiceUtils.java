package com.netflixClone.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.netflixClone.dao.UserRepository;
import com.netflixClone.dao.VideoRepository;
import com.netflixClone.entity.User;
import com.netflixClone.entity.Video;
import com.netflixClone.exception.ResourceNotFoundException;

@Component
public class ServiceUtils {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private VideoRepository videoRepository;
	
	public User getUserByEmailOrThrow(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
	}
	
	public User getUserByIdOrThrow(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
	}
	
	public Video getVideoByIdOrThrow(Long id) {
		return videoRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Video with id " + id + " not found"));
	}

}
