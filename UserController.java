package com.banking.jbs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.jbs.dto.BankResponse;
import com.banking.jbs.dto.CreditDebitRequest;
import com.banking.jbs.dto.EnquiryRequest;
import com.banking.jbs.dto.LoginDto;
import com.banking.jbs.dto.TransferRequest;
import com.banking.jbs.dto.UserRequest;
import com.banking.jbs.service.impl.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs")
public class UserController {
	
	@Autowired
	UserService userService;
	@Operation(
			summary = "Create New User Account",
			description = "Creating new user and assigning an account ID"
			)
	@ApiResponse(
			responseCode = "201",
			description = "HTTP Status 201 CREATED"
			)
	@PostMapping
	public BankResponse createAccount(@RequestBody UserRequest userRequest) {
		return userService.createAccount(userRequest);
	}
	
	@PostMapping("/login")
	public BankResponse login(@RequestBody LoginDto loginDto) {
		return userService.login(loginDto);
	}
	
	@Operation(
			summary = "Balance Enquiry",
			description = "Given an Account Number, check how much the user has "
			)
	@ApiResponse(
			responseCode = "200",
			description = "HTTP Status 200 SUCCESS"
			)
	@GetMapping("balanceEnquiry")
	public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request) {
		return userService.balanceEnquiry(request);
	}
	
	@GetMapping("nameEnquiry")
	public String nameEnquiry(@RequestBody EnquiryRequest request) {
		return userService.nameEnquiry(request);
	}
	
	@PostMapping("credit")
	public BankResponse creditAccount(@RequestBody CreditDebitRequest request) {
		return userService.creditAccount(request);
	}
	
	@PostMapping("debit")
	public BankResponse debitAccount(@RequestBody CreditDebitRequest request) {
		return userService.debitAccount(request);
	}
	
	@PostMapping("transfer")
	public BankResponse transfer(@RequestBody TransferRequest request) {
		return userService.transfer(request);
	}

}
