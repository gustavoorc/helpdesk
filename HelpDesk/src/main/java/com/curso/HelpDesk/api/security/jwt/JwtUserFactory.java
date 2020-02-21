package com.curso.HelpDesk.api.security.jwt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.curso.HelpDesk.api.entidade.User;
import com.curso.HelpDesk.api.enums.ProfileEnum;

public class JwtUserFactory {
	
	private JwtUserFactory() {
	}
	
	public static JwtUser create(User usuario) {
		return new JwtUser(usuario.getId(), usuario.getEmail(), usuario.getPassword(),
										mapToGrantedAuthorities(usuario.getProfile()));
	}

	private static List<GrantedAuthority> mapToGrantedAuthorities(ProfileEnum profile) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(profile.toString()));
		return authorities;
	}

}
