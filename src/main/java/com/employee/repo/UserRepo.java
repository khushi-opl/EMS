package com.employee.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.domain.User;
@Repository
public interface UserRepo extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);
	
	@Query(value= "SELECT u FROM User u WHERE u.username = :username")
	User getUserByUsernameAndRole(@Param("username") String username);

//	@Query(value = "CALL getAllUsers()" ,nativeQuery = true)
//	List<User> getAllUsers();
	
	@Procedure(procedureName ="getAllUsers")
	List<User> getAllUsers();
	
//	@Query(value = "CALL getStudentById(:idValue)",nativeQuery = true)
//	User getStudentById(@Param ("idValue") Long idValue);
	
	@Procedure(procedureName = "getStudentById")
	User getStudentById(@Param("id_data") Long id);

}
