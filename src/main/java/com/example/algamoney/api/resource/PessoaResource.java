package com.example.algamoney.api.resource;


import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
//--------------------------------------------------------------
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
	
	// injeta uma instância da classe PessoaService assim
	@Autowired
	private PessoaService pessoaService;
	//PessoaService pService = new PessoaService();
	

	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')") //6:12
	@PostMapping
	public ResponseEntity<Pessoa> criar(@RequestBody Pessoa pessoa, HttpServletResponse response) {

		Pessoa pessoaSalva = pessoaRepository.save(pessoa);

		publisher.publishEvent(new RecursoCriadoEvent(this , response, pessoaSalva.getCodigo()) );

		return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
	}


	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')") //6:12
	@GetMapping("/{codigo}")
	public Pessoa buscarPeloCodigo(@PathVariable Long codigo) {

		return this.pessoaRepository.findById(codigo).orElse(null);

	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)//espera-se responder com o código 204
	@PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA') and #oauth2.hasScope('write')") // 6:12
	public void remover(@PathVariable Long codigo) {
		this.pessoaRepository.deleteById(codigo);
		
	}
	

	
	@PutMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')") //6:12
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa) {
		Pessoa pessoaSalva = pessoaService.atualizarPessoa(codigo, pessoa);
		return ResponseEntity.ok(pessoaSalva);
	}

	
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')") //6:12
	public void atualizarPropriedadeAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		
		pessoaService.atualizarPropriedadeAtivo(codigo, ativo);
	}
	
	/*
	 * atualizado no 7.7
	*caso fosse usar com Spring data Criteria 5.7 
	@GetMapping 
	public List<Pessoa> pesquisar(PessoaFilter pessoaFilter) {
		return pessoaRepository.filtrar(pessoaFilter);
	}
	*/
	
	//atualizado no 7.7
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA')")
	public Page<Pessoa> pesquisar(@RequestParam(required = false, defaultValue = "") String nome, Pageable pageable) {
		return pessoaRepository.findByNomeContaining(nome, pageable);
	}
	
	
}
