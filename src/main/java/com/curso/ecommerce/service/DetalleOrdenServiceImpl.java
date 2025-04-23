package com.curso.ecommerce.service;
import com.curso.ecommerce.repository.IOrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.curso.ecommerce.model.DetalleOrden;
import com.curso.ecommerce.repository.IDetalleOrdenRepository;

@Service
public class DetalleOrdenServiceImpl implements IDetalleOrdenService{

    private final IOrdenRepository IOrdenRepository;
	@Autowired
	private IDetalleOrdenRepository detalleOrdenRepository;

    DetalleOrdenServiceImpl(IOrdenRepository IOrdenRepository) {
        this.IOrdenRepository = IOrdenRepository;
    }
	
	@Override
	public DetalleOrden save(DetalleOrden detalleOrden) {
		return detalleOrdenRepository.save(detalleOrden);
	}

}
