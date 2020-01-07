package com.example.algamoney.api.resource;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
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
//--------------------------------------------------------------
import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;
//import com.example.algamoney.api.service.PessoaService; some functions must be moved to another class to make the code more redable


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
	
	
	//PessoaService pService = new PessoaService();
	
	@PutMapping("/{codigo}")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa) {
		Pessoa pessoaSalva = atualizarPessoa(codigo, pessoa);
		return ResponseEntity.ok(pessoaSalva);
	}
	
	public Pessoa atualizarPessoa(Long codigo, Pessoa pessoa) {

		  Pessoa pessoaSalva = this.pessoaRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
		  BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
		  return this.pessoaRepository.save(pessoaSalva);
		}
	
	
	
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarPropriedadeAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		
		atualizarPropriedadeAtivo2(codigo, ativo);
	}
	
	/**Atualiza apenas o campo ativo da classe pessoa*/
	public void atualizarPropriedadeAtivo2(Long codigo, Boolean ativo) {

		Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
		pessoaSalva.setAtivo(ativo);
		pessoaRepository.save(pessoaSalva);
		
	}
	
	/**Busca a pessoa pelo ID e já verifica se o codigo é invalido (se invalido retorna 404 Not Found)*/
	private Pessoa buscarPessoaPeloCodigo(Long codigo) {
		
		Pessoa pessoaSalva = pessoaRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
		if (pessoaSalva == null) {
			throw new EmptyResultDataAccessException(1);
		}
		return pessoaSalva;
	}
	
	
}
