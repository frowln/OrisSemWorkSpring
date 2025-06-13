package kpfu.itis.kasimov.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final RestTemplate restTemplate;

    @Value("${elasticemail.api.key}")
    private String apiKey;

    @Value("${elasticemail.sender.email}")
    private String senderEmail;

    public void sendEmail(String to, String subject, String body) {
        String url = "https://api.elasticemail.com/v2/email/send";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("apikey", apiKey);
        params.add("from", senderEmail);
        params.add("to", to);
        params.add("subject", subject);
        params.add("bodyText", body);
        params.add("isTransactional", "true");

        try {
            String response = restTemplate.postForObject(url, params, String.class);
            log.info("Email sent. Response: {}", response);
        } catch (Exception e) {
            log.error("Failed to send email", e);
        }
    }
}
