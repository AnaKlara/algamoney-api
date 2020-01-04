package com.example.algamoney.api.resource;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

/* Controlador Rest*/

@RequestMapping("/categorias") //mapeamento da requisição
@RestController //converte automaticamente pra JSON
public class CategoriaResource {

	
	//injetando o acervo de categorias
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@GetMapping
	public List<Categoria> listar() {
		return categoriaRepository.findAll();
	}
	/*
	 @GetMapping
	public ResponseEntity<?> listar() {                   // ==> um objeto resposta que ainda não sei o tipo dele
		
		List<Categoria> categorias = categoriarepository.findAll();
		return !Categorias.isEmpty() ? ResponseEntity.ok(categorias): ResponseEntity.notFound().build();
	}
	 */
	
	
	@PostMapping
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		//@valid --> bean validation (video 3.10)
		
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		
		//recuperar a identificação do objeto que foi criado no banco
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
				.buildAndExpand(categoriaSalva.getCodigo()).toUri();
		
		response.setHeader("Location", uri.toASCIIString());
	
		return ResponseEntity.created(uri).body(categoriaSalva);
	}
	
	
	@GetMapping("/{codigo}")
	public Categoria buscarPeloCodigo(@PathVariable Long codigo) {
		
		return this.categoriaRepository.findById(codigo).orElse(null);
		
	}
	
	
	
}
