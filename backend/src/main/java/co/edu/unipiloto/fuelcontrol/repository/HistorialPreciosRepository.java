package co.edu.unipiloto.fuelcontrol.repository;

import co.edu.unipiloto.fuelcontrol.domain.HistorialPrecios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialPreciosRepository extends JpaRepository<HistorialPrecios, Long> {
    List<HistorialPrecios> findAllByOrderByFechaCambioDesc();
    Optional<HistorialPrecios> findTopByZonaAndTipoCombustibleOrderByFechaCambioDesc(
            String zona, String tipoCombustible);
}