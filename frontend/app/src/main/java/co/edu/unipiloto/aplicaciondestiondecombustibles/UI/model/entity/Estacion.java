package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity;

public class Estacion {
    private Long id;
    private String nombre;
    private String direccion;
    private String ciudad;
    private String nit;
    private Double stockGasolina;
    private Double stockDiesel;
    private Boolean activa;
    private String Departamento;


    public Long getId()              { return id; }

    public String getNombre()        { return nombre; }
    public String getDireccion()     { return direccion; }
    public String getCiudad()        { return ciudad; }
    public String getNit()           { return nit; }
    public Double getStockGasolina() { return stockGasolina; }
    public Double getStockDiesel()   { return stockDiesel; }
    public String getDepartamento()  { return Departamento; }

    public Boolean getActiva()       { return activa; }
}