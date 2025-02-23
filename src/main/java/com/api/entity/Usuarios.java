package com.api.entity;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Usuarios {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="documento")
	private int documento;

	@Column(name="nombre")
	private String nombre;

	@Column(name="password")
	private String password;

	@Column(name="correo")
	private String correo;

	@Column(name="telefono")
	private BigInteger telefono;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	private Set<Propiedad> propiedades = new HashSet<>();

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	private Set<ResultadoVotacion> resultados = new HashSet<>();
	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public Set<Propiedad> getPropiedades() {
		return propiedades;
	}

	public void setPropiedades(Set<Propiedad> propiedades) {
		this.propiedades = propiedades;
	}

	public String getEmail() {
		return correo;
	}

	public void setEmail(String email) {
		this.correo = email;
	}

	public String getNombre() {
		return nombre;
	}

	public Set<ResultadoVotacion> getResultados() {
		return resultados;
	}

	public void setResultados(Set<ResultadoVotacion> resultados) {
		this.resultados = resultados;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getDocumento() {
		return documento;
	}
	public void setDocumento(int documento) {
		this.documento = documento;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public BigInteger getTelefono() {
		return telefono;
	}

	public void setTelefono(BigInteger telefono) {
		this.telefono = telefono;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "authorities_users", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
	private Set<Authority> authority=new HashSet<>();

	public Set<Authority> getAuthority() {
		return authority;
	}

	public void setAuthority(Set<Authority> authority) {
		this.authority = authority;
	}
}
