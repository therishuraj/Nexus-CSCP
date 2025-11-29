package com.nexus.investment_service.utils;

public class Constants {

    // User service URL is now configured via application.properties
    // using the property: user.service.url
    // Default: http://localhost:3000 (for local development)
    // Docker: http://user-service:3000 (via environment variable USER_SERVICE_URL)

    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_FUNDED = "FUNDED";
    public static final String STATUS_CLOSED = "CLOSED";


}