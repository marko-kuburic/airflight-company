package com.aircompany.hr.service;

import com.aircompany.hr.model.Customer;
import com.aircompany.hr.model.User;
import com.aircompany.hr.repository.UserRepository;
import com.aircompany.sales.dto.*;
import com.aircompany.sales.model.Loyalty;
import com.aircompany.sales.repository.CustomerRepository;
import com.aircompany.sales.repository.LoyaltyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private LoyaltyRepository loyaltyRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Optional<UserProfileResponse> authenticateUser(LoginRequest loginRequest) {
        System.out.println("DEBUG: UserService.authenticateUser called with email: " + loginRequest.getEmail());
        
        // Get all users and find by email
        List<User> users = userRepository.findAllByEmail(loginRequest.getEmail());
        
        System.out.println("DEBUG: UserRepository.findAllByEmail result size: " + users.size());
        
        if (!users.isEmpty()) {
            User user = users.get(0);
            System.out.println("DEBUG: Found user with type: " + user.getUserType());
            
            // Check password
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                System.out.println("DEBUG: Password matches, returning user profile");
                return Optional.of(convertToUserProfileResponse(user));
            } else {
                System.out.println("DEBUG: Password does not match");
            }
        } else {
            System.out.println("DEBUG: No user found with email: " + loginRequest.getEmail());
        }
        
        return Optional.empty();
    }
    
    public UserProfileResponse registerUser(RegisterRequest registerRequest) {
        // Check if user already exists
        if (customerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }
        
        // Create new customer
        Customer customer = new Customer();
        customer.setFirstName(registerRequest.getFirstName());
        customer.setLastName(registerRequest.getLastName());
        customer.setEmail(registerRequest.getEmail());
        customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        customer.setPhone(registerRequest.getPhone());
        
        // Save customer
        customer = customerRepository.save(customer);
        
        // Create loyalty account
        Loyalty loyalty = new Loyalty();
        loyalty.setCustomer(customer);
        loyalty.setTier(Loyalty.LoyaltyTier.BRONZE);
        loyalty.setPoints(0);
        loyaltyRepository.save(loyalty);
        
        return convertToUserProfileResponse(customer);
    }
    
    private UserProfileResponse convertToUserProfileResponse(User user) {
        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(user.getId());
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setEmail(user.getEmail());
        profile.setUserType(user.getUserType());
        
        // Only set customer-specific fields if it's a Customer
        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            profile.setPhone(customer.getPhone());
            profile.setCreatedAt(customer.getCreatedAt());
            
            // Get loyalty info
            Optional<Loyalty> loyaltyOpt = loyaltyRepository.findByCustomer(customer);
            if (loyaltyOpt.isPresent()) {
                Loyalty loyalty = loyaltyOpt.get();
                UserProfileResponse.LoyaltyInfo loyaltyInfo = new UserProfileResponse.LoyaltyInfo();
                loyaltyInfo.setTier(loyalty.getTier().toString());
                loyaltyInfo.setPoints(loyalty.getPoints());
                profile.setLoyalty(loyaltyInfo);
            } else {
                UserProfileResponse.LoyaltyInfo loyaltyInfo = new UserProfileResponse.LoyaltyInfo();
                loyaltyInfo.setTier("BRONZE");
                loyaltyInfo.setPoints(0);
                profile.setLoyalty(loyaltyInfo);
            }
        }
        // For non-customer users (FlightDispatcher, Admin, etc.), don't set loyalty field at all
        
        return profile;
    }
    
    private UserProfileResponse convertToUserProfileResponse(Customer customer) {
        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(customer.getId());
        profile.setFirstName(customer.getFirstName());
        profile.setLastName(customer.getLastName());
        profile.setEmail(customer.getEmail());
        profile.setPhone(customer.getPhone());
        profile.setDateOfBirth(customer.getDateOfBirth());
        profile.setPreferredLanguage(customer.getPreferredLanguage());
        
        // Get loyalty info
        Optional<Loyalty> loyaltyOpt = loyaltyRepository.findByCustomer(customer);
        if (loyaltyOpt.isPresent()) {
            Loyalty loyalty = loyaltyOpt.get();
            UserProfileResponse.LoyaltyInfo loyaltyInfo = new UserProfileResponse.LoyaltyInfo();
            loyaltyInfo.setTier(loyalty.getTier().toString());
            loyaltyInfo.setPoints(loyalty.getPoints());
            profile.setLoyalty(loyaltyInfo);
        } else {
            UserProfileResponse.LoyaltyInfo loyaltyInfo = new UserProfileResponse.LoyaltyInfo();
            loyaltyInfo.setTier("BRONZE");
            loyaltyInfo.setPoints(0);
            profile.setLoyalty(loyaltyInfo);
        }
        
        return profile;
    }
}