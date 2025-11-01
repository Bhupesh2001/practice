package com.practice.expense_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.expense_service.dto.ExpenseDto;
import com.practice.expense_service.entities.Expense;
import com.practice.expense_service.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ObjectMapper objectMapper;

    public boolean createExpense(ExpenseDto expenseDto) {
        log.info("ExpenseService: Creating expense for userId: {}", expenseDto.getUserName());
        setCurrency(expenseDto);
        try {
            Expense convertedValue = objectMapper.convertValue(expenseDto, Expense.class);
            log.info("ExpenseService: Converted ExpenseDto to Expense entity: {}", convertedValue);
            expenseRepository.save(convertedValue);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public List<ExpenseDto> getExpenses(String userName) {
        List<Expense> expenseOpt = expenseRepository.findByUserName(userName).orElse(new ArrayList<>());
        return objectMapper.convertValue(expenseOpt, new TypeReference<>() {});
    }

    private void setCurrency(ExpenseDto expenseDto) {
        if (isNull(expenseDto.getCurrency())) {
            expenseDto.setCurrency("inr");
        }
    }
}
