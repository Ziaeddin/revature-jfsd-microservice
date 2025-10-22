package com.zianajafian.employee.service.service;

import com.zianajafian.employee.service.entity.Employee;
import com.zianajafian.employee.service.payload.EmployeeDepartment;

import java.util.List;

public interface EmployeeService {

    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long departmentId);
    Employee saveEmployee(Employee department);
    EmployeeDepartment getEmployeeWithDepartment(Long employeeId);
}
