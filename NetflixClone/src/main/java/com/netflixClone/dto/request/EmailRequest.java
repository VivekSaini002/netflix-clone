package com.netflixClone.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequest {
	
	@NotBlank(message = "Email must not be blank")
	@Email(message = "Invalid email format")
	private String email;

}
