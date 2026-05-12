package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.Recompensa;
import co.edu.unipiloto.fuelcontrol.dto.response.RecompensaResponse;
import co.edu.unipiloto.fuelcontrol.repository.RecompensaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecompensaService {

    private final RecompensaRepository recompensaRepository;

    public RecompensaService(RecompensaRepository recompensaRepository) {
        this.recompensaRepository = recompensaRepository;
    }

    public List<RecompensaResponse> listarActivas() {
        return recompensaRepository.findByActivoTrueOrderByCostoPuntosAsc()
                .stream().map(this::toResponse).toList();
    }

    public void seedSiVacio() {
        if (recompensaRepository.count() > 0) {
            return;
        }
        Recompensa r1 = new Recompensa();
        r1.setNombre("Descuento 5%");
        r1.setDescripcion("Aplica 5% de descuento en una compra futura");
        r1.setCostoPuntos(10);
        r1.setPorcentajeDescuento(5);
        recompensaRepository.save(r1);

        Recompensa r2 = new Recompensa();
        r2.setNombre("Descuento 10%");
        r2.setDescripcion("Aplica 10% de descuento en una compra futura");
        r2.setCostoPuntos(20);
        r2.setPorcentajeDescuento(10);
        recompensaRepository.save(r2);

        Recompensa r3 = new Recompensa();
        r3.setNombre("Descuento 15%");
        r3.setDescripcion("Aplica 15% de descuento en una compra futura");
        r3.setCostoPuntos(30);
        r3.setPorcentajeDescuento(15);
        recompensaRepository.save(r3);
    }

    private RecompensaResponse toResponse(Recompensa recompensa) {
        return RecompensaResponse.builder()
                .id(recompensa.getId())
                .nombre(recompensa.getNombre())
                .descripcion(recompensa.getDescripcion())
                .costoPuntos(recompensa.getCostoPuntos())
                .porcentajeDescuento(recompensa.getPorcentajeDescuento())
                .build();
    }
}
