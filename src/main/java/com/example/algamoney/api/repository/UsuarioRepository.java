package com.example.algamoney.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.algamoney.api.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	public Optional<Usuario> findByEmail(String email); // Optional --> se não encontrar não preciso verificar se é diferente de null
	
	public List<Usuario> findByPermissoesDescricao(String permissaoDescricao);
	
}