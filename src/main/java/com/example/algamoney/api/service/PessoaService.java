package com.example.algamoney.api.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;

@Service
public class PessoaService {  
	
	@Autowired
	private static PessoaRepository pessoaRepository;
	
	  public static Pessoa atualizar(Long codigo, Pessoa pessoa) {
			Optional<Pessoa> pessoaSalva = pessoaRepository.findById(codigo);
			
			if (!pessoaSalva.isPresent()) {
				throw new EmptyResultDataAccessException(1);
			}
			
			BeanUtils.copyProperties(pessoa, pessoaSalva,"codigo"); 
			pessoa.setCodigo(codigo);
	  return pessoaRepository.save(pessoa);
	  }

}

/*
* Foi adicionada regra que trata diretamente de inserção/atualização de dados ao banco, 
* coisa que o Repository já trata. Porém foi criado uma classe de Service reoacionada para fazer esta tarefa de atualização.
*
*Esta regra poderia ser adicionada ao Repository? Estaria seguindo a recomendação de uso dos padrões em questão?
* 
*
* A diferença do método do nosso serviço, é que se caso a entidade Pessoa que queremos atualizar não existir, uma Exception será lançada. 
* Isso é uma regra de negócio que implementamos, por isso faz sentido deixarmos em um serviço. 
* No Repository que temos, é feito essa tratativa, porém caso a Pessoa que queremos atualizar não exista, ela será inserida como nova, o que foge da regra que implementamos.
*
*/