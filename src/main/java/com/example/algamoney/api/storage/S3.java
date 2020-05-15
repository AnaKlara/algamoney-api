package com.example.algamoney.api.storage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;

import com.example.algamoney.api.config.properties.AlgamoneyApiProperty;
import com.example.algamoney.api.model.Lancamento;

@Component
public class S3 {

	@Autowired
	private AmazonS3 amazonS3;
	
	@Autowired
	private AlgamoneyApiProperty property;
	
	private static final Logger logger = LoggerFactory.getLogger(S3.class);
	
	
	
	
	public String salvarTemporariamente(MultipartFile arquivo) {
		/* 1 - Qual a permissão do objeto?
		 * 2 - Quais os metadados do arquivo?
		 * 3 - Qual será o nome único para esse arquivo?*/
		
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read); //Todos os usuários poderão ler
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(arquivo.getContentType());
		objectMetadata.setContentLength(arquivo.getSize());
		
		String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename());
		
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(
					property.gets3().getBucket(),
					nomeUnico,
					arquivo.getInputStream(), 
					objectMetadata)
					.withAccessControlList(acl);
			
			putObjectRequest.setTagging(new ObjectTagging(
					Arrays.asList(new Tag("expirar", "true"))));
			
			amazonS3.putObject(putObjectRequest);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Arquivo {} enviado com sucesso para o S3.", 
						arquivo.getOriginalFilename());
			}
			
			return nomeUnico;
		} catch (IOException e) {
			throw new RuntimeException("Problemas ao tentar enviar o arquivo para o S3.", e);
		}
	}

	private String gerarNomeUnico(String originalFilename) {
		return UUID.randomUUID().toString() + "_" + originalFilename;
	}
	
	public String configurarUrl(String objeto) {
		
		// as barras duplicadas servem para adaptar a URL para qualquer protocolo, pode ser http ou https
		return "\\\\" + property.gets3().getBucket() + ".s3-sa-east-1.amazonaws.com/" + objeto ;
	}

	public void salvar(String objeto) {
		SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(
																			property.gets3().getBucket(),
																			objeto,
																			new ObjectTagging(Collections.emptyList())
																			);
		amazonS3.setObjectTagging(setObjectTaggingRequest);
				
				
		
	}

	// 22.35
	public void remover(String anexo) {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(property.gets3().getBucket(), anexo);
		amazonS3.deleteObject(deleteObjectRequest);
	}

	public void substituir(String anexoAntigo, String anexoNovo) {
	
		if ( StringUtils.hasText(anexoAntigo)) {
			this.remover(anexoAntigo);
		}
		salvar(anexoNovo);
	}

	
}
