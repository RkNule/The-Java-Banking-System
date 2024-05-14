package com.banking.jbs.service.impl;

import com.banking.jbs.dto.TransactionDto;
//import com.banking.jbs.entity.Transaction;

public interface TransactionService {
	
	void saveTransaction(TransactionDto transactionDto);

}
