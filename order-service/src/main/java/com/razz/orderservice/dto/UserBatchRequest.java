package com.razz.orderservice.dto;

import java.util.List;

public record UserBatchRequest(List<String> userIds) {
}
