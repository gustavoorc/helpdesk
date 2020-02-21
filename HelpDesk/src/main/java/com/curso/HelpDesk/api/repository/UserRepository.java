package com.curso.HelpDesk.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.curso.HelpDesk.api.entidade.User;

public interface UserRepository extends MongoRepository<User, String> {

	User findByEmail(String email);
}
