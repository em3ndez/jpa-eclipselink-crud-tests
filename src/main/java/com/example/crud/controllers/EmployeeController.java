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

    @Retry(times = 3, on = org.springframework.dao.OptimisticLockingFailureException.class)
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @RequestMapping(value = "/api/v1/employee", method = POST, consumes = "application/json")
    public ResponseEntity<Long> addEmployee(@RequestBody Employee employee) {
        log.debug("Inserting employee");
        @SuppressWarnings("unchecked") ResponseEntity response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        try {
            val e = employeeService.saveEmployee(employee);
            if (e != null) {
                response = ResponseEntity.status(HttpStatus.CREATED).body(e.getId());
            } else {
                response = ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return response;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    @RequestMapping(value = "/api/v1/employee/{id:[\\d]+}", method = DELETE)
    public ResponseEntity<Long> deleteEmployee(@PathVariable Long id) {
        log.debug("Deleting employee");
        @SuppressWarnings("unchecked") ResponseEntity response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        try {
            employeeService.delete(id);
            response = ResponseEntity.status(HttpStatus.OK).body(id);
        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return response;
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    //@RequestMapping(value = "/api/v1/employee/{id:[\\d]+}", method = GET)
    @RequestMapping(value = "/api/v1/employee/{id}", method = GET)
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

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @RequestMapping(value = "/api/v1/employee/{id:[\\d]+}", method = DELETE)
    public @ResponseBody Long delete(@PathVariable Long id) {
        log.debug("Remove an employee by id {}", id);
        employeeService.delete(id);
        return id;
    }

    /*
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @RequestMapping("/api/v1/employee/lastNameLength")
    public List<Employee> fetchByLength(Long length) {
        return employeeService.fetchByLastNameLength(length);
    }
    */
}
