package com.example.algamoney.api.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.dto.LancamentoEstatisticaPessoa;
import com.example.algamoney.api.mail.Mailer;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.repository.UsuarioRepository;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
		
		
		// GERADOR DE RELATÓRIO
		
		public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws Exception {
			List<LancamentoEstatisticaPessoa> dados = lancamentoRepository.porPessoa(inicio, fim);
			
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("DT_INICIO", Date.valueOf(inicio));
			parametros.put("DT_FIM", Date.valueOf(fim));
			parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
			
			InputStream inputStream = this.getClass().getResourceAsStream(
					"/relatorios/lancamentos-por-pessoa.jasper");
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros,
					new JRBeanCollectionDataSource(dados));
			
			return JasperExportManager.exportReportToPdf(jasperPrint);
		}
		
		
		
		// Envia Email
		// 22.15 & 22.16 & 22.21 & 22.22
		
		@Autowired
		private UsuarioRepository usuarioRepository;
		
		private static final String DESTINATARIOS  = "ROLE_PESQUISAR_LANCAMENTO";
	
		@Autowired
		private Mailer mailer;
		
		//22.22
		private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);
		
		
		//@Scheduled(fixedDelay = 1000 * 60 * 30)
		@Scheduled(cron = "0 0 6 * * *")
		public void avisarSobreLancamentosVencidos() {
			if (logger.isDebugEnabled()) {
				logger.debug("Preparando envio de "
						+ "e-mails de aviso de lançamentos vencidos.");
			}
			
			List<Lancamento> vencidos = lancamentoRepository
					.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
			
			if (vencidos.isEmpty()) {
				logger.info("Sem lançamentos vencidos para aviso.");
				
				return;
			}
			
			logger.info("Exitem {} lançamentos vencidos.", vencidos.size());
			
			List<Usuario> destinatarios = usuarioRepository
					.findByPermissoesDescricao(DESTINATARIOS);
			
			if (destinatarios.isEmpty()) {
				logger.warn("Existem lançamentos vencidos, mas o "
						+ "sistema não encontrou destinatários.");
				
				return;
			}
			
			mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);
			
			logger.info("Envio de e-mail de aviso concluído."); 
		}
		
		
		
}
