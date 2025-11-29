package com.payment.paymentservice.repository;

import com.payment.paymentservice.model.Payout;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PayoutRepository extends MongoRepository<Payout, String> {
}