package com.curso.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.curso.ecommerce.model.Orden;
import com.curso.ecommerce.model.Usuario;

public interface IOrdenService {
	Orden save(Orden orden);
	Optional<Orden> findById(Integer id);
	List<Orden> findAll();	
	String generaNumeroOrden();
	List<Orden> findByUsuario(Usuario usuario);

}

