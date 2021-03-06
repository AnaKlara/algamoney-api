//3.9
package com.example.algamoney.api.exceptionhandler;
/*
 * Aqui estão as exceções gerais que qualquer controlador poderia gerar
 * 
 * */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice //Ele observa toda a aplicação
public class AlgamoneyExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Autowired
	private MessageSource messageSource;
	
	
	
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request)  {
		
		String mensagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale() );
		String mensagemDesenvolvedor = ex.getCause().toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor ));
		
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	
	
	
	
	@Override //3.10
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request){
		
		List<Erro> erros = criarListaDeErros(ex.getBindingResult());
		
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	
	
	
	private List<Erro> criarListaDeErros( BindingResult bindingResult ){
		
		List<Erro> erros = new ArrayList<>();
		
		for (FieldError fieldError : bindingResult.getFieldErrors() ) {
			String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String mensagemDesenvolvedor = fieldError.toString();
			erros.add(new Erro(mensagemUsuario, mensagemDesenvolvedor ) );
		}
		return erros;
	}
	
	
	
	
	public static class Erro {
		
		private String mensagemDesenvolvedor;
		private String mensagemUsuario;
		
		public Erro( String mensagemUsuario, String mensagemDesenvolvedor) {
			
			this.mensagemUsuario = mensagemUsuario;
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
		}

		public String getMensagemDesenvolvedor() {
			return mensagemDesenvolvedor;
		}

		public String getMensagemUsuario() {
			return mensagemUsuario;
		}
	}
	
	/*
	@ExceptionHandler({ EmptyResultDataAccessException.class }) //um array com os tipos de exceções que essa função irá tratar
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handlerEmptyResultDataAcessException(RuntimeException ex) {
		
	}
	*/
	@ExceptionHandler({ EmptyResultDataAccessException.class }) //um array com os tipos de exceções que essa função irá tratar
	public ResponseEntity<Object> handlerEmptyResultDataAcessException(RuntimeException ex,  WebRequest request) {
		
		String mensagemUsuario = messageSource.getMessage("recurso.notFound", null, LocaleContextHolder.getLocale() );
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor ));
		
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	
	//5.4
	@ExceptionHandler({ DataIntegrityViolationException.class }) //um array com os tipos de exceções que essa função irá tratar
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest  request){
		
		String mensagemUsuario = messageSource.getMessage("recurso.operacao-nao-encontrada", null, LocaleContextHolder.getLocale() );
		//recurso.notFound está no arquivo messages.properties em src/mais/resources
		
		String mensagemDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex);
		//String mensagemDesenvolvedor = ex.toString();
		
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor ));
		
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
}
