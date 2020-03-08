package com.example.algamoney.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


//6.7
@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> { //<> Aqui fica o tipo do dado que eu quero interceptar 6.7
//antes de responder com o Acess Token, ele irá processar a requisição para que na resposta o refresh token seja retirado do header e colocado no cookie
	
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) { 
		// 6.7 existem várias ocasiões em que a req contém o tipo de dado <OAuth2AccessToken>, não queremos interceptar todas, mas apenas aquelas
		// que satisfazem as condições abaixo:
		//Quando o nome do método for "postAccessToken"
		return returnType.getMethod().getName().equals("postAccessToken");
	}

	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		
		//Casting
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
		
		//Casting
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;
		
		String refreshToken = body.getRefreshToken().getValue();
		adicionarRefreshTokenNoCookie(refreshToken, req, resp);
		removerRefreshTokenDoBody(token);
		
		return body;
	}


	private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);//cria o cookie
		refreshTokenCookie.setHttpOnly(true);//só é acessível com HTTPs
		refreshTokenCookie.setSecure(false); // TODO: Mudar para true em producao
		refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token");//pra qual caminho o cookie deve ser enviado pelo browser?
		refreshTokenCookie.setMaxAge(2592000);//em quanto tempo esse cookie deve expirar em dias?
		resp.addCookie(refreshTokenCookie);
	}
	
	
	private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
		token.setRefreshToken(null);
	}
}