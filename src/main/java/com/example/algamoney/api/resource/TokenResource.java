package com.example.algamoney.api.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.config.properties.AlgamoneyApiProperty;

@RestController
@RequestMapping("/tokens")
public class TokenResource {
	
	@Autowired //7.2
	private AlgamoneyApiProperty algamoneyApiProperty;
	
	@DeleteMapping("/revoke")
	public void revoke(HttpServletRequest req, HttpServletResponse resp) {
		
		Cookie cookie = new Cookie("refreshToken", null); //o conteúdo do cookie será null
		cookie.setHttpOnly(true);
		cookie.setSecure(  algamoneyApiProperty.getSeguranca().isEnableHttps()  );// em produção será true  cookie.setSecure(false);
		cookie.setPath(req.getContextPath() + "/oauth/token");
		cookie.setMaxAge(0);
		
		resp.addCookie(cookie);
		resp.setStatus(HttpStatus.NO_CONTENT.value());		
	}

}
