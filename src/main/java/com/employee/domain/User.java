package com.employee.domain;

import com.employee.enums.GenderEnum;
import com.employee.enums.RoleEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
		private String name;
		private String dob;
	    private String username;
	    private String password;
	    @Enumerated(EnumType.STRING)
	    private GenderEnum gender;
	    private String address;
	    private String email;
	    private String contactNumber;
	    private String pinCode;
	    @Enumerated(EnumType.STRING)
	    private RoleEnum role;
	    
	    private String fileName;
		private String fileSize;
		private String contentType;
		
		@Lob
		private byte[] fileData;
	    
	    
	   

}
