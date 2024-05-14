package com.banking.jbs.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banking.jbs.config.JwtTokenProvider;
import com.banking.jbs.dto.AccountInfo;
import com.banking.jbs.dto.BankResponse;
import com.banking.jbs.dto.CreditDebitRequest;
import com.banking.jbs.dto.EmailDetails;
import com.banking.jbs.dto.EnquiryRequest;
import com.banking.jbs.dto.LoginDto;
import com.banking.jbs.dto.TransactionDto;
import com.banking.jbs.dto.TransferRequest;
import com.banking.jbs.dto.UserRequest;
import com.banking.jbs.entity.Role;
import com.banking.jbs.entity.User;
import com.banking.jbs.repository.UserRepository;
import com.banking.jbs.utils.AccountUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Override
	public BankResponse createAccount(UserRequest userRequest) {
		
		if(userRepository.existsByEmail(userRequest.getEmail())) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
					.responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
					.accountInfo(null)
					.build();
		}
		
		
		User newUser = User.builder()
				.firstName(userRequest.getFirstName())
				.lastName(userRequest.getLastName())
				.otherName(userRequest.getOtherName())
				.gender(userRequest.getGender())
				.address(userRequest.getAddress())
				.stateOfOrigin(userRequest.getStateOfOrigin())
				.accountNumber(AccountUtils.generateAccountNumber())
		        .accountBalance(BigDecimal.ZERO)
		        .email(userRequest.getEmail())
		        .password(passwordEncoder.encode(userRequest.getPassword()))
				.phoneNumber(userRequest.getPhoneNumber())
				.alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
				.status("ACTIVE")
				.role(Role.valueOf("ROLE_ADMIN"))
				.build();
		
		User savedUser = userRepository.save(newUser);
		
		EmailDetails emailDetails = EmailDetails.builder()
				.recipient(savedUser.getEmail())
				.subject("ACCOUNT CREATION")
				.messageBody("Congratulations! your Account has been successfully created.\nYour Account Details: \n" +
						"Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName()
						+ "\nAccount Number: "+ savedUser.getAccountNumber())
				.build();
		emailService.sendEmailAlert(emailDetails);
		return BankResponse.builder()
				.responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
				.responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
				.accountInfo(AccountInfo.builder()
						.accountBalance(savedUser.getAccountBalance())
						.accountNumber(savedUser.getAccountNumber())
						.accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
						.build())
				.build();
		
	}
	
	public BankResponse login(LoginDto loginDto) {
		Authentication authentication = null;
		authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
		
		EmailDetails loginAlert = EmailDetails.builder()
				.subject("You're logged in!")
				.recipient(loginDto.getEmail())
				.messageBody("You logged into your account. If you did not initiate this request, please contact your bank")
				.build();
		
		emailService.sendEmailAlert(loginAlert);
		return BankResponse.builder()
				.responseCode("Login Success")
				.responseMessage(jwtTokenProvider.generateToken(authentication))
				.build();
				        
		
	}

	@Override
	public BankResponse balanceEnquiry(EnquiryRequest request) {
		boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
		if(!isAccountExist) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
					.responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
					.accountInfo(null)
					.build();
		}
		
		User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
		return BankResponse.builder()
				.responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
				.responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
				.accountInfo(AccountInfo.builder()
						.accountBalance(foundUser.getAccountBalance())
						.accountNumber(foundUser.getAccountNumber())
						.accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
						.build())
				.build();
	}

	@Override
	public String nameEnquiry(EnquiryRequest request) {
		boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
		if(!isAccountExist) {
			return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
		}
		
		User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
		return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
	}

	@Override
	public BankResponse creditAccount(CreditDebitRequest request) {
		boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
		if(!isAccountExist) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
					.responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
					.accountInfo(null)
					.build();
		}
		
		User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
		userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
		userRepository.save(userToCredit);
		
		
		TransactionDto transactionDto = TransactionDto.builder()
				.accountNumber(userToCredit.getAccountNumber())
				.transactionType("CREDIT")
				.amount(request.getAmount())
				.build();
		
		transactionService.saveTransaction(transactionDto);
		
		
		return BankResponse.builder()
				.responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
				.responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
				.accountInfo(AccountInfo.builder()
						.accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
						.accountBalance(userToCredit.getAccountBalance())
						.accountNumber(request.getAccountNumber())
						.build())
				.build();
				
				
				
				
	}

	@Override
	public BankResponse debitAccount(CreditDebitRequest request) {
		boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
		if(!isAccountExist) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
					.responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
					.accountInfo(null)
					.build();
		}
		
		User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
		BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
		BigInteger debitAmount = request.getAmount().toBigInteger();
		if( availableBalance.intValue() < debitAmount.intValue()) {
			return BankResponse.builder()
					.responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
					.responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
					.accountInfo(null)
					.build();
		}
		
		
		
		else {
			userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
			userRepository.save(userToDebit);
			
			TransactionDto transactionDto = TransactionDto.builder()
					.accountNumber(userToDebit.getAccountNumber())
					.transactionType("Debit")
					.amount(request.getAmount())
					.build();
			
			transactionService.saveTransaction(transactionDto);
			
			
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_DEBITED_CODE)
					.responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
					.accountInfo(AccountInfo.builder()
							.accountNumber(request.getAccountNumber())
							.accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
							.accountBalance(userToDebit.getAccountBalance())
							.build())
					.build();
		}
	}

	@Override
	public BankResponse transfer(TransferRequest request) {
		//Boolean isSourceAccountExist = userRepository.existsByAccountNumber(request.getSourceAccountNumber());
		Boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
		if(!isDestinationAccountExist) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
					.responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
					.accountInfo(null)
					.build();
		}
		
		User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
		if(request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
			return BankResponse.builder()
					.responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
					.responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
					.accountInfo(null)
					.build();
		}
		
		sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
		String sourceUserName = sourceAccountUser.getFirstName() +" "+ sourceAccountUser.getLastName() +" "+ sourceAccountUser.getOtherName();
		userRepository.save(sourceAccountUser);
		EmailDetails debitAlert = EmailDetails.builder()
				.subject("DEBIT ALERT")
				.recipient(sourceAccountUser.getEmail())
				.messageBody("The sum of "+ request.getAmount() + " has been deducted from your account! Your current Balance is " + sourceAccountUser.getAccountBalance())
				.build();
		
		emailService.sendEmailAlert(debitAlert);
		
		User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
		destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
		//String receipentUserName = destinationAccountUser.getFirstName()+" "+ destinationAccountUser.getLastName() + " "+ destinationAccountUser.getOtherName();
		userRepository.save(destinationAccountUser);
		
		EmailDetails creditAlert = EmailDetails.builder()
				.subject("CREDIT ALERT")
				.recipient(destinationAccountUser.getEmail())
				.messageBody("The sum of "+ request.getAmount() + " has been credited from your account from "+ sourceUserName + "Your current Balance is  "+ destinationAccountUser.getAccountBalance())
				.build();
		
		emailService.sendEmailAlert(creditAlert);
		
		TransactionDto transactionDto = TransactionDto.builder()
				.accountNumber(destinationAccountUser.getAccountNumber())
				.transactionType("Debit")
				.amount(request.getAmount())
				.build();
		
		transactionService.saveTransaction(transactionDto);
		
		return BankResponse.builder()
				.responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
				.responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
				.build();
	}

}
