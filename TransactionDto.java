package com.banking.jbs.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TransactionDto {

	private String transactionType;
	private BigDecimal amount;
	private String accountNumber;
	private String status;
}
