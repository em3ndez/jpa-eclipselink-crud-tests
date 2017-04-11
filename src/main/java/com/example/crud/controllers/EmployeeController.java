package com.example.crud.controllers;

import com.example.crud.db.annotations.Retry;
import com.example.crud.db.models.Employee;
import com.example.crud.services.EmployeeService;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@Transactional(propagation = Propagation.MANDATORY)
public class EmployeeController {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired private EmployeeService employeeService;

    /**
     * Read all employees.
     *
     * Examples:
     *  1. HTTP GET localhost:8443/api/v1/employees
     *  2. curl -i -X GET localhost:8443/employees
     */
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @RequestMapping(value = "/api/v1/employees", method = GET, produces = "application/json")
    public ResponseEntity<List<Employee>> index() {
        log.debug("Getting all employees...");
        @SuppressWarnings("unchecked") ResponseEntity response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        try {
            List<Employee> employees = employeeService.findAll();
            if (employees != null) {
                response = ResponseEntity.status(HttpStatus.OK).body(employees);
            } else {
                response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return response;
    }

    /**
     * Create a new employee.
     *
     * Examples:
     *  1. HTTP POST localhost:8443/api/v1/employee firstName=Tedd lastName=Hanson honorific=Mr suffix=Jr. socialSecurityNumber=555-55-5555
     *  2. curl -i -X POST -H "Content-Type:application/json" -d '{ "firstName" : "Karl", "lastName" : "Pasim", "socialSecurityNumber" : "123-23-2312" }' localhost:8443/api/v1/employee
     */
    @Retry(times = 3, on = org.springframework.dao.OptimisticLockingFailureException.class)
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @RequestMapping(value = "/api/v1/employee", method = POST, consumes = "application/json")
    public ResponseEntity<Long> addEmployee(@RequestBody Employee employee) {
        log.debug("Inserting employee");
        @SuppressWarnings("unchecked") ResponseEntity response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        try {
            val e = employeeService.saveEmployee(employee);
            if (e != null) {
                response = ResponseEntity.status(HttpStatus.CREATED).body(e);
            } else {
                response = ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return response;
    }

    /**
     * Read an employee by primary key (id).
     *
     * Examples:
     *  1. HTTP GET localhost:8443/api/v1/employee/851864136237137920
     *  2. curl -i -X GET localhost:8443/employee/851864136237137920
     */
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @RequestMapping(value = "/api/v1/employee/{id:[\\d]+}", method = GET)
    public ResponseEntity<Employee> find(@PathVariable Long id) {
        log.debug("Getting a specific employee by id {}", id);
        @SuppressWarnings("unchecked") ResponseEntity response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        try {
            Employee employee = employeeService.getOne(id);
            if (employee != null) {
                response = ResponseEntity.status(HttpStatus.OK).body(employee);
            } else {
                response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return response;
    }

    /**
     * Read the set of employees with a given last name.
     *
     * Examples:
     *  1. HTTP GET localhost:8443/api/v1/employee?last=washington
     *  2. curl -i -X GET localhost:8443/employee?last=washington
     */
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @RequestMapping(value = "/api/v1/employee/named", method = GET)
    public ResponseEntity<List<Employee>> find(@RequestParam("last") String name) {
        log.debug("Getting a specific employee by name: {}", name);
        @SuppressWarnings("unchecked") ResponseEntity response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        try {
            List<Employee> employees = employeeService.findByLastName(name);
            if (employees != null) {
                response = ResponseEntity.status(HttpStatus.OK).body(employees);
            } else {
                response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return response;
    }

    /**
     * Delete a new employee.
     *
     * Examples:
     *  1. HTTP DELETE localhost:8443/api/v1/employee/851864136237137920
     *  2. curl -i -X DELETE localhost:8443/api/v1/employee/851864136237137920
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    @RequestMapping(value = "/api/v1/employee/{id:[\\d]+}", method = DELETE)
    public ResponseEntity<Long> deleteEmployee(@PathVariable Long id) {
        log.debug("Remove an employee by id {}", id);
        @SuppressWarnings("unchecked") ResponseEntity response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        try {
            employeeService.delete(id);
            response = ResponseEntity.status(HttpStatus.OK).body(id);
        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return response;
    }

}
