package com.employee.servive.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.employee.exception.CsvValidationException;
import com.employee.exception.EmptyListException;
import com.employee.proxy.LoginRequest;
import com.employee.proxy.LoginResponse;
import com.employee.proxy.UserProxy;
import com.employee.repo.ResetPwRepo;
import com.employee.repo.UserRepo;
import com.employee.servive.UserService;
import com.employee.utils.AESutils;
import com.employee.utils.DocumentHelper;
import com.employee.utils.JwtUtil;
import com.employee.utils.MapperUtil;
import com.github.javafaker.Faker;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class UserImpl implements UserService {

//    private final AESutils AESutils;

	@Autowired
	public UserRepo repo;

	 @Autowired
	 private DocumentHelper documentHelper;

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

//    UserImpl(AESutils AESutils) {
//        this.AESutils = AESutils;
//    }

	@Override
	public UserProxy getUserById(Long id) {
		User user = repo.findById(id).orElseThrow(() -> new RuntimeException("Id Not Found"));
		System.out.println(user);
		return mapper.convertor(user, UserProxy.class);
	}

	@Override
	public String saveUser(UserProxy userProxy, MultipartFile profileImage) {

		String fileName = null;
		String path = null;

		try {

			String inPath = new ClassPathResource("").getFile().getAbsolutePath();
			path = inPath + File.separator + "static" + File.separator + "img";

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
		user.setIsActive(true);

		repo.save(user);
		return "saved successfully...";

	}

	@Override
	public String updateUser(Long id, UserProxy proxy,MultipartFile profileImage) {
		Optional<User> std = repo.findById(id);
		System.out.println(id);
		if (std.isPresent()) {
			
			User user = std.get();
			Predicate<String> predicate = s -> Objects.isNull(s) || s.equals("");
			user.setName(predicate.test(proxy.getName()) ? user.getName() : proxy.getName());
			user.setDob(proxy.getDob());
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
		
			if (!profileImage.isEmpty() ) {
				
				String fileName = null;
				String path = null;

				try {
					
					String inPath = new ClassPathResource("").getFile().getAbsolutePath();
					path = inPath + File.separator + "static" + File.separator + "img";

					File f = new File(path);
					if (!f.exists()) {
						f.mkdirs();
					}
					String ext=profileImage.getOriginalFilename().substring(profileImage.getOriginalFilename().lastIndexOf("."));
					fileName =UUID.randomUUID().toString() +ext;
					String absolutePath = path + File.separator + fileName;

					Files.copy(profileImage.getInputStream(), Paths.get(absolutePath), StandardCopyOption.REPLACE_EXISTING);

					user.setFileName(fileName);
					user.setFileData(profileImage.getBytes());
					user.setFileSize((profileImage.getSize() / 1000) + "kb");
					user.setContentType(profileImage.getContentType());
					System.out.println("Image saved to: " + absolutePath);
					String fileUrl = "/static/documents/" + fileName;

				} catch (java.io.IOException e) {
					e.printStackTrace();
				}

			}else {
				System.err.println("Image not availabble");
				
			}
//			user.setProfileImage(predicate.test(proxy.getFileData())? user.getFileData():proxy.getFileData());
			user.setContactNumber(
					predicate.test(proxy.getContactNumber()) ? user.getContactNumber() : proxy.getContactNumber());
			user.setPinCode(predicate.test(proxy.getPinCode()) ? user.getPinCode() : proxy.getPinCode());
			repo.save(user);
			System.out.println("print");
			System.out.println("out");

			return "Updated successfully";

		}
		return "No id Found";
	}

	@Override
	public String deleteUser(Long id) {
		
		User user = repo.findById(id).get();
		user.setIsActive(false);
		repo.save(user);
		return "deleted successfully";

	}

	@Override
	public Page<User> getAllstdByPage(Integer student, Integer page, String sortBy) {
		PageRequest pageRequest = PageRequest.of(page - 1, student, Sort.by(sortBy));
		return repo.findAllByRoleAndIsActiveTrue(RoleEnum.ADMIN, pageRequest);
//		Page<User> all = repo.findByIsActiveTrue(RoleEnum.ADMIN,PageRequest.of(page - 1, student, Sort.by(sortBy)));
	

	}

	@Override
	public LoginResponse login(LoginRequest logReq) {
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
		user.setDob(faker.date().birthday(18,20));
		user.setUsername(faker.name().fullName());
		user.setPassword(encoder.encode(faker.internet().password()));
//		user.setPassword(faker.internet().password());
		user.setGender(GenderEnum.values()[random.nextInt(GenderEnum.values().length)]);
		user.setAddress(faker.address().fullAddress());
		user.setEmail(faker.internet().emailAddress());
		user.setContactNumber(faker.phoneNumber().phoneNumber());
		user.setPinCode(faker.address().zipCode());
		user.setRole(RoleEnum.values()[random.nextInt(RoleEnum.values().length)]);
		user.setIsActive(true);


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
		Optional<User> optionalUser= repo.findByUsername(username);
		if(optionalUser.isPresent()) {
			return optionalUser.get();
		}
		return null;

	}

	public Page<User> getUsers(Integer student, Integer page, String sortBy) {
		PageRequest pageRequest = PageRequest.of(page - 1, student, Sort.by(sortBy));
		return repo.findAllByRoleAndIsActiveTrue(RoleEnum.USER, pageRequest);

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
 
		PageRequest pageable = PageRequest.of(page - 1, size);
		 if (query.contains("@")) {
		        return repo.findByEmailContaining(query, pageable);
		    } 
		 else  {
		        return repo.findByUsernameContaining(query, pageable);
		    } 		    
	}

	@Override
	public byte[] getExcelFileOfData() {
		final String SHEET_NAME= "employeedata";
		final String[] HEADERS= {"EmpId","Name","Mobile","Gender","Address","Role"};
		List<User> listemp = repo.findAllByRole(RoleEnum.USER);
				
		try {
			
			Workbook workbook=new XSSFWorkbook();
			Sheet sheet=workbook.createSheet(SHEET_NAME);
			
			Row frow=sheet.createRow(0);
			frow.createCell(0).setCellValue(HEADERS[0]);	
			frow.createCell(1).setCellValue(HEADERS[1]);	
			frow.createCell(2).setCellValue(HEADERS[2]);	
			frow.createCell(3).setCellValue(HEADERS[3]);	
			frow.createCell(4).setCellValue(HEADERS[4]);
			frow.createCell(5).setCellValue(HEADERS[5]);	

			int rowcount=1;
			for(User emp:listemp)
			{
				Row row=sheet.createRow(rowcount);
				row.createCell(0).setCellValue(emp.getId());
				row.createCell(1).setCellValue(emp.getName());
				row.createCell(2).setCellValue(emp.getContactNumber());
				row.createCell(3).setCellValue(emp.getGender().toString());
				row.createCell(4).setCellValue(emp.getAddress());
				row.createCell(5).setCellValue(emp.getRole().toString());
				
				rowcount++;	
			}
			ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
			workbook.write(outputStream);
			return outputStream.toByteArray();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;	
	}

	@Override
	public ResponseEntity<?> downloadExcelFormat(String format) {
		try {
			format = format.toLowerCase();
			if(!format.equals("csv") && !format.equals("xlsx")) {
				return ResponseEntity.badRequest().body(Map.of("error","Invalid format: use 'csv' or 'xlsx'"));
			}
			return documentHelper.excelFormat(format);
			
		} catch (Exception e) {
			 e.printStackTrace(); 
	            return ResponseEntity
	                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Map.of("error", "Failed to generate file"));
		}
	}

	@Override
	public String saveDataFromExcel(MultipartFile file) {
	    List<User> list = new ArrayList<>();

	    try {
	      
	        if (file.getOriginalFilename().endsWith(".xlsx")) {
	            System.out.println("xlsx ");
	            list = documentHelper.saveDataFromExcel(file.getInputStream()); 
	        } else if (file.getOriginalFilename().endsWith(".csv")) {
	            System.out.println("csv");
	            list = documentHelper.parseUsersFromCsv(file.getInputStream()); 
	        } 
        repo.saveAll(list);
	        return "Data has been saved successfully.";	    } 
	     catch (IOException e) {
        e.printStackTrace();
	        throw new RuntimeException("Failed to read the uploaded file. Please try again.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Unexpected error while processing file: " + e.getMessage());
	    }
	}

//	@Override

	@Override
	public boolean checkUserExist(String email) {
		 return repo.existsByEmail(email);
		
	}

//	public List<UserProxy> getAllUsers() {
//		
//		List<User> alldataList = repo.findByIsActiveTrue();
//		
//		if(alldataList.isEmpty()) {
//			throw new EmptyListException("List is empty","101");
//		}
//		else {
//			return mapper.convertor(repo.findByIsActiveTrue(), UserProxy.class);
//		}
////		return alldataList;
//	}
	
	}
		




