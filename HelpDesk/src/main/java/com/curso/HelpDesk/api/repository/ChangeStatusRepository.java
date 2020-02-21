package com.curso.HelpDesk.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.curso.HelpDesk.api.entidade.ChangeStatus;

public interface ChangeStatusRepository extends MongoRepository<ChangeStatus, String> {
	
	Iterable<ChangeStatus> findByTicketIdOrderByDateChangeStatusDesc(String ticketId);

}
