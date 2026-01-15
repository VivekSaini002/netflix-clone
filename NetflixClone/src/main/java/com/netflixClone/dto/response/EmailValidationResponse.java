package com.netflixClone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailValidationResponse {
	
	private boolean exixts;
	private boolean available;
	

}
