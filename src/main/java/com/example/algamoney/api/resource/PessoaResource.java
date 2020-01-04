package com.example.algamoney.api.resource;

import java.net.URI;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;

/* Controlador Rest*/

@RequestMapping("/pessoas") //mapeamento da requisição
@RestController //converte automaticamente pra JSON
public class PessoaResource {
	
	
	    //injetando o acervo de categorias
		@Autowired
		private PessoaRepository pessoaRepository;
		
		@GetMapping
		public List<Pessoa> listar() {
			return pessoaRepository.findAll();
		}
		
		@PostMapping
		public ResponseEntity<Pessoa> criar( @RequestBody Pessoa pessoa, HttpServletResponse response) {
			Pessoa pessoaSalva = pessoaRepository.save(pessoa);
			
			URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
					.buildAndExpand(pessoaSalva.getCodigo()).toUri();
				response.setHeader("Location", uri.toASCIIString());
				
				return ResponseEntity.created(uri).body(pessoaSalva);
		}
		
		
		
		@GetMapping("/{codigo}")
		public Pessoa buscarPeloCodigo(@PathVariable Long codigo) {
			
			return this.pessoaRepository.findById(codigo).orElse(null);
			
		}

}
