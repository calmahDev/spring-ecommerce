package com.curso.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.curso.ecommerce.model.Orden;
import com.curso.ecommerce.repository.IOrdenRepository;

public class OrderServiceImpl implements IOrdenService{
	@Autowired
	private IOrdenRepository ordenRepository;
	@Override
	public Orden save(Orden orden) {
		// TODO Auto-generated method stub
		return ordenRepository.save(orden);
	}

	 
}
