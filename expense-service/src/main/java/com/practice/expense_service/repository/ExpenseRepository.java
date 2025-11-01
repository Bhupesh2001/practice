package com.practice.expense_service.repository;

import com.practice.expense_service.entities.Expense;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends CrudRepository<Expense, Long> {
    Optional<List<Expense>> findByUserName(String userName);
}