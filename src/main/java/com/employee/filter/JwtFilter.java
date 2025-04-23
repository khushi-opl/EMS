package com.employee.filter;

import java.io.IOException;

import org.aspectj.lang.annotation.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.employee.servive.impl.MyUserDetailservice;
import com.employee.utils.AESutils;
import com.employee.utils.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private MyUserDetailservice service;
	
//	@Autowired
//	private AESutils aeSutils;
//	
//	 private static final String SECRET_KEY = "x9f63AB8Hd+qP9Tkfk5mnXdoYmD1aRbZ68zkDZn4gQQ="; 
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
//		   try {
//		        // Extract the token from request
//		        String token = jwtUtil.getTokenFromRequest(request);
//
//		        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//		            // Extract the username from the JWT token
//		            String encryptedUsername = jwtUtil.extractUserName(token);
//
//		            // Decrypt the username using AES decryption utility
//		            String decryptedUsername = aeSutils.decrypt(encryptedUsername, SECRET_KEY);
//
//		            // Load user details using decrypted username
//		            UserDetails userByUsername = service.loadUserByUsername(decryptedUsername);
//
//		            if (userByUsername != null && jwtUtil.validateToken(token, userByUsername)) {
//		                // Create an authentication token and set it in the security context
//		                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//		                        userByUsername, null, userByUsername.getAuthorities());
//		                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//		                SecurityContextHolder.getContext().setAuthentication(authToken);
//		            }
//		        }
//
//		        filterChain.doFilter(request, response);
//		    } catch (ExpiredJwtException e) {
//		        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//		        response.setContentType("application/json");
//		        response.getWriter().write("{\"error\": \"Token has expired\", \"message\": \"" + e.getMessage() + "\"}");
//		    } catch (Exception e) {
//		        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//		        response.setContentType("application/json");
//		        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + e.getMessage() + "\"}");
//		    }
//	}
//}


		try {
		System.err.println("try");
			
			String token = jwtUtil.getTokenFromRequest(request);
			if(token !=null && SecurityContextHolder.getContext().getAuthentication()==null)
			{
				System.err.println("try-if");
				String userName = jwtUtil.extractUserName(token);
				UserDetails userByUsername = service.loadUserByUsername(userName);
				System.err.println(userName);
				System.err.println(token);
				System.err.println( jwtUtil.validateToken(token, userByUsername));
				System.err.println(userByUsername);
				if(userByUsername !=null && jwtUtil.validateToken(token, userByUsername))
				{
					System.err.println("try-if");
					UsernamePasswordAuthenticationToken authtoken = new UsernamePasswordAuthenticationToken(userByUsername,null, userByUsername.getAuthorities());
					authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authtoken);;
				}
			}
			System.err.println("After try");
			filterChain.doFilter(request, response);
			
		} catch (ExpiredJwtException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"Token has expired\", \"message\": \"" + e.getMessage() + "\"}");
			
		}	
	}

	
}