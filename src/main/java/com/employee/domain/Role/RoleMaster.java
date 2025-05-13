package com.employee.domain.Role;

import java.util.HashSet;
import java.util.Set;

import com.employee.domain.User;
import com.employee.enums.RoleEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//public class RoleMaster {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//	  @Enumerated(EnumType.STRING)
//	    private RoleEnum roleName; 
//	  
//	  @ManyToMany(mappedBy = "roles")
//	    private Set<User> users = new HashSet<>();
//}
