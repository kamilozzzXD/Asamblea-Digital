package com.api.entity;

import javax.persistence.*;

@Entity
public class Votacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_votacion")
    private Long id;

    @Column(name = "id_asamblea")
    private Long idAsamblea;

    @Column(name = "estado")
    private String estado;

    public Long getIdVotacion() {
        return id;
    }

    public void setIdVotacion(Long idVotacion) {
        this.id = idVotacion;
    }

    public Long getIdAsamblea() {
        return idAsamblea;
    }

    public void setIdAsamblea(Long idAsamblea) {
        this.idAsamblea = idAsamblea;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
