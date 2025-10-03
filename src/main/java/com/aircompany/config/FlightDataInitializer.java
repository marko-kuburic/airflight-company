package com.aircompany.config;

import com.aircompany.flight.model.*;
import com.aircompany.sales.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class FlightDataInitializer implements CommandLineRunner {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private final Random random = new Random();
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if data already exists - check multiple entities
        Long flightCount = entityManager.createQuery("SELECT COUNT(f) FROM Flight f", Long.class).getSingleResult();
        Long countryCount = entityManager.createQuery("SELECT COUNT(c) FROM Country c", Long.class).getSingleResult();
        Long fareCount = entityManager.createQuery("SELECT COUNT(f) FROM Fare f", Long.class).getSingleResult();
        
        if (flightCount > 0 || countryCount > 0 || fareCount > 0) {
            System.out.println("Data already exists (Flights: " + flightCount + ", Countries: " + countryCount + ", Fares: " + fareCount + "). Skipping initialization.");
            return;
        }
        
        System.out.println("🚀 Starting flight data initialization according to specification...");
        
        // Initialize all data
        initializeCountries();
        initializeAirports();
        initializeAircraft();
        initializeCabinClasses();
        initializeRoutes();
        initializeFlights();
        
        System.out.println("✅ Flight data initialization completed successfully!");
    }
    
    private void initializeCountries() {
        System.out.println("📍 Creating countries...");
        
        String[][] countryData = {
            {"Serbia", "SRB", "Belgrade"},
            {"Germany", "DEU", "Berlin"},
            {"France", "FRA", "Paris"},
            {"Italy", "ITA", "Rome"},
            {"Spain", "ESP", "Madrid"},
            {"United Kingdom", "GBR", "London"},
            {"Netherlands", "NLD", "Amsterdam"},
            {"Greece", "GRC", "Athens"},
            {"Turkey", "TUR", "Ankara"},
            {"Croatia", "HRV", "Zagreb"}
        };
        
        for (String[] data : countryData) {
            Country country = new Country();
            country.setName(data[0]);
            country.setCode(data[1]);
            // Note: Country model might not have setCapital method
            entityManager.persist(country);
        }
        
        entityManager.flush();
        System.out.println("✓ Created " + countryData.length + " countries");
    }
    
    private void initializeAirports() {
        System.out.println("✈️ Creating airports...");
        
        // Get countries for reference
        List<Country> countries = entityManager.createQuery("SELECT c FROM Country c", Country.class).getResultList();
        
        String[][] airportData = {
            {"Nikola Tesla Airport", "BEG", "Belgrade", "Serbia", "44.8184", "20.3091", "Europe/Belgrade"},
            {"Frankfurt Airport", "FRA", "Frankfurt", "Germany", "50.0264", "8.5431", "Europe/Berlin"},
            {"Charles de Gaulle Airport", "CDG", "Paris", "France", "49.0097", "2.5479", "Europe/Paris"},
            {"Leonardo da Vinci Airport", "FCO", "Rome", "Italy", "41.7999", "12.2462", "Europe/Rome"},
            {"Madrid-Barajas Airport", "MAD", "Madrid", "Spain", "40.4720", "3.5626", "Europe/Madrid"},
            {"Heathrow Airport", "LHR", "London", "United Kingdom", "51.4700", "0.4543", "Europe/London"},
            {"Amsterdam Airport Schiphol", "AMS", "Amsterdam", "Netherlands", "52.3105", "4.7683", "Europe/Amsterdam"},
            {"Athens International Airport", "ATH", "Athens", "Greece", "37.9364", "23.9445", "Europe/Athens"},
            {"Istanbul Airport", "IST", "Istanbul", "Turkey", "41.2619", "28.7276", "Europe/Istanbul"},
            {"Zagreb Airport", "ZAG", "Zagreb", "Croatia", "45.7429", "16.0688", "Europe/Zagreb"}
        };
        
        for (String[] data : airportData) {
            Airport airport = new Airport();
            airport.setName(data[0]);
            airport.setIataCode(data[1]);
            airport.setCity(data[2]);
            airport.setLatitude(new BigDecimal(data[4]));
            airport.setLongitude(new BigDecimal(data[5]));
            airport.setTimezone(data[6]);
            
            // Find country by name
            for (Country country : countries) {
                if (country.getName().equals(data[3])) {
                    airport.setCountry(country);
                    break;
                }
            }
            
            entityManager.persist(airport);
        }
        
        entityManager.flush();
        System.out.println("✓ Created " + airportData.length + " airports");
    }
    
    private void initializeAircraft() {
        System.out.println("🛩️ Creating aircraft...");
        
        String[][] aircraftData = {
            {"Airbus A320", "320", "150"},
            {"Airbus A321", "321", "180"},
            {"Airbus A330", "330", "250"},
            {"Boeing 737", "737", "160"},
            {"Boeing 777", "777", "300"},
            {"Boeing 787", "787", "250"}
        };
        
        for (int i = 0; i < aircraftData.length; i++) {
            Aircraft aircraft = new Aircraft();
            aircraft.setModel(aircraftData[i][0]);
            aircraft.setCapacity(Integer.parseInt(aircraftData[i][2]));
            aircraft.setStatus(Aircraft.AircraftStatus.ACTIVE);
            entityManager.persist(aircraft);
        }
        
        entityManager.flush();
        System.out.println("✓ Created " + aircraftData.length + " aircraft");
    }
    
    private void initializeCabinClasses() {
        System.out.println("🎫 Creating cabin classes...");
        
        // Create shared cabin classes according to specification
        String[][] cabinData = {
            {"ECONOMY", "Economy class seats with standard service", "200"},
            {"BUSINESS", "Business class seats with premium service", "800"},
            {"FIRST", "First class seats with luxury service", "1500"}
        };
        
        for (String[] data : cabinData) {
            CabinClass cabinClass = new CabinClass();
            cabinClass.setName(data[0]);
            cabinClass.setDescription(data[1]);
            cabinClass.setBasePrice(new BigDecimal(data[2]));
            entityManager.persist(cabinClass);
        }
        
        entityManager.flush();
        System.out.println("✓ Created 3 cabin classes (Economy, Business, First)");
    }
    
    private void initializeRoutes() {
        System.out.println("🗺️ Creating routes...");
        
        // Get all airports
        List<Airport> airports = entityManager.createQuery("SELECT a FROM Airport a", Airport.class).getResultList();
        Airport belgradeAirport = airports.stream()
            .filter(a -> "BEG".equals(a.getIataCode()))
            .findFirst()
            .orElse(null);
        
        if (belgradeAirport == null) {
            System.err.println("Belgrade airport not found!");
            return;
        }
        
        // Create routes from Belgrade to other airports
        for (Airport destAirport : airports) {
            if (!destAirport.equals(belgradeAirport)) {
                Route route = new Route();
                route.setName(belgradeAirport.getCity() + " to " + destAirport.getCity());
                
                // Calculate distance using Haversine formula
                double distance = calculateDistance(
                    belgradeAirport.getLatitude().doubleValue(),
                    belgradeAirport.getLongitude().doubleValue(),
                    destAirport.getLatitude().doubleValue(),
                    destAirport.getLongitude().doubleValue()
                );
                route.setTotalDistance(new BigDecimal(String.format("%.2f", distance)));
                
                entityManager.persist(route);
                
                // Create segment for this route
                Segment segment = new Segment();
                segment.setRoute(route);
                segment.setOriginAirport(belgradeAirport);
                segment.setDestinationAirport(destAirport);
                segment.setDistance(new BigDecimal(String.format("%.2f", distance)));
                entityManager.persist(segment);
            }
        }
        
        entityManager.flush();
        System.out.println("✓ Created routes from Belgrade to other destinations");
    }
    
    private void initializeFlights() {
        System.out.println("🛫 Creating flights with dynamic pricing offers...");
        
        // Get data for flight creation
        List<Route> routes = entityManager.createQuery("SELECT r FROM Route r", Route.class).getResultList();
        List<Aircraft> aircraft = entityManager.createQuery("SELECT a FROM Aircraft a", Aircraft.class).getResultList();
        
        // Create 50 flights (reasonable number for testing)
        for (int i = 1; i <= 50; i++) {
            Flight flight = new Flight();
            
            // Generate flight number with JU prefix (Air Serbia)
            flight.setFlightNumber("JU" + String.format("%04d", i));
            
            // Random route and aircraft
            Route route = routes.get(random.nextInt(routes.size()));
            Aircraft selectedAircraft = aircraft.get(random.nextInt(aircraft.size()));
            
            // Random departure time (next 30 days)
            LocalDateTime baseTime = LocalDateTime.now().plusDays(random.nextInt(30));
            LocalDateTime depTime = baseTime.withHour(6 + random.nextInt(16)).withMinute(0).withSecond(0);
            
            flight.setDepTime(depTime);
            flight.setArrTime(depTime.plusHours(1 + random.nextInt(6))); // 1-7 hour flights
            flight.setStatus(Flight.FlightStatus.SCHEDULED);
            flight.setRoute(route);
            flight.setAircraft(selectedAircraft);
            
            entityManager.persist(flight);
            
            // Create dynamic pricing offer for this flight according to specification
            createDynamicPricingOffer(flight);
        }
        
        entityManager.flush();
        System.out.println("✓ Created 50 flights with dynamic pricing offers");
    }
    
    private void createDynamicPricingOffer(Flight flight) {
        // According to specification: "Pri svakoj pretrazi letova sistem generiše ponude sa aktuelnim cenama"
        // Create one offer per flight with multiple fares for different cabin classes
        
        Offer offer = new Offer();
        offer.setTitle("Flight " + flight.getFlightNumber() + " - Dynamic Pricing");
        offer.setDescription("Dynamic pricing offer for all cabin classes on flight " + flight.getFlightNumber());
        offer.setDiscountPercentage(new BigDecimal("0.00")); // Will be calculated dynamically
        offer.setStartDate(LocalDateTime.now());
        
        // Specification: "Svaka ponuda ima ograničen rok važenja (npr. 10 minuta)"
        offer.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        offer.setEndDate(flight.getDepTime().minusHours(1)); // Valid until 1 hour before departure
        offer.setFlight(flight);
        offer.setBasePrice(new BigDecimal("200")); // Base economy price
        entityManager.persist(offer);
        
        // Get cabin classes
        List<CabinClass> cabinClasses = entityManager.createQuery("SELECT c FROM CabinClass c ORDER BY c.basePrice", CabinClass.class).getResultList();
        
        // Create fares for each cabin class with dynamic pricing
        List<Fare> fares = new ArrayList<>();
        
        for (CabinClass cabinClass : cabinClasses) {
            Fare fare = new Fare();
            fare.setCabinClass(cabinClass);
            fare.setOffer(offer);
            
            // Apply dynamic pricing based on specification factors
            BigDecimal dynamicPrice = calculateDynamicPrice(cabinClass.getBasePrice(), flight);
            fare.setPrice(dynamicPrice);
            
            entityManager.persist(fare);
            fares.add(fare);
        }
        
        // Set fares to offer
        offer.setFares(fares);
    }
    
    private BigDecimal calculateDynamicPrice(BigDecimal basePrice, Flight flight) {
        // Implement dynamic pricing according to specification 3.4.1
        double multiplier = 1.0;
        
        // Factor 1: Demand simulation (random for now)
        // "Ukoliko je prodaja usporena, sistem može ponuditi niže cene"
        // "Ako je potražnja velika, cena raste"
        double demandFactor = 0.8 + (random.nextDouble() * 0.6); // 0.8 to 1.4
        
        // Factor 2: Season effect
        // "U vreme praznika, školskih raspusta cene se automatski uvećavaju"
        LocalDateTime depTime = flight.getDepTime();
        double seasonFactor = 1.0;
        
        // Weekend effect: "karте петком, суботом и недељом скупље"
        int dayOfWeek = depTime.getDayOfWeek().getValue();
        if (dayOfWeek >= 5) { // Friday, Saturday, Sunday
            seasonFactor = 1.2;
        }
        
        // Factor 3: Time until departure
        // "last-minute popust" ili "rana kupovina popust"
        long hoursUntilDeparture = java.time.Duration.between(LocalDateTime.now(), depTime).toHours();
        double timeFactor = 1.0;
        
        if (hoursUntilDeparture < 24) {
            // Last minute - može biti jeftiniji ili skuplji
            timeFactor = random.nextBoolean() ? 0.7 : 1.5;
        } else if (hoursUntilDeparture > 720) { // 30 days
            // Early booking discount
            timeFactor = 0.9;
        }
        
        multiplier = demandFactor * seasonFactor * timeFactor;
        
        // Apply multiplier to base price
        BigDecimal finalPrice = basePrice.multiply(new BigDecimal(multiplier));
        
        // Round to nearest 10
        return finalPrice.setScale(0, java.math.RoundingMode.HALF_UP)
                       .divide(new BigDecimal("10"))
                       .setScale(0, java.math.RoundingMode.HALF_UP)
                       .multiply(new BigDecimal("10"));
    }
    
    // Simple distance calculation using Haversine formula
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
}