package com.example.crud.test;

import com.example.crud.db.annotations.Retry;
import com.example.crud.db.models.Employee;
import com.example.crud.db.models.Gender;
import org.junit.Test;

import javax.persistence.*;

import org.eclipse.persistence.sessions.Session;

import java.util.List;

public class BasicTest {

    EntityManagerFactory emf;
    EntityManager em;
    //    Connection connection = em.unwrap(java.sql.Connection.class);

    @Test
    public void testMain() throws Exception {
        //emf = PersistenceTesting.createEMF(true);
        emf = Persistence.createEntityManagerFactory("default");
        em = emf.createEntityManager();
        Session session = em.unwrap(Session.class);
        session.getLogin().setQueryRetryAttemptCount(3);

        try {


            hire(100);

            // Add employee with 555 area code to satisfy a test query
            em.getTransaction().begin();
            Employee e = new Employee();
            e.setFirstName("John");
            e.setLastName("Doe");
            e.setGender(Gender.Male);
            e.setSocialSecurityNumber("111-22-3333");
            e.addPhoneNumber("HOME", "555-555-2222");
            em.persist(e);
            Long id = e.getId();

            em.getTransaction().commit();
            em.clear();

            queryAllEmployees(em);
            em.clear();

            queryEmployeeLikeAreaCode55(em);
            em.clear();

            modifyEmployee(em, id);
            em.clear();

            deleteEmployee(em, id);
            em.clear();

            em.close();

        } finally {
            emf.close();
        }
    }

    @Retry(times = 3, on = org.springframework.dao.OptimisticLockingFailureException.class)
    public int hire(int n) {
        em.getTransaction().begin();
        new SamplePopulation().createNewEmployees(em, n);
        em.getTransaction().commit();
        em.clear();
        return n;
    }

    public void queryAllEmployees(EntityManager em) {
        List<Employee> results = em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();

        System.out.println("Query All Results: " + results.size());

        results.forEach(e -> System.out.println("\t>" + e));
    }

    public void queryEmployeeLikeAreaCode55(EntityManager em) {
        System.out.println("\n\n --- Query Employee.phoneNumbers.areaCode LIKE '55%' ---");

        TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e JOIN e.phoneNumbers phones WHERE phones.number LIKE '55%'", Employee.class);
        List<Employee> emps = query.getResultList();

        emps.forEach(e -> System.out.println("> " + e));
    }

    public void modifyEmployee(EntityManager em, Long id) {
        System.out.println("\n\n --- Modify Employee ---");
        em.getTransaction().begin();

        Employee emp = em.find(Employee.class, id);
        emp.setSalary(1);

        TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID AND e.firstName = :FNAME", Employee.class);
        query.setParameter("ID", id);
        query.setParameter("FNAME", emp.getFirstName());
        emp = query.getSingleResult();

        em.getTransaction().commit();

    }

    public void deleteEmployee(EntityManager em, Long id) {
        em.getTransaction().begin();

        em.remove(em.find(Employee.class, id));
        em.flush();

        //em.getTransaction().rollback();
        em.getTransaction().commit();
    }

}
