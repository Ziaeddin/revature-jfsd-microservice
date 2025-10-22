package com.zianajafian.employee.service.service.impl;

import com.zianajafian.employee.service.entity.Employee;
import com.zianajafian.employee.service.payload.Department;
import com.zianajafian.employee.service.payload.EmployeeDepartment;
import com.zianajafian.employee.service.repository.EmployeeRepository;
import com.zianajafian.employee.service.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {


    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId).get();
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public EmployeeDepartment getEmployeeWithDepartment(Long employeeId) {
        // get employee by employee id
        Employee employee = employeeRepository.findById(employeeId).get();

        // get department from department service
        // call department service to get department by department code
        System.out.println(employee.getDepartmentId());
        Department department = restTemplate.getForObject("http://DEPARTMENT-SERVICE/api/departments/" + employee.getDepartmentId(), Department.class);


        System.out.println("Dep: "+department);

        //combine both employee and department
        EmployeeDepartment employeeDepartment = new EmployeeDepartment();
        employeeDepartment.setEmployee(employee);
        employeeDepartment.setDepartment(department);

        return employeeDepartment;
    }
}
