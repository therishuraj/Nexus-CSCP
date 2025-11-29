package com.nexus.user_service.repository;

import com.nexus.user_service.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * Find user by email
     * @param email the email to search for
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by email
     * @param email the email to check
     * @return boolean
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all users by role
     * @param role the role to search for
     * @return List<User>
     */
    @Query("{ 'roles': ?0 }")
    List<User> findByRole(String role);
    
    /**
     * Find users by name containing (case insensitive)
     * @param name the name pattern to search for
     * @return List<User>
     */
    List<User> findByNameContainingIgnoreCase(String name);
    
    /**
     * Delete user by email
     * @param email the email of user to delete
     */
    void deleteByEmail(String email);
}
