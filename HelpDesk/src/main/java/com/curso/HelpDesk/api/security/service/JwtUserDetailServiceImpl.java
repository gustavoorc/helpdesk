package com.curso.HelpDesk.api.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.curso.HelpDesk.api.Servico.UsuarioService;
import com.curso.HelpDesk.api.entidade.User;
import com.curso.HelpDesk.api.security.jwt.JwtUserFactory;

@Service
public class JwtUserDetailServiceImpl implements UserDetailsService{
	
	@Autowired
	private UsuarioService usuarioService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User usuario = usuarioService.findByEmail(email);
		
		if(usuario == null) {
			throw new UsernameNotFoundException(String.format("Não foi encontrado usuario com o email '%s",	 email));
		}else {
			return JwtUserFactory.create(usuario);
		}
	}

}
