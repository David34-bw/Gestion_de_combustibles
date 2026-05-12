package co.edu.unipiloto.fuelcontrol.repository;

import co.edu.unipiloto.fuelcontrol.domain.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByEstacionIdOrderByFechaVentaDesc(Long estacionId);
    List<Venta> findByUsuarioIdOrderByFechaVentaDesc(Long usuarioId);
}