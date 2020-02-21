package com.curso.HelpDesk.api.Servico;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.curso.HelpDesk.api.entidade.User;

public interface UsuarioService {
	
	User findByEmail(String email);
	
	User createOrUpdate(User usuario);
	
	Optional<User> findById(String id);
	
	void delete(String id);
	
	Page<User> findAll(int page, int count);

}
