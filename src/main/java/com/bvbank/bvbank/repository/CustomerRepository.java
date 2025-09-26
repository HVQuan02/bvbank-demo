package com.bvbank.bvbank.repository;

import com.bvbank.bvbank.dto.CustomerLocationCountDto;
import com.bvbank.bvbank.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    boolean existsByPhone(String phone);

    @Query("SELECT new com.bvbank.bvbank.dto.CustomerLocationCountDto(c.address, COUNT(c)) " +
           "FROM Customer c GROUP BY c.address")
    List<CustomerLocationCountDto> countCustomersByLocation();
}
