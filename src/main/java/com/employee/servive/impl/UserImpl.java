package com.employee.servive.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.employee.domain.ResetPassword;
import com.employee.domain.UpdatePassProxy;
import com.employee.domain.User;
import com.employee.enums.GenderEnum;
import com.employee.enums.RoleEnum;
import com.employee.proxy.LoginRequest;
import com.employee.proxy.LoginResponse;
import com.employee.proxy.UserProxy;
import com.employee.repo.ResetPwRepo;
import com.employee.repo.UserRepo;
import com.employee.servive.UserService;
import com.employee.utils.JwtUtil;
import com.employee.utils.MapperUtil;
import com.github.javafaker.Faker;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class UserImpl implements UserService {

	@Autowired
	public UserRepo repo;

//	 @Autowired
//	 private JavaMailSender emailSender; 

	@Autowired
	private MapperUtil mapper;

	@Autowired
	private ResetPwRepo pwrepo;
	@Autowired
	private AuthenticationManager authtAuthenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private EntityManager entityManager;

	@Override

	public List<UserProxy> getAllUsers() {

		List<User> alldataList = repo.getAllUsers();
		return mapper.convertor(repo.findAll(), UserProxy.class);
//		return alldataList;
	}

	@Override
	public UserProxy getUserById(Long id) {
		User user = repo.findById(id).orElseThrow(() -> new RuntimeException("Id Not Found"));
		return mapper.convertor(user, UserProxy.class);
	}

	@Override
	public String saveUser(UserProxy userProxy, MultipartFile profileImage) {

		String fileName = null;
		String path = null;

		try {

			String inPath = new ClassPathResource("").getFile().getAbsolutePath();
			path = inPath + File.separator + "static" + File.separator + "documents";

			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
			fileName = profileImage.getOriginalFilename();
			String absolutePath = path + File.separator + fileName;

			Files.copy(profileImage.getInputStream(), Paths.get(absolutePath), StandardCopyOption.REPLACE_EXISTING);

			userProxy.setFileName(fileName);
			userProxy.setFileData(profileImage.getBytes());
			userProxy.setFileSize((profileImage.getSize() / 1000) + "kb");
			userProxy.setContentType(profileImage.getContentType());
			System.out.println("Image saved to: " + absolutePath);
			String fileUrl = "/static/documents/" + fileName;

		} catch (java.io.IOException e) {
			e.printStackTrace();
		}

		userProxy.setPassword(encoder.encode(userProxy.getPassword()));
		User user = mapper.convertor(userProxy, User.class);

		repo.save(user);
		return "saved successfully...";

	}

	@Override
	public String updateUser(Long id, UserProxy proxy) {
		Optional<User> std = repo.findById(id);
		System.err.println(id);
		if (std.isPresent()) {
			User user = std.get();
			Predicate<String> predicate = s -> Objects.isNull(s) || s.equals("");
			user.setName(predicate.test(proxy.getName()) ? user.getName() : proxy.getName());
			user.setDob(predicate.test(proxy.getDob()) ? user.getDob() : proxy.getDob());
			user.setUsername(predicate.test(proxy.getUsername()) ? user.getUsername() : proxy.getUsername());
			if (proxy.getGender() != null) {
				user.setGender(proxy.getGender());
			}
			user.setPassword(
					predicate.test(proxy.getPassword()) ? user.getPassword() : encoder.encode(proxy.getPassword()));
			if (proxy.getRole() != null) {
				user.setRole(proxy.getRole());
			}
			user.setAddress(predicate.test(proxy.getAddress()) ? user.getAddress() : proxy.getAddress());
			user.setEmail(predicate.test(proxy.getEmail()) ? user.getEmail() : proxy.getEmail());
//			user.setProfileImage(predicate.test(proxy.getProfileImage())? user.getProfileImage():proxy.getProfileImage());
			user.setContactNumber(
					predicate.test(proxy.getContactNumber()) ? user.getContactNumber() : proxy.getContactNumber());
			user.setPinCode(predicate.test(proxy.getPinCode()) ? user.getPinCode() : proxy.getPinCode());
			repo.save(user);
			System.err.println(user);

			return "Updated successfully";

		}
		return "No id Found";
	}

	@Override
	public String deleteUser(Long id) {
		repo.deleteById(id);
		return "deleted successfully";

	}

	@Override
	public Page<User> getAllstdByPage(Integer student, Integer page, String sortBy) {
		Page<User> all = repo.findAll(PageRequest.of(page - 1, student, Sort.by(sortBy)));
		return all;

	}

	@Override
	public LoginResponse login(LoginRequest logReq) {

		System.err.println("impl");
		System.out.println(logReq.toString());
		Authentication auth = new UsernamePasswordAuthenticationToken(logReq.getUsername(), logReq.getPassword());
		Authentication authresult = authtAuthenticationManager.authenticate(auth);
		System.err.println(authresult);
		if (authresult.isAuthenticated()) {
			User user = repo.findByUsername(logReq.getUsername())
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));
			String role = user.getRole().toString();
			String token = jwtUtil.generateToken(logReq.getUsername(), role);
			return new LoginResponse(logReq.getUsername(), token, role);

		}

		return new LoginResponse(logReq.getUsername(), "Failed Request", "No Role");
	}

	private static Faker faker = new Faker();
	private static Random random = new Random();

	private User generateStd() {
		User user = new User();

		user.setName(faker.name().fullName());
		user.setDob(faker.date().birthday().toString());
		user.setUsername(faker.name().fullName());
		user.setPassword(faker.internet().password());
		user.setGender(GenderEnum.values()[random.nextInt(GenderEnum.values().length)]);
		user.setAddress(faker.address().fullAddress());
		user.setEmail(faker.internet().emailAddress());
		user.setContactNumber(faker.phoneNumber().phoneNumber());
		user.setPinCode(faker.address().zipCode());
		user.setRole(RoleEnum.values()[random.nextInt(RoleEnum.values().length)]);

		String IMAGE_DIR = "src/main/resources/static/img";
		Random r = new Random();

		try {
			File[] imageFiles = new File(IMAGE_DIR)
					.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
			if (imageFiles != null && imageFiles.length > 0) {
				File imageFile = imageFiles[r.nextInt(imageFiles.length)];
				byte[] fileData = Files.readAllBytes(imageFile.toPath());

				user.setFileName(imageFile.getName());
				user.setFileData(fileData);
				user.setFileSize((fileData.length / 1000) + "kb");
				user.setContentType(Files.probeContentType(imageFile.toPath()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Fake employee created: " + user.getUsername());
		return user;
	}

	@Override
	public String saveBulkStd(Integer noofstudent) {
		for (int i = 1; i <= noofstudent; i++) {
			repo.save(generateStd());
		}
		return "Saved";
	}

	@Override
	public User getCurrentUser(String username) {
		return repo.getUserByUsernameAndRole(username);

	}

	public Page<User> getUsers(Integer student, Integer page, String sortBy) {
		PageRequest pageRequest = PageRequest.of(page - 1, student, Sort.by(sortBy));
		return repo.findByRole("USER", pageRequest);

	}

	@Override
	@Transactional
	public String resetPassword(String token, UpdatePassProxy updatePass) {
		System.err.println(token);
		Optional<ResetPassword> resetPassword = pwrepo.findById(token);
		if(resetPassword.isPresent()) {
		if (resetPassword.get().getExpiryTime() < System.currentTimeMillis()) {
			
			pwrepo.deleteById(token);
			return "Expired";
			
		}}
		User user = repo.findByUsername(resetPassword.get().getUsername()).orElseThrow();
		user.setPassword(encoder.encode(updatePass.getPassword()));
		repo.save(user);
		pwrepo.deleteById(token);
		return "Update Successfully.";
	}

	@Override
	public String sendLink(String username) {
		User user = repo.findByUsername(username).orElseThrow();	
		String uuid = UUID.randomUUID().toString();
		ResetPassword resetPassword = new ResetPassword(uuid, username, System.currentTimeMillis() + (5 * 60 * 1000));
		pwrepo.save(resetPassword);
		String resetUrl = "http://localhost:4200/resetpw?token=" + uuid;
		System.err.println(resetUrl);
		return "send";
	}

	@Override
	public Page<User> searchStudents(int page, int size, String query) {
 {
	 return repo.findByUsernameContaining(query, PageRequest.of(page-1, size));
	}


	}
	}

//		 try {
//	            // Decrypt the username and password using AES
//	            String decryptedUsername = AESUtils.decrypt(logReq.getUsername(), SECRET_KEY);
//	            String decryptedPassword = AESUtils.decrypt(logReq.getPassword(), SECRET_KEY);
//
//	            // Authenticate using decrypted credentials
//	            Authentication authentication = new UsernamePasswordAuthenticationToken(decryptedUsername, decryptedPassword);
//	            Authentication authResult = authtAuthenticationManager.authenticate(authentication);
//
//	            if (authResult.isAuthenticated()) {
//	                // Fetch user from DB
//	                User user = repo.findByUsername(decryptedUsername)
//	                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//	                // Generate JWT token
//	                String token = jwtUtil.generateToken(decryptedUsername, user.getRole().toString());
//
//	                // Return response with token and role
//	                return new LoginResponse(decryptedUsername, token, user.getRole().toString());
//	            } else {
//	                throw new RuntimeException("Authentication failed");
//	            }
//	        } catch (Exception e) {
//	            throw new RuntimeException("Login failed", e);
//	        }
//	    }
//	}

//		System.err.println("impl");
//		System.out.println(logReq.toString());
//		Authentication auth=new UsernamePasswordAuthenticationToken(logReq.getUsername(), logReq.getPassword());
//		Authentication authresult= authtAuthenticationManager.authenticate(auth);
//		System.err.println(authresult);
//		if(authresult.isAuthenticated())
//		{
//			User user = repo.findByUsername(logReq.getUsername())
//				    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//			        String role = user.getRole().toString();
//			        String token = jwtUtil.generateToken(logReq.getUsername(), role);
//			        return new LoginResponse(logReq.getUsername(),token, role);
//	
//		}

//	return new LoginResponse(logReq.getUsername(),"Failed Request","No Role");
//	}
//	}
