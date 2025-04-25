package com.employee.servive;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.employee.domain.UpdatePassProxy;
import com.employee.domain.User;
import com.employee.proxy.LoginRequest;
import com.employee.proxy.LoginResponse;
import com.employee.proxy.UserProxy;


public interface UserService {
	public List<UserProxy> getAllUsers();
	public Page<User> getUsers(Integer student,Integer page,String sortBy);
	public UserProxy getUserById(Long id);
	public String deleteUser(Long id);
	public String saveUser(UserProxy userProxy, MultipartFile profileImage);
	public String resetPassword(String token,UpdatePassProxy updatePass);
	 public String sendLink(String username);
	public String updateUser(Long id, UserProxy proxy);
	public Page<User> getAllstdByPage(Integer student,Integer page,String sortBy);
	public  LoginResponse login(LoginRequest logReq);
	public String saveBulkStd(Integer size);
	public User getCurrentUser(String username);
	public Page<User> searchStudents(int page, int size,String query);
	public byte[] getExcelFileOfData();
}