package co.edu.unipiloto.fuelcontrol.repository;

import co.edu.unipiloto.fuelcontrol.domain.CanjeRecompensa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanjeRecompensaRepository extends JpaRepository<CanjeRecompensa, Long> {
    List<CanjeRecompensa> findByUsuarioIdOrderByFechaCanjeDesc(Long usuarioId);
}
