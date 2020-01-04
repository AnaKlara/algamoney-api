package com.example.algamoney.api.model;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Embeddable 
public class Endereco {

	@NotNull 
	@Size(min = 3, max = 50)
	private String  logradouro;
	
	@NotNull 
	@Size(min = 3, max = 20)
	private Integer numero ;
	
	@NotNull 
	@Size(min = 3, max = 50)
	private String complemento;
	
	@NotNull 
	@Size(min = 3, max = 20)
	private String  bairro;
	
	@NotNull 
	@Size(min = 3, max = 20)
	private String cep;
	
	@NotNull
	@Size(min = 3, max = 20)
	private String cidade;
	
	@NotNull
	@Size(min = 3, max = 20)
	private String estado;

	
	//SETTERS AND GETTERS
	


	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
}
