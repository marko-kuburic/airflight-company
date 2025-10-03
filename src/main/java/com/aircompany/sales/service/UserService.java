package com.aircompany.sales.service;

import com.aircompany.hr.model.Customer;
import com.aircompany.sales.dto.LoginRequest;
import com.aircompany.sales.dto.RegisterRequest;
import com.aircompany.sales.dto.UserProfileResponse;
import com.aircompany.sales.model.Loyalty;
import com.aircompany.sales.model.Reservation;
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
    private PasswordEncoder passwordEncoder;
    
    public Optional<UserProfileResponse> authenticateUser(LoginRequest loginRequest) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(loginRequest.getEmail());
        
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            
            // Check password
            if (passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
                return Optional.of(convertToUserProfileResponse(customer));
            }
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
    
    private UserProfileResponse convertToUserProfileResponse(Customer customer) {
        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(customer.getId());
        profile.setFirstName(customer.getFirstName());
        profile.setLastName(customer.getLastName());
        profile.setEmail(customer.getEmail());
        profile.setPhone(customer.getPhone());
        
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
    
    public Optional<UserProfileResponse> getUserProfile(Long userId) {
        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(userId);
        profile.setFirstName("Demo");
        profile.setLastName("User");
        profile.setEmail("demo@aircompany.com");
        profile.setPhone("+1234567890");
        
        UserProfileResponse.LoyaltyInfo loyaltyInfo = new UserProfileResponse.LoyaltyInfo();
        loyaltyInfo.setTier("BRONZE");
        loyaltyInfo.setPoints(0);
        profile.setLoyalty(loyaltyInfo);
        
        return Optional.of(profile);
    }
    
    public List<Reservation> getUserReservations(Long userId) {
        return List.of();
    }
}
