package com.employee.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.domain.ResetPassword;
@Repository
public interface ResetPwRepo extends JpaRepository<ResetPassword , String> {

}
