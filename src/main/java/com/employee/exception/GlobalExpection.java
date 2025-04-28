package com.employee.exception;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.employee.proxy.ErrorResponse;

@RestControllerAdvice
public class GlobalExpection {
	
	@ExceptionHandler(value= EmptyListException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorResponse listEmptyException(EmptyListException ex) {
		return new ErrorResponse(ex.getErrMsg(),ex.getErrCode(),ex.toString());
		}
//	@ExceptionHandler(NoSuchElementException.class)
//	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
//	public ErrorResponse noSuchElementException(NoSuchElementException ex) {
//		return new ErrorResponse("NO Such Element Found","1000",ex.toString());	
//	}
//	@ExceptionHandler(value =Exception.class)
//	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
//	public ErrorResponse generalizedErrorException(Exception e)
//	{
//		return new ErrorResponse("Somenthing Went Wrong!!!","404",e.toString());
//	}
	
}
