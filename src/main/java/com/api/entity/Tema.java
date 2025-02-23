package com.api.entity;

import javax.persistence.*;

@Entity
public class Tema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="temaDiscusion", length = 1000)
    private String temaDiscusion;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getTemaDiscusion() {
        return temaDiscusion;
    }

    public void setTemaDiscusion(String temaDiscusion) {
        this.temaDiscusion = temaDiscusion;
    }
}
