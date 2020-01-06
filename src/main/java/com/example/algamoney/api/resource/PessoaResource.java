package com.example.algamoney.api.resource;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.service.PessoaService;


/* Controlador Rest*/

@RequestMapping("/pessoas") // mapeamento da requisição
@RestController // converte automaticamente pra JSON
public class PessoaResource {

	// injetando o acervo de pessoas
	@Autowired
	private PessoaRepository pessoaRepository;


	//criando um disparador de evento
	@Autowired
	private ApplicationEventPublisher publisher; 

	@GetMapping
	public List<Pessoa> listar() {
		return pessoaRepository.findAll();
	}

	@PostMapping
	public ResponseEntity<Pessoa> criar(@RequestBody Pessoa pessoa, HttpServletResponse response) {

		Pessoa pessoaSalva = pessoaRepository.save(pessoa);

		publisher.publishEvent(new RecursoCriadoEvent(this , response, pessoaSalva.getCodigo()) );

		return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
	}



	@GetMapping("/{codigo}")
	public Pessoa buscarPeloCodigo(@PathVariable Long codigo) {

		return this.pessoaRepository.findById(codigo).orElse(null);

	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)//espera-se responder com o código 204
	public void remover(@PathVariable Long codigo) {
		this.pessoaRepository.deleteById(codigo);
		
	}
	
	
	
	@PutMapping("/{codigo}") 
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa) {
		
		    return ResponseEntity.ok(PessoaService.atualizar(codigo, pessoa)); 
	   } 

}
