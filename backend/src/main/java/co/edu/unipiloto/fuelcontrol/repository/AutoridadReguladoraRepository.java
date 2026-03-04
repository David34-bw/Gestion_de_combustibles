package co.edu.unipiloto.fuelcontrol.repository;

import co.edu.unipiloto.fuelcontrol.domain.AutoridadReguladora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoridadReguladoraRepository extends JpaRepository<AutoridadReguladora, Long> {
    Optional<AutoridadReguladora> findByUsuarioId(Long usuarioId);
    List<AutoridadReguladora> findByActivaTrue();
}
