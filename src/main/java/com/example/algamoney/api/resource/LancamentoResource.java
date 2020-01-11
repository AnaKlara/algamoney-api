package com.example.algamoney.api.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.exceptionhandler.AlgamoneyExceptionHandler.Erro;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.service.LancamentoService;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;


/* Controlador Rest*/

@RequestMapping("/lancamento") // mapeamento da requisição
@RestController // converte automaticamente pra JSON
public class LancamentoResource {


	// injetando o acervo de lancamentos
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	//criando um disparador de evento
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private LancamentoService lancamentoService; 

	/*
	@GetMapping
	public List<Lancamento> listar() {
		return lancamentoRepository.findAll();
	}
*/
	@GetMapping     //5.7
	public List<Lancamento> pequisar(LancamentoFilter lancamentoFilter) {
		return lancamentoRepository.filtrar(lancamentoFilter);
	}
	
	@GetMapping("/{codigo}")
	public Lancamento buscarPeloCodigo(@PathVariable Long codigo) {

		return this.lancamentoRepository.findById(codigo).orElse(null);
	}
	
	@PostMapping
	public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {

		Lancamento lancamentoSalvo = lancamentoService.salvar(lancamento);

		publisher.publishEvent(new RecursoCriadoEvent(this , response, lancamentoSalvo.getCodigo()) );

		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
	}
	
	
	//5.6
	@Autowired
	private MessageSource messageSource;
	
	@ExceptionHandler({  PessoaInexistenteOuInativaException.class })
	public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex) {
		
		
		String mensagemUsuario = messageSource.getMessage("mensagem.pessoa-inexeistente-ou-inativa", null, LocaleContextHolder.getLocale() );
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor ));
		
		return ResponseEntity.badRequest().body(erros);
	}
	
	
	//5.7
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)//espera-se responder com o código 204
	public void remover(@PathVariable Long codigo) {
		this.lancamentoRepository.deleteById(codigo);
	}	
	
	
	
}