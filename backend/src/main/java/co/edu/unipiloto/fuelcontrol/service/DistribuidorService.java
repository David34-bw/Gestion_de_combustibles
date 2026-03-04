package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.Distribuidor;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.exception.ResourceNotFoundException;
import co.edu.unipiloto.fuelcontrol.repository.DistribuidorRepository;
import co.edu.unipiloto.fuelcontrol.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistribuidorService {

    private final DistribuidorRepository distribuidorRepository;
    private final UsuarioRepository usuarioRepository;

    public Distribuidor crear(Distribuidor distribuidor, Long representanteId) {
        if (distribuidorRepository.findByNit(distribuidor.getNit()).isPresent()) {
            throw new BadRequestException("Ya existe un distribuidor con ese NIT");
        }
        if (representanteId != null) {
            Usuario rep = usuarioRepository.findById(representanteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", representanteId));
            distribuidor.setRepresentante(rep);
        }
        return distribuidorRepository.save(distribuidor);
    }

    public List<Distribuidor> listarActivos() {
        return distribuidorRepository.findByActivoTrue();
    }

    public List<Distribuidor> listarTodos() {
        return distribuidorRepository.findAll();
    }

    public Distribuidor buscarPorId(Long id) {
        return distribuidorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Distribuidor", id));
    }

    public Distribuidor actualizar(Long id, Distribuidor datos) {
        Distribuidor dist = buscarPorId(id);
        if (datos.getNombre() != null)           dist.setNombre(datos.getNombre());
        if (datos.getTelefonoContacto() != null) dist.setTelefonoContacto(datos.getTelefonoContacto());
        if (datos.getEmailContacto() != null)    dist.setEmailContacto(datos.getEmailContacto());
        if (datos.getCiudad() != null)           dist.setCiudad(datos.getCiudad());
        return distribuidorRepository.save(dist);
    }

    public Distribuidor actualizarStock(Long id, Double gasolina, Double diesel) {
        Distribuidor dist = buscarPorId(id);
        if (gasolina != null) dist.setStockGasolina(dist.getStockGasolina() + gasolina);
        if (diesel != null)   dist.setStockDiesel(dist.getStockDiesel() + diesel);
        return distribuidorRepository.save(dist);
    }

    public void desactivar(Long id) {
        Distribuidor dist = buscarPorId(id);
        dist.setActivo(false);
        distribuidorRepository.save(dist);
    }
}
