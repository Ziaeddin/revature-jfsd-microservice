package com.zianajafian.employee.service.repository;

import com.zianajafian.employee.service.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
