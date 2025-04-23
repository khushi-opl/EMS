package com.employee.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.employee.filter.JwtFilter;
import com.employee.servive.impl.MyUserDetailservice;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private JwtFilter jwtFilter;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {  
		return http.csrf(AbstractHttpConfigurer::disable)          
				.httpBasic(AbstractHttpConfigurer::disable)         
				.formLogin(AbstractHttpConfigurer::disable)         
				.authorizeHttpRequests(authorizeRequests -> authorizeRequests       
						.requestMatchers( "/user/login","/user/saveUser","/user/generate-bulkstudent/**","/user/getCurrentUser/**","/user/getUserById/**").permitAll()    
//						.requestMatchers("/user/saveUser").hasAuthority("ADMIN")
						.anyRequest().authenticated())      
				.sessionManagement(sessionManagement -> sessionManagement         
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))       
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)          
				.build();}
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@Primary
	public UserDetailsService userDetailsService()
	{
		return new MyUserDetailservice();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider  dao= new DaoAuthenticationProvider();
		dao.setPasswordEncoder(bCryptPasswordEncoder());
		dao.setUserDetailsService(userDetailsService());
		return dao;
	}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
	{
		return config.getAuthenticationManager();
	}
}
