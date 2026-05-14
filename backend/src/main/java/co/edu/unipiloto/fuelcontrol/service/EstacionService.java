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
    private static final double CAPACIDAD_MAX = 500.0;

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
        if (gasolina < 0) {
            throw new BadRequestException("La cantidad de gasolina debe ser positiva");
        }
        double nuevoStock = estacion.getStockGasolina() + gasolina;
        if (nuevoStock > CAPACIDAD_MAX) {
            throw new BadRequestException(
                "Stock de gasolina supera el máximo de 500 galones. " +
                "Stock actual: " + estacion.getStockGasolina() + " galones");
        }
        estacion.setStockGasolina(nuevoStock);
    }
    if (diesel != null) {
        if (diesel < 0) {
            throw new BadRequestException("La cantidad de diesel debe ser positiva");
        }
        double nuevoStock = estacion.getStockDiesel() + diesel;
        if (nuevoStock > CAPACIDAD_MAX) {
            throw new BadRequestException(
                "Stock de diesel supera el máximo de 500 galones. " +
                "Stock actual: " + estacion.getStockDiesel() + " galones");
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
        public Estacion buscarPorAdministrador(Long usuarioId) {
        return estacionRepository.findByAdministradorId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                "No tienes una estación asociada a tu cuenta"));
    }
}
