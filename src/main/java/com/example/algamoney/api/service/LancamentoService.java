package com.example.algamoney.api.service;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

//5.6

@Service
public class LancamentoService {

	
		@Autowired
		private PessoaRepository pessoaRepository;
		
		@Autowired
		private LancamentoRepository lancamentoRepository;

		public Lancamento salvar(@Valid Lancamento lancamento) {
			
			/*
			 * 	Pessoa pessoa = pessoaService.buscarPessoaPeloCodigo(lancamento.getPessoa().getCodigo());
			 * Não é o ideal pois, se tantarmos salvar um lancamento com uma pessoa que não existe o servidor irá retornar 404
			 * porém não é culpa do servidor e sim do cliente que forneceu o código de pessoa errada
			 * 
			 * */
			Optional<Pessoa> optPessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
			Pessoa pessoa = optPessoa.get();
			if (pessoa == null || pessoa.isInativo()) {
				throw new PessoaInexistenteOuInativaException();
			}
			
			return lancamentoRepository.save(lancamento);
		}
		
		
		
		// ATUALIZAR
		
		
		public Lancamento atualizar(Long codigo, Lancamento lancamento) {
			
			Lancamento lancamentoSalvo = buscarLancamentoExistente(codigo).orElse(null);
			
			
			if (lancamentoSalvo != null && !lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
				validarPessoa(lancamento);
			}

			BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");

			return lancamentoRepository.save(lancamentoSalvo);
		}

		private void validarPessoa(Lancamento lancamento) {
			Pessoa pessoa = null;
			if (lancamento.getPessoa().getCodigo() != null) {
				pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo()).orElse(null);;
			}

			if (pessoa == null || pessoa.isInativo()) {
				throw new PessoaInexistenteOuInativaException();
			}
		}

		private Optional<Lancamento> buscarLancamentoExistente(Long codigo) {
			Optional<Lancamento> lancamentoSalvo = lancamentoRepository.findById(codigo);
			if (lancamentoSalvo == null) {
				throw new IllegalArgumentException();
			}
			return lancamentoSalvo;
		}	
		
}
