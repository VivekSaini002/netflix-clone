package com.netflixClone.service;

import java.util.List;

import com.netflixClone.dto.request.VideoRequest;
import com.netflixClone.dto.response.MessageResponse;
import com.netflixClone.dto.response.PageResponse;
import com.netflixClone.dto.response.VideoResponse;
import com.netflixClone.dto.response.VideoStatsResponse;

import jakarta.validation.Valid;

public interface VideoService {

	MessageResponse createVideoByAdmin(VideoRequest videoRequest);

	PageResponse<VideoResponse> getAllAdminVideos(int page, int size, String search);

	MessageResponse updateVideoByAdmin(Long id, @Valid VideoRequest videoRequest);

	MessageResponse deleteVideoByAdmin(Long id);

	MessageResponse toggleVideoPublishStatusByAdmin(Long id, boolean value);

	VideoStatsResponse getAdminStats();

	PageResponse<VideoResponse> getPublishedVideo(int page, int size,String search, String email);

	List<VideoResponse> getFeaturedVideos();
	
	
	
	

}
