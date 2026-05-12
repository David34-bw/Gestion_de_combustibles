package co.edu.unipiloto.fuelcontrol.repository;

import co.edu.unipiloto.fuelcontrol.domain.Recompensa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecompensaRepository extends JpaRepository<Recompensa, Long> {
    List<Recompensa> findByActivoTrueOrderByCostoPuntosAsc();
}
