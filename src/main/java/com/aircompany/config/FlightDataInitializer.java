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
        Long routeCount = entityManager.createQuery("SELECT COUNT(r) FROM Route r", Long.class).getSingleResult();
        
        // Force regeneration to implement new city distribution if we have old Belgrade-centric data
        boolean needsRegeneration = false;
        if (routeCount > 0 && routeCount < 50) { // Old system had ~9 routes, new should have 90 (10*9)
            System.out.println("🔄 Detected old Belgrade-centric route structure. Regenerating with all-city distribution...");
            needsRegeneration = true;
        }
        
        if ((flightCount > 0 || countryCount > 0 || fareCount > 0) && !needsRegeneration) {
            System.out.println("Data already exists (Flights: " + flightCount + ", Countries: " + countryCount + ", Routes: " + routeCount + ", Fares: " + fareCount + ").");
            
            // Check if we need to add more flights (should have 1000 flights)
            if (flightCount < 1000) {
                System.out.println("🚀 Expanding flight data to 1000 flights over 20 days...");
                expandFlightData();
                System.out.println("✅ Flight data expansion completed successfully!");
            } else {
                System.out.println("Flight data is already complete. Skipping initialization.");
            }
            return;
        }
        
        if (needsRegeneration) {
            System.out.println("�️ Clearing old data for regeneration...");
            clearOldData();
        }
        
        System.out.println("�🚀 Starting flight data initialization with all-city distribution...");
        
        // Initialize all data
        initializeCountries();
        initializeAirports();
        initializeAircraft();
        initializeCabinClasses();
        initializeRoutes();
        initializeFlights();
        
        System.out.println("✅ Flight data initialization completed successfully!");
    }
    
    private void clearOldData() {
        System.out.println("Clearing offers and fares...");
        entityManager.createQuery("DELETE FROM Fare").executeUpdate();
        entityManager.createQuery("DELETE FROM Offer").executeUpdate();
        
        System.out.println("Clearing flights...");
        entityManager.createQuery("DELETE FROM Flight").executeUpdate();
        
        System.out.println("Clearing route segments...");
        entityManager.createQuery("DELETE FROM Segment").executeUpdate();
        
        System.out.println("Clearing routes...");
        entityManager.createQuery("DELETE FROM Route").executeUpdate();
        
        entityManager.flush();
        System.out.println("✓ Old data cleared successfully");
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
        System.out.println("🗺️ Creating routes between all city pairs...");
        
        // Get all airports
        List<Airport> airports = entityManager.createQuery("SELECT a FROM Airport a", Airport.class).getResultList();
        
        int routeCount = 0;
        
        // Create routes between all pairs of airports (both directions)
        for (int i = 0; i < airports.size(); i++) {
            for (int j = 0; j < airports.size(); j++) {
                if (i != j) { // Don't create routes from an airport to itself
                    Airport originAirport = airports.get(i);
                    Airport destAirport = airports.get(j);
                    
                    Route route = new Route();
                    route.setName(originAirport.getCity() + " to " + destAirport.getCity());
                    
                    // Calculate distance using Haversine formula
                    double distance = calculateDistance(
                        originAirport.getLatitude().doubleValue(),
                        originAirport.getLongitude().doubleValue(),
                        destAirport.getLatitude().doubleValue(),
                        destAirport.getLongitude().doubleValue()
                    );
                    route.setTotalDistance(new BigDecimal(String.format("%.2f", distance)));
                    
                    entityManager.persist(route);
                    
                    // Create segment for this route
                    Segment segment = new Segment();
                    segment.setRoute(route);
                    segment.setOriginAirport(originAirport);
                    segment.setDestinationAirport(destAirport);
                    segment.setDistance(new BigDecimal(String.format("%.2f", distance)));
                    entityManager.persist(segment);
                    
                    routeCount++;
                }
            }
        }
        
        entityManager.flush();
        System.out.println("✓ Created " + routeCount + " routes between all " + airports.size() + " cities (bidirectional)");
    }
    
    private void initializeFlights() {
        System.out.println("🛫 Creating 1000 flights over the next 20 days with dynamic pricing offers...");
        createFlightData();
    }
    
    private void expandFlightData() {
        System.out.println("🛫 Adding more flights to reach 1000 flights over 20 days...");
        createFlightData();
    }
    
    private void createFlightData() {
        // Get data for flight creation
        List<Route> routes = entityManager.createQuery("SELECT r FROM Route r", Route.class).getResultList();
        List<Aircraft> aircraft = entityManager.createQuery("SELECT a FROM Aircraft a", Aircraft.class).getResultList();
        
        // Check current flight count
        Long currentFlightCount = entityManager.createQuery("SELECT COUNT(f) FROM Flight f", Long.class).getSingleResult();
        int targetFlights = 1000;
        int flightsToCreate = targetFlights - currentFlightCount.intValue();
        
        if (flightsToCreate <= 0) {
            System.out.println("Already have " + currentFlightCount + " flights. No additional flights needed.");
            return;
        }
        
        System.out.println("Creating " + flightsToCreate + " flights across " + routes.size() + " routes...");
        
        // Create flights distributed over 20 days
        int daysToSpread = 20;
        int flightsPerDay = flightsToCreate / daysToSpread;
        int extraFlights = flightsToCreate % daysToSpread;
        
        int flightCounter = currentFlightCount.intValue() + 1;
        
        // Create flights for each day
        for (int day = 0; day < daysToSpread; day++) {
            int flightsForThisDay = flightsPerDay + (day < extraFlights ? 1 : 0);
            LocalDateTime baseDate = LocalDateTime.now().plusDays(day);
            
            // Distribute flights throughout the day and across all routes
            for (int flightOfDay = 0; flightOfDay < flightsForThisDay; flightOfDay++) {
                Flight flight = new Flight();
                
                // Generate flight number with AC prefix (Air Company)
                flight.setFlightNumber("AC" + String.format("%07d", flightCounter));
                
                // Select route in round-robin fashion for even distribution across all city pairs
                Route route = routes.get((flightCounter - 1) % routes.size());
                Aircraft selectedAircraft = aircraft.get(random.nextInt(aircraft.size()));
                
                // Distribute departure times throughout the day (5 AM to 11 PM)
                int totalMinutesInDay = 18 * 60; // 18 hours * 60 minutes
                int minuteOfDay = (flightOfDay * totalMinutesInDay) / flightsForThisDay;
                int hourOfDay = 5 + (minuteOfDay / 60);
                int minute = minuteOfDay % 60;
                
                LocalDateTime depTime = baseDate.withHour(hourOfDay).withMinute(minute).withSecond(0);
                
                // Calculate realistic arrival time based on distance
                double distanceKm = route.getTotalDistance().doubleValue();
                // Average commercial flight speed: 800-900 km/h, but factor in taxi, takeoff, landing
                int baseDurationMinutes = (int) Math.round((distanceKm / 750.0) * 60); // 750 km/h average
                int flightDurationMinutes = Math.max(60, baseDurationMinutes + 30 + random.nextInt(30)); // Min 1 hour, add buffer
                
                flight.setDepTime(depTime);
                flight.setArrTime(depTime.plusMinutes(flightDurationMinutes));
                flight.setStatus(Flight.FlightStatus.SCHEDULED);
                flight.setRoute(route);
                flight.setAircraft(selectedAircraft);
                
                entityManager.persist(flight);
                
                // Create dynamic pricing offer for this flight
                createDynamicPricingOffer(flight);
                
                flightCounter++;
                
                // Flush every 50 flights to avoid memory issues
                if (flightCounter % 50 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                    // Re-fetch routes and aircraft after clear
                    routes = entityManager.createQuery("SELECT r FROM Route r", Route.class).getResultList();
                    aircraft = entityManager.createQuery("SELECT a FROM Aircraft a", Aircraft.class).getResultList();
                }
            }
        }
        
        entityManager.flush();
        System.out.println("✓ Created " + flightsToCreate + " flights distributed over " + daysToSpread + " days");
        System.out.println("✓ Total flights in system: " + targetFlights);
        System.out.println("✓ Flights distributed across " + routes.size() + " routes between all city pairs");
        
        // Print distribution summary
        System.out.println("✓ Average flights per route: " + (targetFlights / routes.size()));
        System.out.println("✓ Average flights per day: " + (targetFlights / daysToSpread));
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