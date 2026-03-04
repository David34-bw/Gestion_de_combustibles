package co.edu.unipiloto.fuelcontrol.repository;

import co.edu.unipiloto.fuelcontrol.domain.SolicitudCombustible;
import co.edu.unipiloto.fuelcontrol.domain.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudCombustibleRepository extends JpaRepository<SolicitudCombustible, Long> {
    List<SolicitudCombustible> findByUsuarioId(Long usuarioId);
    List<SolicitudCombustible> findByEstado(EstadoSolicitud estado);
    List<SolicitudCombustible> findByEstacionId(Long estacionId);
    List<SolicitudCombustible> findByDistribuidorId(Long distribuidorId);
    List<SolicitudCombustible> findByUsuarioIdOrderByFechaSolicitudDesc(Long usuarioId);
    List<SolicitudCombustible> findAllByOrderByFechaSolicitudDesc();
}
