package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
        SELECT e FROM Employee e
        JOIN FETCH e.user
        LEFT JOIN FETCH e.department
        LEFT JOIN FETCH e.jobTitle
        LEFT JOIN FETCH e.manager
        """)
    List<Employee> findAllWithDetails();

    @Query("""
        SELECT e FROM Employee e
        JOIN FETCH e.user
        LEFT JOIN FETCH e.department
        LEFT JOIN FETCH e.jobTitle
        LEFT JOIN FETCH e.manager
        WHERE e.id = :id
        """)
    Optional<Employee> findByIdWithDetails(Long id);

    @Query("""
        SELECT e FROM Employee e
        JOIN FETCH e.user
        LEFT JOIN FETCH e.department
        LEFT JOIN FETCH e.jobTitle
        LEFT JOIN FETCH e.manager
        WHERE e.user.username = :username
        """)
    Optional<Employee> findByUserUsername(String username);
}
