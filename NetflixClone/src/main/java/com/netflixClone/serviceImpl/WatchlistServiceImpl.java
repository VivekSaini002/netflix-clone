package com.netflixClone.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.netflixClone.dao.UserRepository;
import com.netflixClone.dao.VideoRepository;
import com.netflixClone.dto.response.MessageResponse;
import com.netflixClone.dto.response.PageResponse;
import com.netflixClone.dto.response.VideoResponse;
import com.netflixClone.entity.User;
import com.netflixClone.entity.Video;
import com.netflixClone.service.WatchlistService;
import com.netflixClone.util.PaginationUtils;
import com.netflixClone.util.ServiceUtils;

@Service
public class WatchlistServiceImpl implements WatchlistService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private VideoRepository videoRepository;
	
	@Autowired
	private ServiceUtils serviceUtils;

	@Override
	public MessageResponse addToWatchlist(String email, Long videoId) {
		User user = serviceUtils.getUserByEmailOrThrow(email);
		Video video = serviceUtils.getVideoByIdOrThrow(videoId);
		user.addToWatchlist(video);
		userRepository.save(user);
		return new MessageResponse("Video added to watchlist successfully.");	
	}

	@Override
	public MessageResponse removeFromWatchlist(String email, Long videoId) {
		User user = serviceUtils.getUserByEmailOrThrow(email);
		Video video = serviceUtils.getVideoByIdOrThrow(videoId);
		user.removeFromWatchList(video);
		userRepository.save(user);
		return new MessageResponse("Video removed from watchlist successfully.");
	}

	@Override
	public PageResponse<VideoResponse> getWatchlistPaginated(String email, int page, int size, String search) {
		User user = serviceUtils.getUserByEmailOrThrow(email);
		
		Pageable pageable = PaginationUtils.createPageRequest(page, size);
		Page<Video> videoPage;
		
		if(search != null && !search.trim().isEmpty()) {
			videoPage = userRepository.searchWatchlistByUserId(user.getId(), search.trim(), pageable);
		} else {
			videoPage = userRepository.findWatchlistByUserId(user.getId(), pageable);
		}
		
		return PaginationUtils.toPageResponse(videoPage, VideoResponse::fromEntity);
	}
	

}
