package com.banking.jbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.jbs.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

}
