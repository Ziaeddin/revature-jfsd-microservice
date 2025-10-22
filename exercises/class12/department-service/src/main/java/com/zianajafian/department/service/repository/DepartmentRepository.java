package com.zianajafian.department.service.repository;

import com.zianajafian.department.service.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
