package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.domain.Vehiculo;
import co.edu.unipiloto.fuelcontrol.dto.request.VehiculoRequest;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.exception.ResourceNotFoundException;
import co.edu.unipiloto.fuelcontrol.repository.UsuarioRepository;
import co.edu.unipiloto.fuelcontrol.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository,
                           UsuarioRepository usuarioRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository  = usuarioRepository;
    }

    public Vehiculo registrar(Long usuarioId, VehiculoRequest request) {
        if (vehiculoRepository.existsByPlaca(request.getPlaca().toUpperCase())) {
            throw new BadRequestException("Ya existe un vehículo con esa placa registrado");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));

        Vehiculo vehiculo = Vehiculo.builder()
                .placa(request.getPlaca().toUpperCase())
                .tipoVehiculo(request.getTipoVehiculo())
                .aplicaSubsidio(request.getAplicaSubsidio() != null ? request.getAplicaSubsidio() : false)
                .numeroRunt(request.getNumeroRunt())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .usuario(usuario)
                .build();

        return vehiculoRepository.save(vehiculo);
    }

    public List<Vehiculo> listarPorUsuario(Long usuarioId) {
        return vehiculoRepository.findByUsuarioId(usuarioId);
    }

    public Vehiculo buscarPorId(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo", id));
    }

    public void eliminar(Long id) {
        vehiculoRepository.deleteById(id);
    }
}
