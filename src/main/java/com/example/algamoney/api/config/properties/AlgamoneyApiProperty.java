package com.example.algamoney.api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;


// 7.2    --> 
// 22.16  --> 
// 22.30 --> S3
// 22.31 --> Bucket
@ConfigurationProperties("algamoney")
public class AlgamoneyApiProperty {

	private String originPermitida = "https://algam-angular.herokuapp.com";

	private final Seguranca seguranca = new Seguranca();
	
	private final S3 s3 = new S3();
	
	
	
	public String getOriginPermitida() {
		return originPermitida;
	}

	public void setOriginPermitida(String originPermitida) {
		this.originPermitida = originPermitida;
	}

	public Seguranca getSeguranca() {
		return seguranca;
	}

	public S3 gets3() {
		return s3;
	}
	
	
	//  ----------------------------
	//  ------- Security
	//  ----------------------------
	
	public static class Seguranca {

		private boolean enableHttps;

		public boolean isEnableHttps() {
			return enableHttps;
		}

		public void setEnableHttps(boolean enableHttps) {
			this.enableHttps = enableHttps;
		}

	}
	
	
	//  ----------------------------
	//  ------- Correio (Mail)
	//  ----------------------------
	private final Mail mail = new Mail();
	
	public Mail getMail() {
		return mail;
	}	
	
	public static class Mail {		
		private String host;		
		private Integer port;		
		private String username; // usuario e senha do email		
		private String password;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
	
	//  ----------------------------
	//  ------- Amazon S3
	//  ----------------------------
	
	public static class S3 {
		
		private String accessKeyId;
		private String secretAccessKey;
		
		private String bucket;
		
		
		public String getAccessKeyId() {
			return accessKeyId;
		}
		public void setAccessKeyId(String accessKeyId) {
			this.accessKeyId = accessKeyId;
		}
		public String getSecretAccessKey() {
			return secretAccessKey;
		}
		public void setSecretAccessKey(String secretAccessKey) {
			this.secretAccessKey = secretAccessKey;
		}
		public String getBucket() {
			return bucket;
		}
		public void setBucket(String bucket) {
			this.bucket = bucket;
		}
		
	}

	
	
}