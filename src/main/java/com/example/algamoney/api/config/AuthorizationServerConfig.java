package com.example.algamoney.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.example.algamoney.api.config.token.CustonTokenEnahncer;

@Profile("oauth-security")//7.6
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient("angular")
				.secret("$2a$10$TZUyFkp1X2F2ymKdey/atu44n8gSyIRLDmg6ly5Q.E5ETFxmz2jM2")//@ngul@r0 encodado com BCrypt
				.scopes("read", "write")// é possível cadastrar diferentes clientes, cada um com escopos diferentes (6:12) --> é um gerenciador de permissões a nível de aplicação
				.authorizedGrantTypes("password", "refresh_token")//6.6
				.accessTokenValiditySeconds(1800) // 1800
				.refreshTokenValiditySeconds(3600 * 24); //3600 * 24
	}

	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		//7.5 : Aqui podemos acrescentar dados que queremos que sejam transportados no token
		TokenEnhancerChain tokenEnhanceChain = new TokenEnhancerChain(); 
		tokenEnhanceChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter() ));
		
		
		endpoints
			.tokenStore(tokenStore())
			.tokenEnhancer(tokenEnhanceChain)
			.reuseRefreshTokens(false)
			.authenticationManager(authenticationManager);
	}

	@Bean //7.5
	public TokenEnhancer tokenEnhancer() {
		return new CustonTokenEnahncer();
	}


	@Bean // quem precisar desse método recupera através do Bean
	public JwtAccessTokenConverter accessTokenConverter() { // setar a chave que valida o token
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("algaworks");
		return accessTokenConverter;
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter()); 
	}
}