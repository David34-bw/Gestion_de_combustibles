package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.Estacion;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.exception.ResourceNotFoundException;
import co.edu.unipiloto.fuelcontrol.repository.EstacionRepository;
import co.edu.unipiloto.fuelcontrol.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstacionService {

    private final EstacionRepository estacionRepository;
    private final UsuarioRepository usuarioRepository;

    public Estacion crear(Estacion estacion, Long administradorId) {
        if (estacion.getNit() != null && estacionRepository.findByNit(estacion.getNit()).isPresent()) {
            throw new BadRequestException("Ya existe una estación con ese NIT");
        }
        if (administradorId != null) {
            Usuario admin = usuarioRepository.findById(administradorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", administradorId));
            estacion.setAdministrador(admin);
        }
        return estacionRepository.save(estacion);
    }

    public List<Estacion> listarActivas() {
        return estacionRepository.findByActivaTrue();
    }

    public List<Estacion> listarTodas() {
        return estacionRepository.findAll();
    }

    public Estacion buscarPorId(Long id) {
        return estacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estación", id));
    }

    public Estacion actualizar(Long id, Estacion datos) {
        Estacion estacion = buscarPorId(id);
        if (datos.getNombre() != null)     estacion.setNombre(datos.getNombre());
        if (datos.getDireccion() != null)  estacion.setDireccion(datos.getDireccion());
        if (datos.getCiudad() != null)     estacion.setCiudad(datos.getCiudad());
        if (datos.getDepartamento() != null) estacion.setDepartamento(datos.getDepartamento());
        return estacionRepository.save(estacion);
    }

    public Estacion actualizarStock(Long id, Double gasolina, Double diesel) {
        Estacion estacion = buscarPorId(id);

        if (gasolina != null) {
            double nuevoStock = estacion.getStockGasolina() + gasolina;
            if (estacion.getCapacidadGasolina() != null && nuevoStock > estacion.getCapacidadGasolina()) {
                throw new BadRequestException("El stock supera la capacidad de gasolina de la estación");
            }
            estacion.setStockGasolina(nuevoStock);
        }
        if (diesel != null) {
            double nuevoStock = estacion.getStockDiesel() + diesel;
            if (estacion.getCapacidadDiesel() != null && nuevoStock > estacion.getCapacidadDiesel()) {
                throw new BadRequestException("El stock supera la capacidad de diesel de la estación");
            }
            estacion.setStockDiesel(nuevoStock);
        }
        return estacionRepository.save(estacion);
    }

    public void desactivar(Long id) {
        Estacion estacion = buscarPorId(id);
        estacion.setActiva(false);
        estacionRepository.save(estacion);
    }
}
