package com.example.algamoney.api.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.algamoney.api.config.properties.AlgamoneyApiProperty;


//6.9
//6.10
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)//prioridade alta
public class CorsFilter implements Filter{
	
	
	@Autowired //7.2
	private AlgamoneyApiProperty algamoneyApiProperty;
	
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		//casting
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		response.setHeader("Acess-Control-Allow-Origin", algamoneyApiProperty.getOriginPermitida());
		response.setHeader("Acess-Control-Allow-Credentials", "true");//necessário para o cookie
		
		if ("OPTIONS".contentEquals(request.getMethod()) && algamoneyApiProperty.getOriginPermitida().contentEquals(request.getHeader("Origin"))) {
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
			response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setStatus(HttpServletResponse.SC_OK);
		}else {
			chain.doFilter(req, resp);			
		}
		
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
