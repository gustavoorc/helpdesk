package com.curso.HelpDesk.api.security.model;

import com.curso.HelpDesk.api.entidade.User;

public class CurrentUser {

	private String token;
	private User usuario;
	
	public CurrentUser(String token, User usuario) {
		this.token = token;
		this.usuario = usuario;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}
}
