package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;
import com.example.algamoney.api.config.properties.AlgamoneyApiProperty;

@Configuration
public class S3Config {
	
	@Autowired
	private AlgamoneyApiProperty property;
	
	
	/* A anotação Bean diz pro Spring que quer criar esse objeto e deixar ele disponível para outras classes utilizarem ele como dependência, por exemplo.
	 * 22.32
	 * */
	@Bean
	public AmazonS3 amazonS3() {
		
		AWSCredentials credenciais = new BasicAWSCredentials(property.gets3().getAccessKeyId(), property.gets3().getSecretAccessKey());
		
		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
														.withCredentials(new AWSStaticCredentialsProvider(credenciais))
														.build();
		/*Cria o bucket se ele não existir;
		 * Já que podemos ter arquivos temporários, criamos uma regra que determina quanto tempo um arquivo pode ser temporário;		 * 
		 * */
		if (!amazonS3.doesBucketExistV2(property.gets3().getBucket())) {
			amazonS3.createBucket(
					new CreateBucketRequest(property.gets3().getBucket()));
			
			BucketLifecycleConfiguration.Rule regraExpiracao = 
					new BucketLifecycleConfiguration.Rule()
					.withId("Regra de expiração de arquivos temporários")
					.withFilter(new LifecycleFilter(
							new LifecycleTagPredicate(new Tag("expirar", "true"))))
					.withExpirationInDays(1) // 1 dia pra um arquivo temporário expirar
					.withStatus(BucketLifecycleConfiguration.ENABLED);
			
			// Aqui associamos a regra de expiração com o bucket criado
			BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration()
					.withRules(regraExpiracao);
			
			amazonS3.setBucketLifecycleConfiguration(property.gets3().getBucket(), 
					configuration);
		}
			
		return amazonS3;
	}
	
}
