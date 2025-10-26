package com.practice.expense_service.controller;

import com.practice.expense_service.dto.ExpenseDto;
import com.practice.expense_service.service.ExpenseService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense/v1")
@RequiredArgsConstructor
public class ExpenseController
{

    private final ExpenseService expenseService;

    @GetMapping(path = "/getExpense")
    public ResponseEntity<List<ExpenseDto>> getExpense(@RequestHeader(value = "X-User-Id") @NonNull String userId){
         try{
            List<ExpenseDto> expenseDtoList = expenseService.getExpenses(userId);
            return new ResponseEntity<>(expenseDtoList, HttpStatus.OK);
         }catch(Exception ex){
             return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
         }
    }

    @PostMapping(path="/addExpense")
    public ResponseEntity<Boolean> addExpenses(@RequestBody ExpenseDto expenseDto){
        try{
            return new ResponseEntity<>(expenseService.createExpense(expenseDto), HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }

}
