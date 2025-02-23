package com.api.entity;

import javax.persistence.*;

@Entity
public class FAQ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuarios usuario;

    @JoinColumn(name = "usuario_id")
    private String responde;

    @JoinColumn(name = "pregunta")
    private String pregunta;

    @JoinColumn(name = "respuesta")
    private String respuesta;

    @JoinColumn(name = "revision")
    private Boolean revision;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public Boolean getRevision() {
        return revision;
    }

    public void setRevision(Boolean revision) {
        this.revision = revision;
    }

    public String getResponde() {
        return responde;
    }

    public void setResponde(String responde) {
        this.responde = responde;
    }
}
