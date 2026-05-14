package co.edu.unipiloto.fuelcontrol.repository;

import co.edu.unipiloto.fuelcontrol.domain.UsuarioParticular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioParticularRepository extends JpaRepository<UsuarioParticular, Long> {
    Optional<UsuarioParticular> findByUsuarioId(Long usuarioId);
}
