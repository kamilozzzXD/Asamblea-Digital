package com.api.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class PreguntaVoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOpcionVoto;
    @Column(name = "tipoOpcion")
    private String tipoOpcion;

    @Column(name = "pregunta")
    private String pregunta;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "idOpcionVoto")
    private List<Opcion> opciones;

    @Column(name = "idVotacion")
    private Long votacionId;

    public Long getIdOpcionVoto() {
        return idOpcionVoto;
    }

    public void setIdOpcionVoto(Long idOpcionVoto) {
        this.idOpcionVoto = idOpcionVoto;
    }

    public String getTipoOpcion() {
        return tipoOpcion;
    }

    public void setTipoOpcion(String tipoOpcion) {
        this.tipoOpcion = tipoOpcion;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public Long getIdVotacion() {
        return votacionId;
    }

    public void setIdVotacion(Long idVotacion) {
        this.votacionId = idVotacion;
    }

    public List<Opcion> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<Opcion> opciones) {
        this.opciones = opciones;
    }
}