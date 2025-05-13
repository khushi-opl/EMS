package com.employee.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.sql.RowIdLifetime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.employee.domain.User;
import com.employee.enums.GenderEnum;
import com.employee.enums.RoleEnum;
import com.employee.exception.CsvValidationException;
import com.employee.exception.ExcelValidatationException;
@Component
public class DocumentHelper {
	@Autowired
	private  BCryptPasswordEncoder bCryptPasswordEncoder;
	
//	private static final List<String> headerlist = List.of("username","name","ContactNo","password","gender","email","address","role","dob");
	
	public  ResponseEntity<byte[]> excelFormat(String format) {
	    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	    	System.out.println(format);
	        if (format.equals("csv")) {
	          
	            String csvContent = generateCsvContent(); 
	            out.write(csvContent.getBytes(StandardCharsets.UTF_8));

	            return ResponseEntity.ok()
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"User_Blank_Format.csv\"")
	                    .header(HttpHeaders.CONTENT_TYPE, "text/csv")
	                    .body(out.toByteArray());

	        } else if (format.equals("xlsx")) {
	           
	            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
	                Sheet sheet = workbook.createSheet("User");
	                Row row = sheet.createRow(0);

                List<String> headerList = List.of("username","name", "ContactNo", "password", "gender", "email", "address", "role","dob","pincode");
	                for (int i = 0; i < headerList.size(); i++) {
	                    row.createCell(i).setCellValue(headerList.get(i));
	                }
                workbook.write(out);
	            }

	            return ResponseEntity.ok()
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"User_Blank_Format.xlsx\"")
	                    .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	                    .body(out.toByteArray());

	        } else {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("Invalid format: please use 'csv' or 'xlsx'.".getBytes());
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error generating file".getBytes());
	    }
	}


	private  String generateCsvContent() {
	    StringBuilder builder = new StringBuilder();
	    builder.append("Username,Name,ContactNo,Password,Gender,Email,Address,Role,Dob,PinCode\n");
	    builder.append("\n");
	    return builder.toString();
	}

	public  List<User> saveDataFromExcel(InputStream inputStream) throws IOException, ExcelValidatationException  {
	    List<User> userList = new ArrayList<>();

	    try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
	        Sheet sheet = workbook.getSheet("User");
	        Iterator<Row> rowIterator = sheet.iterator();

	        if (rowIterator.hasNext()) {
	            rowIterator.next(); 
	        }
	        int rowIndex = 1;

	        while (rowIterator.hasNext()) {
	        	rowIndex++;
	            Row row = rowIterator.next();	 
	            if (row == null || row.getCell(0) == null) {
	                throw new ExcelValidatationException("Invalid data Row " + rowIndex + " is empty ");
	            }
	            try {
	                String username = row.getCell(0).getStringCellValue().trim();
	                String name= row.getCell(1).getStringCellValue().trim();	           
	                String contact = NumberToTextConverter.toText(row.getCell(2).getNumericCellValue());
	                String password = row.getCell(3).getStringCellValue().trim();
	                String genderStr = row.getCell(4).getStringCellValue().trim().toUpperCase();
	                String email = row.getCell(5).getStringCellValue().trim();
	                String address = row.getCell(6).getStringCellValue().trim();
	                String roleStr = row.getCell(7).getStringCellValue().trim().toUpperCase();
	                Date dob = null;
	                if (row.getCell(8) != null) { 
	                    dob = row.getCell(8).getDateCellValue(); 
	                }
	                String pincode=row.getCell(9).getStringCellValue().trim();

	                if (name.isEmpty() || password.isEmpty() || email.isEmpty() || roleStr.isEmpty()) {
	                    throw new ExcelValidatationException(" failed: Required field is missing at row " );
	                }

	                if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
	                	throw new ExcelValidatationException("Invalid email format " );
	                }

	                if (pincode.matches(("^\\d{5,6}$"))) {
	                	throw new ExcelValidatationException("Pincode Length Must be 6!");
	                }

	                GenderEnum gender = GenderEnum.valueOf(genderStr);
	                RoleEnum role = RoleEnum.valueOf(roleStr);
           
	                User user = new User();
	                user.setName(name);
	                user.setUsername(username);
	                user.setContactNumber(contact);
	                user.setPassword(bCryptPasswordEncoder.encode(password));
	                user.setGender(gender);
	                user.setEmail(email);
	                user.setAddress(address);
	                user.setRole(role);
	                user.setDob(dob); 
	                user.setPinCode(pincode);
	                user.setIsActive(true);

	                userList.add(user);

	            }
	            catch (Exception e) {
			        e.printStackTrace();
//			        throw new RuntimeException("Failed to process Excel file: " + e.getMessage());
			    } 
	            rowIndex++;       
	    }	           
	    return userList;
	    }
	}

	 public List<User> parseUsersFromCsv(InputStream is) {
	        List<User> users = new ArrayList<>();
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        try (
	        		
	            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim());
	        ) {
	        	 int row = 1;
	            for (CSVRecord record : csvParser) {
	            	  row++;
	                User user = new User();
	                if (record.get("Username").isEmpty()) {
	                    throw new CsvValidationException("Row: " + row + ": Username is required.");
	                }
//	                user.setUsername(record.get("Username"));
	                if (record.get("Name").isEmpty()) {
	                    throw new CsvValidationException("Row " + row + ": Name is required.");
	                }
	                if (!record.get("ContactNo").matches("\\d{10}")) {
	                    throw new CsvValidationException("Row " + row + ": Invalid contact number. Must be 10 digits.");
	                }
	                String rawPassword = record.get("Password").trim();
	                if (rawPassword.length() < 6) {
	                    throw new CsvValidationException("Row " + row + ": Password must be at least 6 characters.");
	                }
	                
	                String genderStr = record.get("Gender").trim().toUpperCase();
	                if (!genderStr.equals("MALE") && !genderStr.equals("FEMALE") && !genderStr.equals("OTHER")) {
	                    throw new CsvValidationException("Row " + row + ": Gender must be MALE, FEMALE, or OTHER.");
	                }
	                if(record.get("Email").matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
	                {
	                    throw new CsvValidationException("Row " + row + ": Invalid email format.");
	                }
	                user.setAddress(record.get("Address"));
	                String role = record.get("Role").trim().toUpperCase();
	                if(!role.equals("ADMIN") && !role.equals("USER")) {
	                	throw new CsvValidationException("Row " + row + ": Role must be ADMIN or USER");
	                }
	                String dobStr = record.get("Dob");
	                Date dob = sdf.parse(dobStr);
	                user.setDob(dob);
	                user.setPinCode(record.get("PinCode"));
	                user.setIsActive(true);
	                users.add(user);
	            }
	        }
	           
	        catch (CsvValidationException ex) {
					throw ex;				
					}
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	        return users;
	    }
	}
		
	
