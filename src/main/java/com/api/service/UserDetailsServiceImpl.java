package com.api.service;

import com.api.entity.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

import com.api.entity.Usuarios;
import com.api.repository.RepositoryUsuario;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	RepositoryUsuario repositorio;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		com.api.entity.Usuarios appUser = repositorio.findByDocumento(Integer.parseInt(username));

		//Mapear nuestra lista de Authority con la de spring security
		List grantList = new ArrayList();
		for (Authority authority: appUser.getAuthority()) {
			GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getAuthority());
			grantList.add(grantedAuthority);
		}

		UserDetails user=(UserDetails) new User(String.valueOf(appUser.getDocumento()) ,appUser.getPassword(),grantList);
		return user;
	}
	
	
}
