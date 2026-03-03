package co.edu.unipiloto.fuel_control_backend.service;


import co.edu.unipiloto.fuel_control_backend.domain.entity.Usuario;
import co.edu.unipiloto.fuel_control_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TwoFactorService {

    private final UsuarioRepository usuarioRepository;

    @Value("${twofa.code.expiration}")
    private int expirationMinutes;

    public String generarCodigo(Usuario usuario) {
        // Código de 6 dígitos
        String codigo = String.format("%06d", new Random().nextInt(999999));
        usuario.setCodigoTwoFactor(codigo);
        usuario.setCodigoExpiracion(LocalDateTime.now().plusMinutes(expirationMinutes));
        usuarioRepository.save(usuario);

        // TODO: enviar por email o SMS cuando integres el servicio de mensajería
        System.out.println("Código 2FA para " + usuario.getEmail() + ": " + codigo);
        return codigo;
    }

    public boolean validarCodigo(Usuario usuario, String codigoIngresado) {
        // Verificar bloqueo por intentos
        if (usuario.getIntentosFallidos() >= 3) {
            throw new RuntimeException("Cuenta bloqueada por demasiados intentos fallidos");
        }
        // Verificar expiración
        if (usuario.getCodigoExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El código ha expirado. Solicita uno nuevo.");
        }
        // Verificar código
        if (!usuario.getCodigoTwoFactor().equals(codigoIngresado)) {
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            usuarioRepository.save(usuario);
            return false;
        }
        // Limpiar tras éxito
        usuario.setCodigoTwoFactor(null);
        usuario.setCodigoExpiracion(null);
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);
        return true;
    }
}