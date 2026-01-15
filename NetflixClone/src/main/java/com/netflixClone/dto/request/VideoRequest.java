package com.netflixClone.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VideoRequest {
	
	@NotBlank(message = "Title must not be blank")
	private String title;
	
	@Size(max = 4000, message = "Description must not exceed 4000 characters")
	private String description;
	
	private String rating;
	private Integer year;
	private Integer duration; 
	private String src;
	private String poster;
	private boolean published;
	private List<String> categories;

}
