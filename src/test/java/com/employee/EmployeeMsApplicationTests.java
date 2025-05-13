package com.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.employee.domain.User;
import com.employee.enums.GenderEnum;
import com.employee.enums.RoleEnum;
import com.employee.proxy.UserProxy;
import com.employee.repo.UserRepo;
import com.employee.servive.impl.UserImpl;
import com.employee.utils.MapperUtil;

@ExtendWith(MockitoExtension.class)
//@DataJpaTest
class EmployeeMsApplicationTests {
	
	@Mock
	private UserRepo userRepo;
	
	@Mock
	private MapperUtil mapperUtil;
	
	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@InjectMocks
	private UserImpl userImpl;
	
	 @BeforeEach
	    void setUp() {
	        MockitoAnnotations.openMocks(this); 
	    }
	 @Test
	    void testGetUserById_ReturnsUserProxy() {
		 Long userId = 1L;
	        Date dob = new Date();

	        User user = new User();
	        user.setId(userId);
	        user.setName("John Doe");
	        user.setDob(dob);
	        user.setUsername("johndoe");
	        user.setPassword("password123");
	        user.setGender(GenderEnum.MALE);
	        user.setAddress("123 Main St");
	        user.setEmail("john@example.com");
	        user.setContactNumber("1234567890");
	        user.setPinCode("123456");
	        user.setRole(RoleEnum.USER);
	        user.setIsActive(true);

	        UserProxy proxy = UserProxy.builder()
	            .id(userId)
	            .name("John Doe")
	            .dob(dob)
	            .username("johndoe")
	            .password("password123")
	            .gender(GenderEnum.MALE)
	            .address("123 Main St")
	            .email("john@example.com")
	            .contactNumber("1234567890")
	            .pinCode("123456")
	            .role(RoleEnum.USER)
	            .isActive(true)
	            .build();

	        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
	        when(mapperUtil.convertor(user, UserProxy.class)).thenReturn(proxy);

	        UserProxy result = userImpl.getUserById(userId);

	        assertThat(result).isNotNull();
	        assertThat(result.getId()).isEqualTo(userId);
	        assertThat(result.getName()).isEqualTo("John Doe");
	        assertThat(result.getUsername()).isEqualTo("johndoe");
	        assertThat(result.getEmail()).isEqualTo("john@example.com");
	        assertThat(result.getRole()).isEqualTo(RoleEnum.USER);

	        verify(userRepo, times(1)).findById(userId);
	        verify(mapperUtil, times(1)).convertor(user, UserProxy.class);
	    }
	 
	 @Test
	    void testGetUserById_WhenIdNotFound_ThrowException() {
	        Long userId = 999L;

	        when(userRepo.findById(userId)).thenReturn(Optional.empty());

	        assertThatThrownBy(() -> userImpl.getUserById(userId))
	            .isInstanceOf(RuntimeException.class)
	            .hasMessageContaining("User not found with id: " + userId);

	        verify(userRepo).findById(userId);
	        verifyNoInteractions(mapperUtil);
	    }
	 @Test
	    void testSaveUser_SuccessfullySavesUserAndProcessesFile() throws IOException {
	     
	        byte[] fileContent = "image content".getBytes();
	        MockMultipartFile mockFile = new MockMultipartFile(
	                "profileImage", "profile.jpg", "image/jpeg", fileContent
	        );

	        UserProxy userProxy = UserProxy.builder()
	                .name("Jane Doe")
	                .dob(new Date())
	                .username("janedoe")
	                .password("plaintext")
	                .gender(GenderEnum.FEMALE)
	                .address("456 Elm St")
	                .email("jane@example.com")
	                .contactNumber("9876543210")
	                .pinCode("654321")
	                .role(RoleEnum.USER)
	                .build();

	        User user = new User(); 

	        when(bCryptPasswordEncoder.encode("plaintext")).thenReturn("encodedPassword");
	        when(mapperUtil.convertor(any(UserProxy.class), eq(User.class))).thenReturn(user);
	        when(userRepo.save(any(User.class))).thenReturn(user);
	        String result = userImpl.saveUser(userProxy, mockFile);

	        assertThat(result).isEqualTo("saved successfully...");
	        assertThat(userProxy.getFileName()).isEqualTo("profile.jpg");
	        assertThat(userProxy.getFileSize()).contains("kb");
	        assertThat(userProxy.getContentType()).isEqualTo("image/jpeg");
	        assertThat(userProxy.getFileData()).isEqualTo(fileContent);

	        verify(bCryptPasswordEncoder).encode("plaintext");
	        verify(mapperUtil).convertor(any(UserProxy.class), eq(User.class));
	        verify(userRepo).save(user);
	    }
	  @Test
	    void testDeleteUser_SuccessfullyDeactivatesUser() {
	        Long userId = 1L;
	        User user = new User();
	        user.setId(userId);
	        user.setIsActive(true);

	        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
	        when(userRepo.save(any(User.class))).thenReturn(user);

	        String result = userImpl.deleteUser(userId);

	        assertThat(user.getIsActive()).isFalse(); 
	        assertThat(result).isEqualTo("deleted successfully");

	        verify(userRepo).findById(userId);
	        verify(userRepo).save(user);
	    }
	  @Test
	    void testUpdateUser_SuccessfullyUpdatesUserWithImage() throws Exception {
	        // Given
	        Long userId = 1L;
	        User existingUser = new User();
	        existingUser.setId(userId);
	        existingUser.setName("Old Name");
	        existingUser.setUsername("olduser");
	        existingUser.setPassword("oldpass");
	        existingUser.setAddress("Old Address");
	        existingUser.setEmail("old@example.com");
	        existingUser.setContactNumber("1234567890");
	        existingUser.setPinCode("111111");
	        existingUser.setGender(GenderEnum.MALE);
	        existingUser.setRole(RoleEnum.ADMIN);

	        byte[] fileData = "image data".getBytes();
	        MockMultipartFile profileImage = new MockMultipartFile("profileImage", "image.jpg", "image/jpeg", fileData);

	        UserProxy proxy = UserProxy.builder()
	                .name("New Name")
	                .username("newuser")
	                .password("newpass")
	                .dob(new Date())
	                .gender(GenderEnum.FEMALE)
	                .role(RoleEnum.USER)
	                .address("New Address")
	                .email("new@example.com")
	                .contactNumber("9876543210")
	                .pinCode("222222")
	                .build();

	        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));
	        when(bCryptPasswordEncoder.encode("newpass")).thenReturn("encodedPass");
	        when(userRepo.save(any(User.class))).thenReturn(existingUser);

	        String result = userImpl.updateUser(userId, proxy, profileImage);

	        assertThat(result).isEqualTo("Updated successfully");
	        assertThat(existingUser.getName()).isEqualTo("New Name");
	        assertThat(existingUser.getUsername()).isEqualTo("newuser");
	        assertThat(existingUser.getPassword()).isEqualTo("encodedPass");
	        assertThat(existingUser.getGender()).isEqualTo(GenderEnum.FEMALE);
	        assertThat(existingUser.getRole()).isEqualTo(RoleEnum.USER);
	        assertThat(existingUser.getAddress()).isEqualTo("New Address");
	        assertThat(existingUser.getEmail()).isEqualTo("new@example.com");
	        assertThat(existingUser.getContactNumber()).isEqualTo("9876543210");
	        assertThat(existingUser.getPinCode()).isEqualTo("222222");
	        assertThat(existingUser.getFileData()).isEqualTo(fileData);
	        assertThat(existingUser.getContentType()).isEqualTo("image/jpeg");

	        verify(userRepo).findById(userId);
	        verify(bCryptPasswordEncoder).encode("newpass");
	        verify(userRepo).save(existingUser);
	    }
	  @Test
	    void testGetUsers_withPaginationAndSorting() {
	  
	        int pageSize = 5;
	        int pageNumber = 2;
	        String sortBy = "name";

	        List<User> mockUsers = List.of(new User(), new User()); // two dummy users
	        Page<User> mockPage = new PageImpl<>(mockUsers);

	        PageRequest expectedPageRequest = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortBy));

	        when(userRepo.findAllByRoleAndIsActiveTrue(RoleEnum.USER, expectedPageRequest))
	                .thenReturn(mockPage);
	 
	        Page<User> result = userImpl.getUsers(pageSize, pageNumber, sortBy);
	        assertNotNull(result);
	        assertEquals(2, result.getContent().size());
	        verify(userRepo).findAllByRoleAndIsActiveTrue(RoleEnum.USER, expectedPageRequest);
	    }
	}
	
	
