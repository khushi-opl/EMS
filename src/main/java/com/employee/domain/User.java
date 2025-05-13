package com.employee.domain;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.employee.enums.GenderEnum;
import com.employee.enums.RoleEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
		
		@Temporal(TemporalType.DATE)
		private Date dob;
	    private String username;
	    private String password;
	    @Enumerated(EnumType.ORDINAL)
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
		
		private Boolean isActive;
		
		
	    @CreationTimestamp
	    private Timestamp createdDate;

	    @UpdateTimestamp
	    private Timestamp modifiedDate;
	    
	    
	   

}
