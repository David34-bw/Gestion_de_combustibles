package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network;

import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests.VentaRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.Ventas.VentaResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.common.ApiResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.auth.AuthResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.entregas.EntregaResponse;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests.EntregaRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity.Distribuidor;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity.Estacion;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.auth.LoginRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.auth.RegisterRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests.ResolucionRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity.SolicitudCombustible;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests.SolicitudRequest;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity.Usuario;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity.Vehiculo;
import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests.VehiculoRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


import java.util.List;
import java.util.Map;

public interface ApiService {

    // ── AUTH ──────────────────────────────────────────────────
    @POST("api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    // ── USUARIOS ──────────────────────────────────────────────
    @GET("api/usuarios/me")
    Call<ApiResponse<Usuario>> getMiPerfil();

    @GET("api/usuarios/{id}")
    Call<ApiResponse<Usuario>> getUsuario(@Path("id") Long id);

    @PUT("api/usuarios/{id}")
    Call<ApiResponse<Usuario>> actualizarUsuario(@Path("id") Long id, @Body Usuario usuario);

    // ── VEHÍCULOS ─────────────────────────────────────────────
    @POST("api/vehiculos")
    Call<ApiResponse<Vehiculo>> registrarVehiculo(@Body VehiculoRequest request);

    @GET("api/vehiculos/mis-vehiculos")
    Call<ApiResponse<List<Vehiculo>>> getMisVehiculos();

    @DELETE("api/vehiculos/{id}")
    Call<ApiResponse<Void>> eliminarVehiculo(@Path("id") Long id);

    // ── PRECIOS ───────────────────────────────────────────────
    @GET("api/precios")
    Call<ApiResponse<Map<String, Object>>> consultarPrecio(
            @Query("zona") String zona,
            @Query("tipoCombustible") String tipoCombustible,
            @Query("tipoVehiculo") String tipoVehiculo);

    // ── ESTACIONES ────────────────────────────────────────────
    @GET("api/estaciones/publicas")
    Call<ApiResponse<List<Estacion>>> getEstacionesPublicas();

    @GET("api/estaciones/{id}")
    Call<ApiResponse<Estacion>> getEstacion(@Path("id") Long id);

    // ── DISTRIBUIDORES ────────────────────────────────────────
    @GET("api/distribuidores")
    Call<ApiResponse<List<Distribuidor>>> getDistribuidores();

    // ── SOLICITUDES ───────────────────────────────────────────
    @POST("api/solicitudes")
    Call<ApiResponse<SolicitudCombustible>> crearSolicitud(@Body SolicitudRequest request);

    @GET("api/solicitudes/mis-solicitudes")
    Call<ApiResponse<List<SolicitudCombustible>>> getMisSolicitudes();

    @GET("api/solicitudes")
    Call<ApiResponse<List<SolicitudCombustible>>> getTodas();

    @GET("api/solicitudes/pendientes")
    Call<ApiResponse<List<SolicitudCombustible>>> getPendientes();

    @GET("api/solicitudes/estacion/{id}")
    Call<ApiResponse<List<SolicitudCombustible>>> getSolicitudesEstacion(@Path("id") Long id);

    @POST("api/solicitudes/{id}/resolver")
    Call<ApiResponse<SolicitudCombustible>> resolverSolicitud(@Path("id") Long id,
                                                              @Body ResolucionRequest request);
    // ── ENTREGAS ──────────────────────────────────────────────
    @POST("api/entregas")
    Call<ApiResponse<EntregaResponse>> registrarEntrega(@Body EntregaRequest request);

    @GET("api/entregas/mis-entregas")
    Call<ApiResponse<List<EntregaResponse>>> getMisEntregas();

    @GET("api/entregas/estacion/{id}")
    Call<ApiResponse<List<EntregaResponse>>> getEntregasPorEstacion(@Path("id") Long id);
    @PATCH("api/solicitudes/{id}/entregar")
    Call<ApiResponse<SolicitudCombustible>> marcarEntregada(@Path("id") Long id);

    // ── INVENTARIO ESTACION ───────────────────────────────────
    @GET("api/estaciones/mi-estacion")
    Call<ApiResponse<Estacion>> getMiEstacion();

    @PATCH("api/estaciones/mi-estacion/stock")
    Call<ApiResponse<Estacion>> actualizarMiStock(@Body Map<String, Double> stock);
    // Imports a agregar:

    // Endpoints a agregar:
// ── VENTAS ────────────────────────────────────────────────
    @POST("api/ventas")
    Call<ApiResponse<VentaResponse>> registrarVenta(@Body VentaRequest request);

    @GET("api/ventas/mis-ventas")
    Call<ApiResponse<List<VentaResponse>>> getMisVentas();
}
