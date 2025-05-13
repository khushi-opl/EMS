package com.employee.repo;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.domain.User;
import com.employee.enums.RoleEnum;
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
	Page<User> findByUsernameContaining(String username, PageRequest pageRequest);

	Page<User> findByIsActiveTrue(RoleEnum role,Pageable pageable);
	
	Optional<User> findByUsername(String username);
	
	Page<User> findAllByRoleAndIsActiveTrue(RoleEnum role, Pageable pageable);
	
	List<User> findAllByRole(RoleEnum role);
	
    boolean existsByEmail(String email);
    
    Page<User> findByEmailContaining(String email, Pageable pageable);
   

	
    
	
	
	
	
	
//	User findById();	
//	@Query(value = "SELECT u FROM User u WHERE u.role = 'USER'")	
//	@Query(value= "SELECT u FROM User u WHERE u.username = :username")
//	User findByUsername( String username);
//	@Query(value = "SELECT u FROM User u")
//	List<User> getAllUsers();
//	@Procedure(procedureName ="getAllUsers")
//	List<User> getAllUsers();	
//	@Query(value = "CALL getStudentById(:idValue)",nativeQuery = true)
//	User getStudentById(@Param ("idValue") Long idValue);	
//	@Procedure(procedureName = "getStudentById")
//	 @Query("SELECT u FROM User u WHERE u.role = 'USER'")
	
}
