package com.banking.jbs.service.impl;

import com.banking.jbs.dto.EmailDetails;

public interface EmailService {
	
	void sendEmailAlert(EmailDetails emailDetails);

	void sendEmailWithAttachment(EmailDetails emailDetails);
}
