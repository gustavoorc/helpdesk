package com.curso.HelpDesk.api.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.curso.HelpDesk.api.entidade.User;

public interface UserService {
	
	User findByEmail(String email);
	
	User createOrUpdate(User usuario);
	
	Optional<User> findById(String id);
	
	void delete(String id);
	
	Page<User> findAll(int page, int count);

}
