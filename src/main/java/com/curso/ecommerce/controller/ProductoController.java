package com.curso.ecommerce.controller;



import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.PostExchange;

import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.ProductoService;
import com.curso.ecommerce.service.UploadFileService;
import com.curso.ecommerce.service.UsuarioServiceImpl;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/productos")
public class ProductoController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
	
	@Autowired
	private ProductoService productoService;
	@Autowired
	private UploadFileService upload;
	@Autowired
	private UsuarioServiceImpl usuarioServiceImpl;
	@GetMapping("")
	public String show(Model model) {
		model.addAttribute("productos",productoService.findAll());
		return "productos/show";
	}
	
	@GetMapping("/create")
	public String create(){
		return"productos/create";
	}
	
	@PostExchange("/save")
	public String save(Producto producto,@RequestParam("img") MultipartFile file, HttpSession session) throws Exception {
		LOGGER.info("Este es el objeto producto {}",producto);
		Usuario u = usuarioServiceImpl.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		producto.setUsuario(u);
		//imagen
		
		if(producto.getId() == null){ //cuando se crea un producto
			String nombreImagen = upload.saveImage(file);
			producto.setImagen(nombreImagen);
		}else {
			
		}
		
		productoService.save(producto);
		return "redirect:/productos";	
	}
	
	@GetMapping("edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		Producto producto = new Producto();
		Optional<Producto> optionalProducto = productoService.get(id);
		
		producto = optionalProducto.get();
		LOGGER.info("El producto buscado es: {}",producto);
		
		model.addAttribute("producto",producto);
		
		return "productos/edit";
	}
	
	@PostMapping("update")
	public String update(Producto producto,@RequestParam("img") MultipartFile file) throws Exception {
		Producto p = new Producto();
		p = productoService.get(producto.getId()).get();
		
		if(file.isEmpty()) {//editamos el producto pero no cambiamos la imagen
			
			producto.setImagen(p.getImagen());
		}else { //editamos producto he imagen
			if(!p.getImagen().equals("default.jpg")){ //eliminar cuando no sea la imagen por defecto
				upload.deleteImage(p.getImagen());
			}
			String nombreImagen = upload.saveImage(file);
			producto.setImagen(nombreImagen);
		}
		producto.setUsuario(p.getUsuario());
		productoService.update(producto);
		return "redirect:/productos";
	}
	@GetMapping("delete/{id}")
	public String delet(@PathVariable Integer id){
		Producto p = new Producto();
		p =productoService.get(id).get();
		if(!p.getImagen().equals("default.jpg")){ //eliminar cuando no sea la imagen por defecto
			upload.deleteImage(p.getImagen());
		}
		productoService.delete(id);
		return "redirect:/productos";
	}
}
