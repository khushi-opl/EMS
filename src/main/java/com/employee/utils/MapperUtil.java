package com.employee.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
@Component
public class MapperUtil {
	public <T, U> T convertor(U value, Class<T> clazz) {   
		ObjectMapper mapper = new ObjectMapper(); 
		return mapper.convertValue(value, clazz);   
		} 
	public <T, U> List<T> convertor(List<U> values, Class<T> clazz) { 
		ObjectMapper mapper = new ObjectMapper();     
		return values.stream().map(value -> mapper.convertValue(value, clazz)).collect(Collectors.toList());   
		}
	
	}


