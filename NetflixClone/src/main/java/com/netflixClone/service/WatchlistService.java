package com.netflixClone.service;

import com.netflixClone.dto.response.MessageResponse;
import com.netflixClone.dto.response.PageResponse;
import com.netflixClone.dto.response.VideoResponse;

public interface WatchlistService {

	MessageResponse addToWatchlist(String email, Long videoId);

	MessageResponse removeFromWatchlist(String email, Long videoId);

	PageResponse<VideoResponse> getWatchlistPaginated(String email, int page, int size, String search);
	

}
