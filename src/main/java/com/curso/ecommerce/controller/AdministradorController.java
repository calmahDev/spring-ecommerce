package com.curso.ecommerce.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.curso.ecommerce.model.Orden;
import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.service.IOrdenService;
import com.curso.ecommerce.service.IUsuarioService;
import com.curso.ecommerce.service.ProductoService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {
	@Autowired
	private ProductoService  productoService ;
	@Autowired
	private IUsuarioService usuarioService;
	@Autowired
	private IOrdenService ordenService;
	
	private Logger logg = LoggerFactory.getLogger(AdministradorController.class);
	
	@GetMapping("")
	public String home (Model model) {
		List<Producto> productos = productoService.findAll();
		model.addAttribute("productos",productos);
		return "administrador/home";
	}
	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		model.addAttribute("usuarios",usuarioService.findAll());
		return "administrador/usuarios";
	}
	@GetMapping("/ordenes")
	public String ordenes(Model model) {
		model.addAttribute("ordenes",ordenService.findAll());
		return "administrador/ordenes";
	}
	@GetMapping("/detalle/{id}")
	public String detalleOrdenes(Model model, @PathVariable Integer id) {
		logg.info("ID de la orden:  {}",id);
		Orden orden =ordenService.findById(id).get();
		model.addAttribute("detalles",orden.getDetalle());
		logg.info("el detalle es:  {}", orden.getDetalle());
		return "administrador/detallecompra";
	}
}
