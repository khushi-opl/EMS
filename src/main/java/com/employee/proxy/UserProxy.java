package com.employee.proxy;



import java.util.Date;

import com.employee.enums.GenderEnum;
import com.employee.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProxy {
	private Long id;

	@NotBlank(message = "Name can not be Null")
	private String name;
	
	@NotNull
	private Date dob;

	@NotBlank(message = "Username cannot be blank")
	@Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
	private String username;

	@NotBlank(message = "Password cannot be blank")
	@Size(min = 3, message = "Password must be at least 3 characters")
	private String password;

	private GenderEnum gender;

	@NotBlank(message = "Address cannot be blank")
	@Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
	private String address;

	@NotNull(message = "Email is required")
	@Email(message = "Enter a valid email format")
	private String email;

	@NotNull(message = "Contact number is required")
	@Pattern(regexp = "(^$|[0-9]{10})", message = "Phone no Must be 10 digit")
	private String contactNumber;
	private String pinCode;

	@NotNull(message = "Role is required")
	private RoleEnum role;
	
	private String fileName;
	private String fileSize;
	private String contentType;
	private byte[] fileData;
	private Boolean isActive;
}
