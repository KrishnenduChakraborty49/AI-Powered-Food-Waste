package com.foodlink.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.phone-number:}")
    private String twilioPhoneNumber;

    private boolean isConfigured = false;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isEmpty() && authToken != null && !authToken.isEmpty()) {
            // Twilio.init(accountSid, authToken); // Uncomment when using Twilio SDK
            isConfigured = true;
            log.info("SMS Service configured successfully with Twilio account {}", accountSid);
        } else {
            log.warn("Twilio credentials not found. SMS Service will run in MOCK mode.");
        }
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        if (!isConfigured) {
            log.warn("Mocking SMS to {}: {}", toPhoneNumber, messageBody);
            return;
        }

        try {
            // Uncomment the following lines when the Twilio SDK dependency is added
            /*
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(toPhoneNumber),
                    new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                    messageBody)
                .create();
            log.info("SMS sent successfully with SID: {}", message.getSid());
            */
            
            // For now, even if configured, we just log to avoid compilation errors without the SDK
            log.info("[REAL SMS ATTEMPT - SDK OMITTED] To: {}, Body: {}", toPhoneNumber, messageBody);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}", toPhoneNumber, e);
        }
    }
}
