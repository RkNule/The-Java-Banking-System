package com.banking.jbs.service.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

//import java.io.FileNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.banking.jbs.dto.EmailDetails;
import com.banking.jbs.entity.Transaction;
import com.banking.jbs.entity.User;
import com.banking.jbs.repository.TransactionRepository;
import com.banking.jbs.repository.UserRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {
	
	private TransactionRepository transactionRepository;
	private UserRepository userRepository;
	private EmailService emailService;
	
	private static final String FILE = "C:\\Users\\DELL\\OneDrive\\Documents\\MyStatement.pdf";
	
	public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws NullPointerException, DocumentException, FileNotFoundException {
	    LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
	    LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

	    List<Transaction> transactionList = transactionRepository.findAll().stream()
	            .filter(transaction -> accountNumber.equals(transaction.getAccountNumber()))
	            .filter(transaction -> {
	                LocalDate createdAt = transaction.getCreatedAt();
	                return createdAt != null && createdAt.isEqual(start);
	            })
	            .filter(transaction -> {
	                LocalDate createdAt = transaction.getCreatedAt();
	                return createdAt != null && createdAt.isEqual(end);
	            })
	            .toList();
	    
	    User user = userRepository.findByAccountNumber(accountNumber);
	    String customerName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();
	    
	    Rectangle statementSize = new Rectangle(PageSize.A4);
		Document document = new Document(statementSize);
		log.info("setting size of document");
		FileOutputStream outputStream = new FileOutputStream(FILE);
		PdfWriter.getInstance(document, outputStream);
		document.open();
		
		PdfPTable bankInfoTable = new PdfPTable(1);
		PdfPCell bankName = new PdfPCell(new Phrase("The Java System Bank"));
		bankName.setBorder(0);
		bankName.setBackgroundColor(BaseColor.BLUE);
		bankName.setPadding(20f);
		
		PdfPCell bankAddress = new PdfPCell(new Phrase("100, OneDay, Mumbai, Maharashtra, India"));
		bankAddress.setBorder(0);
		bankInfoTable.addCell(bankName);
		bankInfoTable.addCell(bankAddress);
		
		PdfPTable statementInfo = new PdfPTable(2);
		PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date: " + startDate ));
		customerInfo.setBorder(0);
		PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
		statement.setBorder(0);
		PdfPCell stopDate = new PdfPCell(new Phrase("End Date: " + endDate));
		stopDate.setBorder(0);
		PdfPCell name = new PdfPCell(new Phrase("Customer Name: " + customerName));
		name.setBorder(0);
		PdfPCell space = new PdfPCell();
		space.setBorder(0);
		PdfPCell address = new PdfPCell(new Phrase("Customer Address: " + user.getAddress()));
		address.setBorder(0);
		
		PdfPTable transactionsTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("Date: "));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);
        PdfPCell transactionType = new PdfPCell(new Phrase("Transaction Type"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("Transaction Amount"));
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);
        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);
        
        transactionsTable.addCell(date);
        transactionsTable.addCell(transactionType);
        transactionsTable.addCell(transactionAmount);
        transactionsTable.addCell(status);
        
        
        transactionList.forEach(transaction ->{
        	transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
        	transactionsTable.addCell(new Phrase(transaction.getTransactionType()));
        	transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
        	transactionsTable.addCell(new Phrase(transaction.getStatus()));
        });
        
        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(endDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);
        
        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionsTable);
        
        document.close();
        
        EmailDetails emailDetails = EmailDetails.builder()
        		.recipient(user.getEmail())
        		.subject("STATEMENT OF ACCOUNT")
        		.messageBody("Kindly find your requested Account statement attached!")
        		.attachment(FILE)
        		.build();
        
        emailService.sendEmailWithAttachment(emailDetails);
        
	    return transactionList;
	}
	
	
}
