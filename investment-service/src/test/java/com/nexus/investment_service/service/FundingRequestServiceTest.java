package com.nexus.investment_service.service;

import com.nexus.investment_service.dto.FundingInvestmentDTO;
import com.nexus.investment_service.dto.FundingRequestCreationDTO;
import com.nexus.investment_service.dto.FundingRequestUpdateDTO;
import com.nexus.investment_service.model.FundingRequest;
import com.nexus.investment_service.repository.FundingRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.nexus.investment_service.utils.Constants.STATUS_FUNDED;
import static com.nexus.investment_service.utils.Constants.STATUS_OPEN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundingRequestServiceTest {

    @Mock
    FundingRequestRepository fundingRequestRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    WebClient webClient;

    @InjectMocks
    FundingRequestService fundingRequestService;

    @BeforeEach
    void setUpWebClientMocks() {
        when(webClient.put().uri(anyString()).bodyValue(any()).retrieve().toBodilessEntity())
                .thenReturn(Mono.just(ResponseEntity.ok().build()));
        // Clear the invocation of put() done during stubbing so verification counts start fresh
        clearInvocations(webClient);
    }

    @Test
    @DisplayName("createFundingRequest should initialize new request with expected fields")
    void testCreateFundingRequest() {
        FundingRequestCreationDTO dto = new FundingRequestCreationDTO();
        dto.setTitle("Test Project");
        dto.setRequiredAmount(1000.0);
        dto.setDeadline(LocalDateTime.now().plusDays(10));
        dto.setCommittedReturnAmount(200.0);
        dto.setDescription("Acquire inventory");

        when(fundingRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        FundingRequest created = fundingRequestService.createFundingRequest("funder-1", dto);

        assertEquals("Test Project", created.getTitle());
        assertEquals(1000.0, created.getRequiredAmount());
        assertEquals(0.0, created.getCurrentFunded());
        assertEquals("funder-1", created.getFunderId());
        assertEquals(200.0, created.getCommittedReturnAmount());
        assertEquals("Acquire inventory", created.getDescription());
        assertFalse(created.isReturnDistributed());
        assertEquals(STATUS_OPEN, created.getStatus());
    }

    @Test
    @DisplayName("investInFundingRequest should record investment and not fund request if threshold not reached")
    void testInvestPartialFunding() {
        FundingRequest request = baseRequest("req-1", 1000.0, 0.0, STATUS_OPEN, 300.0);
        when(fundingRequestRepository.findById("req-1")).thenReturn(Optional.of(request));
        when(fundingRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        FundingInvestmentDTO investmentDTO = new FundingInvestmentDTO();
        investmentDTO.setInvestorId("investor-1");
        investmentDTO.setWalletAdjustment(-400.0); // investing 400

        FundingRequest updated = fundingRequestService.investInFundingRequest("req-1", investmentDTO);

        assertEquals(400.0, updated.getCurrentFunded());
        assertEquals(STATUS_OPEN, updated.getStatus());
        assertEquals(400.0, updated.getInvestorAmounts().get("investor-1"));
        verify(webClient, times(1)).put();
    }

    @Test
    @DisplayName("investInFundingRequest should mark FUNDED and credit funder when fully funded")
    void testInvestTriggersFundedAndFunderCredit() {
        FundingRequest request = baseRequest("req-2", 500.0, 400.0, STATUS_OPEN, 100.0);
        when(fundingRequestRepository.findById("req-2")).thenReturn(Optional.of(request));
        when(fundingRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        FundingInvestmentDTO dto = new FundingInvestmentDTO();
        dto.setInvestorId("investor-2");
        dto.setWalletAdjustment(-100.0); // reaches 500

        FundingRequest updated = fundingRequestService.investInFundingRequest("req-2", dto);

        assertEquals(500.0, updated.getCurrentFunded());
        assertEquals(STATUS_FUNDED, updated.getStatus());
        assertEquals(100.0, updated.getInvestorAmounts().get("investor-2"));
        // investor deduction + funder credit
        verify(webClient, times(2)).put();
    }

    @Test
    @DisplayName("distributeReturns should debit funder and credit investors proportionally")
    void testDistributeReturnsSuccess() {
        FundingRequest request = baseRequest("req-3", 600.0, 600.0, STATUS_FUNDED, 120.0);
        Map<String, Double> investors = new HashMap<>();
        investors.put("invA", 300.0);
        investors.put("invB", 200.0);
        investors.put("invC", 100.0);
        request.setInvestorAmounts(investors);
        when(fundingRequestRepository.findById("req-3")).thenReturn(Optional.of(request));
        when(fundingRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        FundingRequest distributed = fundingRequestService.distributeReturns("req-3");

        assertTrue(distributed.isReturnDistributed());
        // funder debit + 3 investor credits
        verify(webClient, times(4)).put();
    }

    @Test
    @DisplayName("distributeReturns should fail if not FUNDED")
    void testDistributeReturnsNotFunded() {
        FundingRequest request = baseRequest("req-4", 800.0, 500.0, STATUS_OPEN, 200.0);
        request.setInvestorAmounts(Map.of("inv", 500.0));
        when(fundingRequestRepository.findById("req-4")).thenReturn(Optional.of(request));
        assertThrows(ResponseStatusException.class, () -> fundingRequestService.distributeReturns("req-4"));
    }

    @Test
    @DisplayName("updateFundingRequest modifies editable fields when OPEN")
    void testUpdateFundingRequest() {
        FundingRequest request = baseRequest("req-5", 900.0, 0.0, STATUS_OPEN, 180.0);
        when(fundingRequestRepository.findById("req-5")).thenReturn(Optional.of(request));
        when(fundingRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        FundingRequestUpdateDTO updateDTO = new FundingRequestUpdateDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated description");
        updateDTO.setCommittedReturnAmount(200.0);

        FundingRequest updated = fundingRequestService.updateFundingRequest("req-5", "funder-req-5", updateDTO);

        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(200.0, updated.getCommittedReturnAmount());
    }

    private FundingRequest baseRequest(String id, double required, double current, String status, double committedReturn) {
        FundingRequest fr = new FundingRequest();
        fr.setCommittedReturnAmount(committedReturn);
        fr.setId(id);
        fr.setTitle("Base");
        fr.setRequiredAmount(required);
        fr.setCurrentFunded(current);
        fr.setStatus(status);
        fr.setFunderId("funder-" + id);
        fr.setCreatedAt(LocalDateTime.now());
        fr.setDeadline(LocalDateTime.now().plusDays(5));
        fr.setInvestorAmounts(new HashMap<>());
        fr.setDescription("Desc");
        return fr;
    }
}
