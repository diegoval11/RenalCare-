package itca.soft.renalcare.data.models;

public class PacienteDoctor {
    private int id_paciente;
    private String nombre;
    private String dui;
    private String fecha_nacimiento;
    private String genero;
    private String tipo_tratamiento;
    private double peso;
    private double nivel_creatinina;
    private String sintomas;
    private String observaciones;
    private String telefono_emergencia;
    private String contacto_emergencia;

    // Constructor vacío
    public PacienteDoctor() {}

    // Constructor completo
    public PacienteDoctor(int id_paciente, String nombre, String dui, String fecha_nacimiento,
                          String genero, String tipo_tratamiento, double peso, double nivel_creatinina,
                          String sintomas, String observaciones, String telefono_emergencia,
                          String contacto_emergencia) {
        this.id_paciente = id_paciente;
        this.nombre = nombre;
        this.dui = dui;
        this.fecha_nacimiento = fecha_nacimiento;
        this.genero = genero;
        this.tipo_tratamiento = tipo_tratamiento;
        this.peso = peso;
        this.nivel_creatinina = nivel_creatinina;
        this.sintomas = sintomas;
        this.observaciones = observaciones;
        this.telefono_emergencia = telefono_emergencia;
        this.contacto_emergencia = contacto_emergencia;
    }

    // Getters y Setters
    public int getId_paciente() { return id_paciente; }
    public void setId_paciente(int id_paciente) { this.id_paciente = id_paciente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }

    public String getFecha_nacimiento() { return fecha_nacimiento; }
    public void setFecha_nacimiento(String fecha_nacimiento) { this.fecha_nacimiento = fecha_nacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getTipo_tratamiento() { return tipo_tratamiento; }
    public void setTipo_tratamiento(String tipo_tratamiento) { this.tipo_tratamiento = tipo_tratamiento; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getNivel_creatinina() { return nivel_creatinina; }
    public void setNivel_creatinina(double nivel_creatinina) { this.nivel_creatinina = nivel_creatinina; }

    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getTelefono_emergencia() { return telefono_emergencia; }
    public void setTelefono_emergencia(String telefono_emergencia) { this.telefono_emergencia = telefono_emergencia; }

    public String getContacto_emergencia() { return contacto_emergencia; }
    public void setContacto_emergencia(String contacto_emergencia) { this.contacto_emergencia = contacto_emergencia; }
}