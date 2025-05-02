package com.curso.ecommerce.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.curso.ecommerce.model.Usuario;

import jakarta.servlet.http.HttpSession;

@Service
public class UserDetailServiceImpl implements UserDetailsService{
	@Autowired
	private IUsuarioService usuarioService;
	@Autowired
	private BCryptPasswordEncoder bCrypt;	
	@Autowired
	HttpSession session;
	private Logger log = LoggerFactory.getLogger(UserDetailServiceImpl.class);
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("esto es el user name: ");
		Optional<Usuario> optionaUser= usuarioService.findByEmail(username);
		if(optionaUser.isPresent()) {
			log.info("esto es el ID del usuario: {}",optionaUser.get().getId());
			session.setAttribute("idusuario", optionaUser.get().getId());
			Usuario usuario= optionaUser.get();
			return User.builder().username(usuario.getNombre()).password(bCrypt.encode(usuario.getPassword())).roles(usuario.getTipo()).build();
		}else {
			throw new UsernameNotFoundException("usuario no encontrado");
		}
	}

}
