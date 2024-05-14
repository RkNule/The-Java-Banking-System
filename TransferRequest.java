package com.banking.jbs.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
	private String sourceAccountNumber;
	private String destinationAccountNumber;
	private BigDecimal amount;

}
