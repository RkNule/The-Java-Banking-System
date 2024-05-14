package com.banking.jbs.controller;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banking.jbs.entity.Transaction;
import com.banking.jbs.service.impl.BankStatement;
import com.itextpdf.text.DocumentException;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping("/bankStatement")
public class TransactionController {

	private BankStatement bankStatement;
	
	@GetMapping
	public List<Transaction> generateBankStatement(@RequestParam String accountNumber,
			             @RequestParam String startDate, @RequestParam String endDate) throws NullPointerException, FileNotFoundException, DocumentException{
		
		return bankStatement.generateStatement(accountNumber, startDate, endDate);
	}
	
	
}
