package com.zianajafian.employee.service.controller;

import com.zianajafian.employee.service.entity.Employee;
import com.zianajafian.employee.service.payload.EmployeeDepartment;
import com.zianajafian.employee.service.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public List<Employee> getAllDepartment(){
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public Employee getDepartmentById(@PathVariable("id") Long employeeId){
        return employeeService.getEmployeeById(employeeId);
    }

    @PostMapping
    public Employee saveDepartment(@RequestBody Employee employee){
        return employeeService.saveEmployee(employee);
    }

    @GetMapping("/{id}/department")
    public EmployeeDepartment getEmployeeWithDepartment(@PathVariable("id") Long employeeId){
        return employeeService.getEmployeeWithDepartment(employeeId);
    }

}
