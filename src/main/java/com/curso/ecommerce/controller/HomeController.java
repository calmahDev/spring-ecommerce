package com.curso.ecommerce.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.curso.ecommerce.ResourceWebConfiguration;
import com.curso.ecommerce.model.DetalleOrden;
import com.curso.ecommerce.model.Orden;
import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.repository.IDetalleOrdenRepository;
import com.curso.ecommerce.repository.IOrdenRepository;
import com.curso.ecommerce.service.IDetalleOrdenService;
import com.curso.ecommerce.service.IOrdenService;
import com.curso.ecommerce.service.IUsuarioService;
import com.curso.ecommerce.service.ProductoService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/")
public class HomeController {

    private final ResourceWebConfiguration resourceWebConfiguration;

    private final IOrdenRepository IOrdenRepository;
	private final Logger  log=LoggerFactory.getLogger(HomeController.class);
	@Autowired
	private ProductoService productoService;
	@Autowired
	private IUsuarioService usuarioService;
	@Autowired
	private IOrdenService ordenService;
	@Autowired
	private IDetalleOrdenService detalleOrdenService;
	
	//para almacenar los detalles de la orden
	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();
	//datos de la orden
	Orden orden = new Orden();

    HomeController(IOrdenRepository IOrdenRepository, ResourceWebConfiguration resourceWebConfiguration) {
        this.IOrdenRepository = IOrdenRepository;
        this.resourceWebConfiguration = resourceWebConfiguration;
    }
	
	@GetMapping("")
	public String home(Model model, HttpSession session) { 		
		log.info("sesion del usuario: {}",session.getAttribute("idusuario"));
		model.addAttribute("productos",productoService.findAll());
		model.addAttribute("session",session.getAttribute("idusuario"));
		return "usuario/home";
	}
	
	@GetMapping("productohome/{id}")
	public String productoHome(@PathVariable Integer id, Model model) {
		log.info("id enviado como parametro {}", id);
		Producto producto = new Producto();
		Optional<Producto> productoOptional = productoService.get(id);
		producto = productoOptional.get();		
		model.addAttribute("producto",producto);
		return "usuario/productohome";
	}
	
	@PostMapping("/cart")
	public String addCart(@RequestParam Integer id,@RequestParam Integer cantidad,Model model) {
		//TODO: process POST request
		DetalleOrden detalleOrden = new DetalleOrden() ;
		Producto producto = new Producto();
		double sumaTotal =0;		
		Optional<Producto> optionalProducto =productoService.get(id);
		log.info("prodcuto aniandio: {}",optionalProducto.get());
		log.info("cantidad: {}",cantidad);
		producto=optionalProducto.get();
		detalleOrden.setCantidad(cantidad);
		detalleOrden.setPrecio(producto.getPrecio());
		detalleOrden.setNombre(producto.getNombre());
		detalleOrden.setTotal(producto.getPrecio()*cantidad);
		detalleOrden.setProducto(producto);
		//validar que no se enliste mas de una veces el mismo producto
		Integer idProducto =producto.getId();
		boolean ingresado = detalles.stream().anyMatch(p -> p.getProducto().getId()==idProducto);	
		if(!ingresado) {
			detalles.add(detalleOrden);
		}
		sumaTotal=detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		orden.setTotal(sumaTotal);
		model.addAttribute("cart",detalles);
		model.addAttribute("orden",orden);	
		return "usuario/carrito";
	}
	
	@GetMapping("/delete/cart/{id}")
	public String deleteProductoCart(@PathVariable Integer id, Model model) {
		List<DetalleOrden> ordenNueva = new ArrayList<DetalleOrden>();
		for(DetalleOrden detalleOrden : detalles) {
			if(detalleOrden.getProducto().getId() !=id) {
				ordenNueva.add(detalleOrden);
			}
		}
		detalles = ordenNueva;
		double sumaTotal=0;
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		orden.setTotal(sumaTotal);
		model.addAttribute("cart",detalles);
		model.addAttribute("orden",orden);
		return "usuario/carrito";
	}
	
	@GetMapping("/getcart")
	public String getCart(Model model, HttpSession session) {
		model.addAttribute("cart",detalles);
		model.addAttribute("orden",orden);
		//session
		model.addAttribute("session",session.getAttribute("idusurio"));
		return "/usuario/carrito";
	}
	
	@GetMapping("/order")
	public String order(Model model, HttpSession session) {
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();	
		model.addAttribute("cart",detalles);
		model.addAttribute("orden",orden);
		model.addAttribute("usuario",usuario);
		return "usuario/resumenorden";
	}
	
	@GetMapping("/saveOrder")
	public String saveOrder(HttpSession session) {	
		Date fechaCreacion = new Date();
		orden.setFechaCreacion(fechaCreacion);		
		//usuario
		Usuario usuario=usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();		
		orden.setUsuario(usuario);
		ordenService.save(orden);		
		//guardar detalles
		for(DetalleOrden dt:detalles) {
			dt.setOrden(orden);
			detalleOrdenService.save(dt);
		}		
		//limpiar lista
		orden = new Orden();
		detalles.clear();
		return "redirect:/";
	}
	
	@PostMapping("/search")
	public String searchProducto(@RequestParam String nombre, Model model){
		log.info("nombre del producto: {} ",nombre);  
		List<Producto> productos = productoService.findAll().stream().filter(p ->p.getNombre().contains(nombre)).collect(Collectors.toList());
		model.addAttribute("productos",productos); 
		return "usuario/home";
	}
}
