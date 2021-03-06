package com.example.algamoney.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.example.algamoney.api.repository.listener.LancamentoAnexoListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@EntityListeners(LancamentoAnexoListener.class) // 22.36
@Entity
@Table(name = "lancamento")
public class Lancamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	@NotNull //validação com BeanValidation --> @Valid
	private String descricao;
	
	@NotNull //validação com BeanValidation --> @Valid
	@Column(name = "data_vencimento")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataVencimento;
	
	@Column(name = "data_pagamento")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataPagamento;
	
	@NotNull //validação com BeanValidation --> @Valid
	private BigDecimal valor;

	private String observacao;
	
	@NotNull //validação com BeanValidation --> @Valid
	@Enumerated(EnumType.STRING)
	private TipoLancamento tipo;

	@NotNull //validação com BeanValidation --> @Valid
	@ManyToOne
	@JoinColumn(name = "codigo_categoria")
	private Categoria categoria;

	@JsonIgnoreProperties("contatos")
	@NotNull //validação com BeanValidation --> @Valid
	@ManyToOne
	@JoinColumn(name = "codigo_pessoa")
	private Pessoa pessoa;
	
	// 22.34
	//está sem anotação pois ela não é uma propriedade obrigatória
	private String anexo;
	
	@Transient //não vai persistir no banco
	private String urlAnexo;



	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDate getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(LocalDate dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public LocalDate getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(LocalDate dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public TipoLancamento getTipo() {
		return tipo;
	}

	public void setTipo(TipoLancamento tipo) {
		this.tipo = tipo;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lancamento other = (Lancamento) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
	// 22.21
	@JsonIgnore
	public boolean isReceita() {
		return TipoLancamento.RECEITA.equals(tipo);		
	}
	
	// 22.34
	public String getAnexo() {
		return anexo;
	}

	public void setAnexo(String anexo) {
		this.anexo = anexo;
	}
	
	public void setUrlAnexo(String urlAnexo) {
		this.urlAnexo = urlAnexo;
	}
}