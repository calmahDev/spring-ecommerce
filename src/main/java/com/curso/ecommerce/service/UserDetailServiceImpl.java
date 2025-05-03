package com.curso.ecommerce.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.curso.ecommerce.model.Usuario;

import jakarta.servlet.http.HttpSession;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private HttpSession session;
    
    private Logger log = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Esto es el username: {}", username);
        Optional<Usuario> optionalUser = usuarioService.findByEmail(username);
        
        if(optionalUser.isPresent()) {
            Usuario usuario = optionalUser.get();
            log.info("Esto es el ID del usuario: {}", usuario.getId());
            session.setAttribute("idusuario", usuario.getId());
            
            return User.builder()
                .username(usuario.getNombre())
                .password(usuario.getPassword()) // La contraseña ya debe estar codificada
                .roles(usuario.getTipo())
                .build();
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
    }
}