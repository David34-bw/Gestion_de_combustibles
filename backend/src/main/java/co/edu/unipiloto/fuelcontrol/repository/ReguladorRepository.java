package co.edu.unipiloto.fuelcontrol.repository;

import co.edu.unipiloto.fuelcontrol.domain.Regulador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReguladorRepository extends JpaRepository<Regulador, Long> {
    Optional<Regulador> findByUsuarioId(Long usuarioId);
    Optional<Regulador> findByNit(String nit);
}
