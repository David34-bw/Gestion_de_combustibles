package co.edu.unipiloto.fuelcontrol.repository;

import co.edu.unipiloto.fuelcontrol.domain.Estacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstacionRepository extends JpaRepository<Estacion, Long> {
    Optional<Estacion> findByNit(String nit);
    List<Estacion> findByActivaTrue();
    List<Estacion> findByCiudad(String ciudad);
    List<Estacion> findByAdministradorId(Long administradorId);
}
