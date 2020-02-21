package com.curso.HelpDesk.api.service.impl;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.curso.HelpDesk.api.entidade.User;
import com.curso.HelpDesk.api.repository.UserRepository;
import com.curso.HelpDesk.api.service.UserService;


@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository usuarioInterface;
	
	@Override
	public User findByEmail(String email) {
		return usuarioInterface.findByEmail(email);
	}

	@Override
	public User createOrUpdate(User usuario) {
		return usuarioInterface.save(usuario);
	}

	@Override
	public Optional<User> findById(String id) {
		return usuarioInterface.findById(id);
	}

	@Override
	public void delete(String id) {
		usuarioInterface.deleteById(id);
	}

	@Override
	public Page<User> findAll(int page, int count) {
	Pageable pages = PageRequest.of(page, count);
		return usuarioInterface.findAll(pages);
	}

}
