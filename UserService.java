package com.banking.jbs.service.impl;

import com.banking.jbs.dto.BankResponse;
import com.banking.jbs.dto.CreditDebitRequest;
import com.banking.jbs.dto.EnquiryRequest;
import com.banking.jbs.dto.LoginDto;
import com.banking.jbs.dto.TransferRequest;
import com.banking.jbs.dto.UserRequest;

public interface UserService {
	
	BankResponse createAccount(UserRequest userRequest);
	
	BankResponse balanceEnquiry(EnquiryRequest request);
	String nameEnquiry(EnquiryRequest request);
	
	BankResponse creditAccount(CreditDebitRequest request);
	
	BankResponse debitAccount(CreditDebitRequest request);
	
	BankResponse transfer(TransferRequest request);
	
	BankResponse login(LoginDto loginDto);

}
