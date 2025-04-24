package com.curso.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.curso.ecommerce.model.Orden;

public interface IOrdenService {
	Orden save(Orden orden);
	List<Orden> findAll();	
	String generaNumeroOrden();
}

