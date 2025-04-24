package com.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
	
	@GetMapping("/getAllUsers")
    public ResponseEntity<List<UserProxy>> getAllUsers() {
        List<UserProxy> users = service.getAllUsers();
        return  ResponseEntity.ok(users);
    }
	@GetMapping("/generate-bulkstudent/{size}")
	public String saveBulkStd(@PathVariable Integer size)
	{
		return service.saveBulkStd(size);
	}
	 @GetMapping("/getCurrentUser/{username}")
	    public ResponseEntity<User> getCurrentUser(@PathVariable String username) {
		 return ResponseEntity.status(HttpStatus.OK).body(service.getCurrentUser(username));
		 
	    }
	 @PostMapping("/forgotPassword/{token}")
	 public String resetPassword(@PathVariable String token,@RequestBody UpdatePassProxy password) {
	        return service.resetPassword(token,password);
	    }
	 @GetMapping("/sendLink/{name}")
	 public String sendLink(@PathVariable String name) {
	        return service.sendLink(name);
	    }
	 @GetMapping("/search/{page}/{size}/{name}")
	    public Page<User> searchStudents(
	        @PathVariable int page, 
	        @PathVariable int size, 
	        @PathVariable String name
	    ) {
	        return service.searchStudents(page, size, name);
	    }
	
	 @GetMapping("/getUserById/{id}")
	    public ResponseEntity<UserProxy> getUserById(@PathVariable Long id) {
	        return ResponseEntity.status(HttpStatus.OK).body(service.getUserById(id));
	    }
	 @PostMapping("/saveUser")
	 public ResponseEntity<String> saveUser(@RequestPart("user") UserProxy userProxy, @RequestPart("profileImage") MultipartFile profileImage) {
			System.err.println("controller");
			return new ResponseEntity<String>(service.saveUser(userProxy, profileImage), HttpStatus.OK);			
		}
	 
	 @PostMapping("/login")
		public ResponseEntity <LoginResponse> login(@RequestBody LoginRequest logReq)
		{
		 System.err.println("controller");
			return new ResponseEntity<>(service.login(logReq),HttpStatus.ACCEPTED);
		}
	 @PutMapping("/updateUser/{id}")
	    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserProxy proxy) {
	        return ResponseEntity.status(HttpStatus.OK).body(service.updateUser(id, proxy));
	    }
	 @DeleteMapping("/deleteUser/{id}")
	    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
	        return ResponseEntity.status(HttpStatus.OK).body(service.deleteUser(id));
	    }
	 @GetMapping("/getAllstdByPage/{page}/{student}/{sortBy}")
		public Page<User> getAllstdByPage(@PathVariable Integer page,@PathVariable Integer student,@PathVariable String sortBy) {
			return service.getAllstdByPage(student, page,sortBy);	
		}
	 @GetMapping("/getUsers/{page}/{student}/{sortBy}")
		public Page<User> getUsers(@PathVariable Integer page,@PathVariable Integer student,@PathVariable String sortBy) {
			return service.getUsers(student, page,sortBy);
			
		}


}
