package com.example.crud.test;

import com.example.crud.db.models.Address;
import com.example.crud.db.models.Employee;
import com.example.crud.db.models.Gender;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.Random;

import static java.lang.String.format;

/**
 * Examples illustrating the use of JPA with the employee domain
 * com.example.crud.models.
 * 
 * @see BasicTest
 */
public class SamplePopulation {

    private static final Logger log = LoggerFactory.getLogger(SamplePopulation.class);

    Faker fake = new Faker(); //TODO(gburd): https://github.com/joselufo/RandomUserApi https://randomuser.me/

    /**
     * Create the specified number of random sample employees.  
     */
    public void createNewEmployees(EntityManager em, int quantity) {
        for (int index = 0; index < quantity; index++) {
            em.persist(createRandomEmployee());
        }
    }

    public Employee createRandomEmployee() {
        Random r = new Random();

        Employee emp = new Employee();
        emp.setSocialSecurityNumber(format("%d-%d-%d", r.nextInt(999), r.nextInt(99), r.nextInt(9999)));
        emp.setGender(Gender.values()[r.nextInt(2)]);
        emp.setFirstName(fake.name().firstName());
        emp.setLastName(fake.name().lastName());
        emp.addPhoneNumber("HOME", fake.phoneNumber().phoneNumber().toString());
        emp.addPhoneNumber("WORK", fake.phoneNumber().phoneNumber().toString());
        emp.addPhoneNumber("MOBILE", fake.phoneNumber().cellPhone().toString());

        emp.setAddress(new Address(fake.address().city(), fake.address().country(), "", fake.address().zipCode(), fake.address().streetAddress()));

        return emp;
    }
}
