package com.example.algamoney.api.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.util.StringUtils;

import com.example.algamoney.api.AlgamoneyApiApplication;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.storage.S3;

/* 22.36
 * Como o responsável pela instanciação dessa classe não 'o spring, não podemos utilizar os recursos de injeção de dependências
 * 
 * Para contornar isso importaremos o Aplication context 
 * (ver AlgamoneyApiApplication.class )
 */
public class LancamentoAnexoListener {
	
	@PostLoad // Após o carregamento
	public void postload(Lancamento lancamento) {
		
		if (StringUtils.hasText(lancamento.getAnexo()) ) {
			S3 s3 = AlgamoneyApiApplication.getBean(S3.class);
			lancamento.setUrlAnexo(s3.configurarUrl(lancamento.getAnexo()));
		}
	}

}
