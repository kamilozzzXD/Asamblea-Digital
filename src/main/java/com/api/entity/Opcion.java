package com.api.entity;

import javax.persistence.*;

@Entity
public class Opcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idOpcionVoto")
    private PreguntaVoto preguntaVoto;

    @Column(name = "opcion")
    private String opcion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PreguntaVoto getOpcionVoto() {
        return preguntaVoto;
    }

    public void setOpcionVoto(PreguntaVoto preguntaVoto) {
        this.preguntaVoto = preguntaVoto;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }
}
