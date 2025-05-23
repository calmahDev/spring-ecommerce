package com.curso.ecommerce.controller;


import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.curso.ecommerce.model.Orden;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.IOrdenService;
import com.curso.ecommerce.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
	private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
	@Autowired
	private IUsuarioService usuarioService;
	@Autowired
	private IOrdenService ordenService;
	BCryptPasswordEncoder passEncode= new BCryptPasswordEncoder();
	
	@GetMapping("/registro")
	public  String create() {
		return "usuario/registro";
	}
	@PostMapping("/save")
	public String save(Usuario usuario) {
		logger.info("Usuario de Registro:  {}",usuario);	
		usuario.setTipo("USER");
		usuario.setPassword(passEncode.encode(usuario.getPassword()));
		usuarioService.save(usuario);
		return "redirect:/";
	}
	
	@GetMapping("/login")
	public String login() {
		return "usuario/login";
	}
	@GetMapping("/acceder")
	public String acceder (Usuario usuario,  HttpSession	session) {
		logger.info("Acceder a : {}",usuario);
		Optional<Usuario> user= usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString()));
//		logger.info("Usuario de db: {}",user.get());
		if(user.isPresent()) {
			session.setAttribute("idusuario", user.get().getId());
			if(user.get().getTipo().equals("ADMIN")) {
				return "redirect:/administrador";
			}else {
				return "redirect:/";
			}
		}else {
			logger.info("Usuario no exite");
		}
		return "redirect:/";
	}
	
	@GetMapping("/compras")
	public String obtenerCompras(HttpSession session, Model model) {
		model.addAttribute("session", session.getAttribute("idusuario"));
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get() ;
		List<Orden> ordenes = ordenService.findByUsuario(usuario);
		model.addAttribute("ordenes",ordenes);
		return "usuario/compras";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalleCompra(@PathVariable Integer id, HttpSession session, Model model){
		logger.info("Id de la orden", id);
		Optional<Orden> orden = ordenService.findById(id);
		model.addAttribute("detalles",orden.get().getDetalle());
		model.addAttribute("session",session.getAttribute("idusuario"));		
		return "usuario/detallecompra";
	}
	
	@GetMapping("/cerrar")
	public String cerrarSession(HttpSession session) {
		session.removeAttribute("idusurio");
		return "redirect:/";
	}
	
}
