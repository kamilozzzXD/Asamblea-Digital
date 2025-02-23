package com.api.entity;

import javax.persistence.*;

@Entity
public class Acta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String acta;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "id_asamblea")
    private Asamblea asamblea;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActa() {
        return acta;
    }

    public void setActa(String acta) {
        this.acta = acta;
    }

    public Asamblea getAsamblea() {
        return asamblea;
    }

    public void setAsamblea(Asamblea asamblea) {
        this.asamblea = asamblea;
    }
}
