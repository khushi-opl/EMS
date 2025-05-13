package com.employee.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.employee.domain.UpdatePassProxy;
import com.employee.domain.User;
import com.employee.proxy.LoginRequest;
import com.employee.proxy.LoginResponse;
import com.employee.proxy.UserProxy;
import com.employee.servive.UserService;
import com.employee.utils.JwtUtil;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService service;
	@Autowired
	private JwtUtil jwtUtil;
	
	 @GetMapping("/checkUserExist/{email}")
	 public boolean checkUserExist(@PathVariable String email) {
		 return service.checkUserExist(email);
	 }
	
	@GetMapping("/download-excel-format/{format}")
	public ResponseEntity<?> downloadExcelFormat(@PathVariable String format)
	{
		return service.downloadExcelFormat(format);
//		final String FILE_NAME="Employee_Blank_Format.csv";
//		byte[] getExcelFileOfData= service.downloadExcelFormat();
//		return ResponseEntity
//				.status(HttpStatus.OK)
//				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + FILE_NAME + "\"")
//				.body(getExcelFileOfData);
	}
	
	@PostMapping("/dump-excel-data")
	public ResponseEntity<?> saveDataFromExcel(@RequestParam("filedata") MultipartFile excelfile) {
	    try {
	        String response = service.saveDataFromExcel(excelfile);
	        return ResponseEntity.ok(Map.of("status", response));
	    } catch (RuntimeException ex) {
	        return ResponseEntity
	                .badRequest()
	                .body(Map.of("error", ex.getMessage()));
	    } catch (Exception ex) {
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", "Unexpected error: " + ex.getMessage()));
	    }
	}




	@GetMapping("/downloadexceldata")
	public ResponseEntity<?> getExcelFileOfData() {
		try {
			final String FILE_NAME = "Employeedata.xlsx";
			byte[] excelData = service.getExcelFileOfData();

			if (excelData == null || excelData.length == 0) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Excel file data not found.");
			}

			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + FILE_NAME + "\"")
					.body(excelData);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error generating Excel file: " + e.getMessage());
		}
	}

	@GetMapping("/generate-bulkstudent/{size}")
	public ResponseEntity<String> saveBulkStd(@PathVariable Integer size) {
		try {
			return ResponseEntity.ok(service.saveBulkStd(size));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error generating bulk students: " + e.getMessage());
		}
	}

	@GetMapping("/getCurrentUser/{username}")
	public ResponseEntity<?> getCurrentUser(@PathVariable String username) {
		try {
			return ResponseEntity.ok(service.getCurrentUser(username));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching user: " + e.getMessage());
		}
	}

	@PostMapping("/forgotPassword/{token}")
	public ResponseEntity<String> resetPassword(@PathVariable String token, @RequestBody UpdatePassProxy password) {
		try {
			return ResponseEntity.ok(service.resetPassword(token, password));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error resetting password: " + e.getMessage());
		}
	}

	@GetMapping("/sendLink/{name}")
	public ResponseEntity<String> sendLink(@PathVariable String name) {
		try {
			return ResponseEntity.ok(service.sendLink(name));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error sending link: " + e.getMessage());
		}
	}

	@GetMapping("/search/{page}/{size}/{name}")
	public ResponseEntity<?> searchStudents(@PathVariable int page, @PathVariable int size, @PathVariable String name) {
		try {
			return ResponseEntity.ok(service.searchStudents(page, size, name));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error searching students: " + e.getMessage());
		}
	}

	@GetMapping("/getUserById/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.getUserById(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching user by ID: " + e.getMessage());
		}
	}

	@PostMapping("/saveUser")
	public ResponseEntity<String> saveUser(@RequestPart("user") UserProxy userProxy,
			@RequestPart("profileImage") MultipartFile profileImage) {
		try {
			System.out.println("USERPROXY"+userProxy.toString());
			return ResponseEntity.ok(service.saveUser(userProxy, profileImage));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving user: " + e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest logReq) {
		try {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.login(logReq));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
		}
	}

	@PutMapping("/updateUser/{id}")
	public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestPart("user") UserProxy proxy,
			@RequestPart("profileImage") MultipartFile profileImage) {
		try {
			System.out.println("controller");
			return ResponseEntity.ok(service.updateUser(id, proxy, profileImage));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating user: " + e.getMessage());
		}
	}

	@DeleteMapping("/deleteUser/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.deleteUser(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error deleting user: " + e.getMessage());
		}
	}

	@GetMapping("/getAllstdByPage/{page}/{student}/{sortBy}")
	public ResponseEntity<?> getAllstdByPage(@PathVariable Integer page, @PathVariable Integer student,
			@PathVariable String sortBy) {
		try {
			return ResponseEntity.ok(service.getAllstdByPage(student, page, sortBy));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching paged students: " + e.getMessage());
		}
	}

	@GetMapping("/getUsers/{page}/{student}/{sortBy}")
	public ResponseEntity<?> getUsers(@PathVariable Integer page, @PathVariable Integer student,
			@PathVariable String sortBy) {
		try {
			return ResponseEntity.ok(service.getUsers(student, page, sortBy));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching users: " + e.getMessage());
		}
	}
//	@GetMapping("/getAllUsers")
//  public ResponseEntity<?> getAllUsers() {
//      List<UserProxy> users = service.getAllUsers();
//      try {
//      if (users.isEmpty()) {
//          return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//      }
//      
//      return ResponseEntity.ok(users);
//  } catch (Exception e) {
//      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//              .body("An error occurred while fetching users: " + e.getMessage());
//  }
//}
}