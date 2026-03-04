package co.edu.unipiloto.fuelcontrol.repository;

import co.edu.unipiloto.fuelcontrol.domain.Distribuidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistribuidorRepository extends JpaRepository<Distribuidor, Long> {
    Optional<Distribuidor> findByNit(String nit);
    List<Distribuidor> findByActivoTrue();
    List<Distribuidor> findByCiudad(String ciudad);
    List<Distribuidor> findByRepresentanteId(Long representanteId);
}
