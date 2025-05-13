package com.employee.exception;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
	
	 @ExceptionHandler(CsvValidationException.class)
	    public ResponseEntity<?> handleCsvValidationException(CsvValidationException ex) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	    }
	 
	 @ExceptionHandler(value= ExcelValidatationException.class)
	 public ResponseEntity<?> handleExcelValidatation(ExcelValidatationException ex){
		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		 
	 }

}
