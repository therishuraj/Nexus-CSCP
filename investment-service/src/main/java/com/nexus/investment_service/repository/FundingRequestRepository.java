package com.nexus.investment_service.repository;

import com.nexus.investment_service.model.FundingRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for CRUD operations on FundingRequest documents.
 */
@Repository
public interface FundingRequestRepository extends MongoRepository<FundingRequest, String> {
    // Fetch all funding requests created by a particular funder
    List<FundingRequest> findByFunderId(String funderId);
}