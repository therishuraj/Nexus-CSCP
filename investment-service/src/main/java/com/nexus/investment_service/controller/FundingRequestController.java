package com.nexus.investment_service.controller;

import com.nexus.investment_service.dto.FundingRequestCreationDTO;
import com.nexus.investment_service.dto.FundingRequestUpdateDTO;
import com.nexus.investment_service.dto.FundingInvestmentDTO;
import com.nexus.investment_service.model.FundingRequest;
import com.nexus.investment_service.service.FundingRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/funding-requests")
@CrossOrigin("*")
public class FundingRequestController {

    private static final Logger log = LoggerFactory.getLogger(FundingRequestController.class);
    private final FundingRequestService fundingRequestService;

    public FundingRequestController(FundingRequestService fundingRequestService) {
        this.fundingRequestService = fundingRequestService;
    }

    @PostMapping
    public ResponseEntity<FundingRequest> createFundingRequest(
            @RequestHeader("X-User-Id") String funderId,
            @RequestBody FundingRequestCreationDTO requestDTO) {
        log.info("[HTTP] Create funding request funderId={} title={}", funderId, requestDTO.getTitle());
        FundingRequest newRequest = fundingRequestService.createFundingRequest(funderId, requestDTO);
        return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FundingRequest>> getAllFundingRequests() {
        log.info("[HTTP] Get all funding requests");
        return ResponseEntity.ok(fundingRequestService.getAllFundingRequests());
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<FundingRequest> getFundingRequestById(@PathVariable String requestId) {
        log.info("[HTTP] Get funding request id={}", requestId);
        FundingRequest request = fundingRequestService.getFundingRequestById(requestId);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<FundingRequest> updateFundingRequest(
            @PathVariable String requestId,
            @RequestHeader("X-User-Id") String funderId,
            @RequestBody FundingRequestUpdateDTO updateDTO) {
        log.info("[HTTP] Update funding request id={} funderId={} title={} deadline={}", requestId, funderId, updateDTO.getTitle(), updateDTO.getDeadline());
        FundingRequest updatedRequest = fundingRequestService.updateFundingRequest(requestId, funderId, updateDTO);
        return ResponseEntity.ok(updatedRequest);
    }

    @PostMapping("/{requestId}/investment")
    public ResponseEntity<FundingRequest> investInFundingRequest(
            @PathVariable String requestId,
            @RequestHeader("X-User-Id") String investorId,
            @RequestBody FundingInvestmentDTO investmentDTO) {
        investmentDTO.setInvestorId(investorId);
        log.info("[HTTP] Invest in funding request id={} investorId={} walletAdjustment={}", requestId, investorId, investmentDTO.getWalletAdjustment());
        FundingRequest updated = fundingRequestService.investInFundingRequest(requestId, investmentDTO);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{requestId}/distribute-returns")
    public ResponseEntity<FundingRequest> distributeReturns(@PathVariable String requestId) {
        log.info("[HTTP] Distribute returns funding request id={}", requestId);
        FundingRequest updated = fundingRequestService.distributeReturns(requestId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<FundingRequest>> getMyFundingRequests(@RequestHeader("X-User-Id") String funderId) {
        log.info("[HTTP] Get funding requests by funderId={}", funderId);
        List<FundingRequest> requests = fundingRequestService.getFundingRequestsByFunderId(funderId);
        return ResponseEntity.ok(requests);
    }
}
