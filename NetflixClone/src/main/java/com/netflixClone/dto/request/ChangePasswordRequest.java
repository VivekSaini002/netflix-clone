package com.netflixClone.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
	
	@NotBlank(message = "Current password must not be blank")
	private String currentPassword;
	@NotBlank(message = "New password must not be blank")
	private String newPassword;

}
