package com.employee.proxy;

import com.employee.enums.GenderEnum;
import com.employee.enums.RoleEnum;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProxy {
	private String id;
	private String name;
	private String dob;
    private String username;
    private String password;
    private GenderEnum gender;
    private String address;
    private String email;
    private String contactNumber;
    private String pinCode;
    private RoleEnum role;
    private String fileName;
	private String fileSize;
	private String contentType;
	private byte[] fileData;
}
