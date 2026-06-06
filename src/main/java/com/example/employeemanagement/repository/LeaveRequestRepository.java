package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    @Query("""
        SELECT l FROM LeaveRequest l
        JOIN FETCH l.employee e
        JOIN FETCH e.user
        """)
    List<LeaveRequest> findAllWithEmployee();

    @Query("""
        SELECT l FROM LeaveRequest l
        JOIN FETCH l.employee e
        JOIN FETCH e.user
        WHERE l.id = :id
        """)
    Optional<LeaveRequest> findByIdWithEmployee(Long id);
}
