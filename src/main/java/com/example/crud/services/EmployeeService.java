package com.example.crud.services;

import com.example.crud.db.models.Employee;
import com.example.crud.db.dao.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableJpaRepositories("com.example.crud.db.dao")
public class EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired private EmployeeRepository repository;

    public List<Employee> findAll() {
        return repository.findAll();
    }

    public Employee saveEmployee(Employee employee) {
        return repository.saveAndFlush(employee);
    }

    public Employee getOne(Long id) {
        return repository.getOne(id);
    }

    public List<Employee> findByLastName(String name) {
        return repository.findByLastName(name);
    }

    public void delete(Long id) {
        repository.delete(id);
    }
/*
    public List<Employee> fetchByLastNameLength(Long length) {
        return repository.fetchByLastNameLength(length);
    }
*/

}
