package com.zianajafian.department.service.service;

import com.zianajafian.department.service.entity.Department;

import java.util.List;

public interface DepartmentService {

    List<Department> getAllDepartments();
    Department getDepartmentById(Long departmentId);
    Department saveDepartment(Department department);
}
