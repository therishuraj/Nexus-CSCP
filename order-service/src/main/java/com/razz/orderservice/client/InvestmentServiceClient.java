package com.razz.orderservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.razz.orderservice.dto.FundingRequestResponse;

@Component
public class InvestmentServiceClient {
    private static final Logger log = LoggerFactory.getLogger(InvestmentServiceClient.class);
    
    private final WebClient webClient;
    private final String investmentServiceUrl;
    
    public InvestmentServiceClient(
            WebClient webClient,
            @Value("${investment.service.url:http://localhost:3004}") String investmentServiceUrl) {
        this.webClient = webClient;
        this.investmentServiceUrl = investmentServiceUrl;
    }
    
    public FundingRequestResponse getFundingRequestById(String requestId) {
        String url = investmentServiceUrl + "/api/v1/funding-requests/" + requestId;
        
        log.info("Fetching funding request from investment-service - RequestId: {}, URL: {}", requestId, url);
        
        try {
            FundingRequestResponse response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(FundingRequestResponse.class)
                    .block();
            
            log.info("Funding request fetched successfully - RequestId: {}, Status: {}", 
                    requestId, response != null ? response.status() : "null");
            
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch funding request - RequestId: {}, Error: {}", requestId, e.getMessage());
            throw new RuntimeException("Failed to fetch funding request: " + e.getMessage(), e);
        }
    }
}
