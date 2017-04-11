package com.example.crud.db.dao;

import com.example.crud.db.models.Employee;
import com.example.crud.db.models.Gender;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RepositoryRestResource
public interface EmployeeRepository extends BaseRepository<Employee, Long> {
    List<Employee> findByLastName(String lastName);
    List<Employee> findByGender(Gender gender);
    List<Employee> findByAgeLessThan(int age);
    List<Employee> findByAgeBetween(int age1, int age2);
//    List<Employee> fetchByLastNameLength(@Param("length") Long length);
}
