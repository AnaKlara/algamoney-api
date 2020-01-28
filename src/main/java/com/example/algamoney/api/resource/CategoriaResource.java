package com.example.algamoney.api.resource;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

/* Controlador Rest*/

@RequestMapping("/categorias") //mapeamento da requisição
@RestController //converte automaticamente pra JSON
public class CategoriaResource {

	
	//injetando o acervo de categorias
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	//criando um disparador de evento
	@Autowired
	private ApplicationEventPublisher publisher; 
	
	//----------------------------------------------------------------------------------------------------
	//------------------------------VERBOS HTTTP
	//------------------------------------------------------------------------------------------------
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA')")
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
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA')")
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		//@valid --> bean validation (video 3.10)
		
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		
		publisher.publishEvent(new RecursoCriadoEvent(this , response, categoriaSalva.getCodigo()) );

		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}
	
	
	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA')")
	public Categoria buscarPeloCodigo(@PathVariable Long codigo) {
		
		return this.categoriaRepository.findById(codigo).orElse(null);
		
	}
	
	
	
}
