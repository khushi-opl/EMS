package com.employee.servive.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.employee.domain.User;
import com.employee.repo.UserRepo;


@Service
public class MyUserDetailservice implements UserDetailsService {
	@Autowired
	private UserRepo repo;
	
	@Override
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		 User user= repo.findByUsername(name).orElseThrow(() -> new UsernameNotFoundException("Not Valid"));
		 System.out.println(user.toString());
		 return new MyUserDetail(user);
	}

}
