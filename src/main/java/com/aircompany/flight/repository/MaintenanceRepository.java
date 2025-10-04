package com.aircompany.flight.repository;

import com.aircompany.flight.model.Maintenance;
import com.aircompany.flight.model.Maintenance.ServiceStatus;
import com.aircompany.flight.model.Maintenance.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    
    List<Maintenance> findByStatus(ServiceStatus status);
    
    List<Maintenance> findByServiceType(ServiceType serviceType);
    
    List<Maintenance> findByAircraftId(Long aircraftId);
    
    List<Maintenance> findByTechnicianId(Long technicianId);
    
    List<Maintenance> findByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT s FROM Maintenance s LEFT JOIN FETCH s.aircraft WHERE s.id = :id")
    Optional<Maintenance> findByIdWithAircraft(@Param("id") Long id);
    
    @Query("SELECT s FROM Maintenance s LEFT JOIN FETCH s.technician WHERE s.id = :id")
    Optional<Maintenance> findByIdWithTechnician(@Param("id") Long id);
    
    @Query("SELECT s FROM Maintenance s WHERE s.aircraft.id = :aircraftId AND s.status IN :statuses")
    List<Maintenance> findByAircraftIdAndStatusIn(@Param("aircraftId") Long aircraftId, 
                                            @Param("statuses") List<ServiceStatus> statuses);
    
    @Query("SELECT s FROM Maintenance s WHERE s.technician.id = :technicianId AND s.status IN :statuses")
    List<Maintenance> findByTechnicianIdAndStatusIn(@Param("technicianId") Long technicianId, 
                                              @Param("statuses") List<ServiceStatus> statuses);
    
    @Query("SELECT COUNT(s) FROM Maintenance s WHERE s.status = :status")
    Long countByStatus(@Param("status") ServiceStatus status);
    
    @Query("SELECT s FROM Maintenance s WHERE s.startDate >= :startDate AND s.endDate <= :endDate")
    List<Maintenance> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);
}
