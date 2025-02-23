package com.api.entity;

import javax.persistence.*;

@Entity
public class Propiedad {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="direccion")
    private String direccion;

    @Column(name="area")
    private float area;

    @Column(name="habitada")
    private boolean habitada;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuarios usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public float getArea() {
        return area;
    }
    public void setArea(float area) {
        this.area = area;
    }

    public boolean isHabitada() {
        return habitada;
    }

    public void setHabitada(boolean habitada) {
        this.habitada = habitada;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }
}
