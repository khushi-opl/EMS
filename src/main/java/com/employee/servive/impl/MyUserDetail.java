package com.employee.servive.impl;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.employee.domain.User;
import com.employee.repo.UserRepo;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class MyUserDetail implements UserDetails {

  
	private User user;

	public MyUserDetail(User user) {
		super();
		System.out.println(user);
		this.user = user;
		
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
//		return Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString()));
		return null;
	
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	
}
