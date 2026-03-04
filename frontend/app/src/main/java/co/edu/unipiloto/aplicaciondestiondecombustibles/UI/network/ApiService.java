// ─────────────────────────────────────────────────────────────────


package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.network;

import co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

public interface ApiService {

    // ══════════════════════════════════════
    //  AUTH
    // ══════════════════════════════════════

    @POST("api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    // ══════════════════════════════════════
    //  USUARIOS
    // ══════════════════════════════════════

    @GET("api/usuarios/me")
    Call<ApiResponse<Usuario>> getMiPerfil();

    @GET("api/usuarios/{id}")
    Call<ApiResponse<Usuario>> getUsuario(@Path("id") Long id);

    @PUT("api/usuarios/{id}")
    Call<ApiResponse<Usuario>> actualizarUsuario(@Path("id") Long id, @Body Usuario usuario);

    @GET("api/usuarios")
    Call<ApiResponse<List<Usuario>>> listarUsuarios();

    // ══════════════════════════════════════
    //  ESTACIONES
    // ══════════════════════════════════════

    @GET("api/estaciones/publicas")
    Call<ApiResponse<List<Estacion>>> getEstacionesPublicas();

    @GET("api/estaciones")
    Call<ApiResponse<List<Estacion>>> getEstaciones();

    @GET("api/estaciones/{id}")
    Call<ApiResponse<Estacion>> getEstacion(@Path("id") Long id);

    @POST("api/estaciones")
    Call<ApiResponse<Estacion>> crearEstacion(@Body Estacion estacion,
                                              @Query("administradorId") Long adminId);

    @PUT("api/estaciones/{id}")
    Call<ApiResponse<Estacion>> actualizarEstacion(@Path("id") Long id, @Body Estacion estacion);

    @PATCH("api/estaciones/{id}/stock")
    Call<ApiResponse<Estacion>> actualizarStockEstacion(@Path("id") Long id,
                                                        @Body Map<String, Double> stock);

    // ══════════════════════════════════════
    //  DISTRIBUIDORES
    // ══════════════════════════════════════

    @GET("api/distribuidores")
    Call<ApiResponse<List<Distribuidor>>> getDistribuidores();

    @GET("api/distribuidores/{id}")
    Call<ApiResponse<Distribuidor>> getDistribuidor(@Path("id") Long id);

    @POST("api/distribuidores")
    Call<ApiResponse<Distribuidor>> crearDistribuidor(@Body Distribuidor distribuidor,
                                                      @Query("representanteId") Long repId);

    @PATCH("api/distribuidores/{id}/stock")
    Call<ApiResponse<Distribuidor>> actualizarStockDistribuidor(@Path("id") Long id,
                                                                @Body Map<String, Double> stock);

    // ══════════════════════════════════════
    //  SOLICITUDES
    // ══════════════════════════════════════

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

    @GET("api/solicitudes/distribuidor/{id}")
    Call<ApiResponse<List<SolicitudCombustible>>> getSolicitudesDistribuidor(@Path("id") Long id);

    @POST("api/solicitudes/{id}/resolver")
    Call<ApiResponse<SolicitudCombustible>> resolverSolicitud(@Path("id") Long id,
                                                              @Body ResolucionRequest request);

    @PATCH("api/solicitudes/{id}/entregar")
    Call<ApiResponse<SolicitudCombustible>> marcarEntregada(@Path("id") Long id);
}
