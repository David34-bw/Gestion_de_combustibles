package co.edu.unipiloto.fuelcontrol.repository;

import co.edu.unipiloto.fuelcontrol.domain.EntregaCombustible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntregaRepository extends JpaRepository<EntregaCombustible, Long> {
    List<EntregaCombustible> findByDistribuidorIdOrderByFechaEntregaDesc(Long distribuidorId);
    List<EntregaCombustible> findByEstacionIdOrderByFechaEntregaDesc(Long estacionId);
}