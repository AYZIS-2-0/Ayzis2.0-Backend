package com.trust.ayzis.ayzis.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.trust.ayzis.ayzis.model.Componentes;
import com.trust.ayzis.ayzis.model.IProdutoRepository;
import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Venda;

@Configuration
public class LoadDatabase {
    Logger logger = LogManager.getLogger(this.getClass());

    @Bean
    CommandLineRunner initDatabase(
            IProdutoRepository produtoRepository) {

        return args -> {
            Produto produto1 = new Produto("772040NOVA","BANDEJA NOVA DIANTEIRA DIREITA HILUX 2005 A 2015 COM PIVÔ", null, "FORTIKIT", "Produto", "Novo",664.58, 20.0, 10.0, 20.0, 350, "Unidade", false, null);
            Produto produto2 = new Produto("772041NOVA","BANDEJA NOVA DIANTEIRA ESQUERDA HILUX 2005 A 2015 COM PIVÔ", null, "FORTIKIT", "Produto", "Novo",664.58, 20.0, 10.0, 20.0, 350, "Unidade", false, null);
            Produto produto3 = new Produto("772040/41NOVA", "PAR BANDEJA NOVA DIANTEIRA HILUX 2005 A 2015 COM PIVÔ",
                    null, "FORTIKIT", "Produto", "Novo", 1169.23, 20.0, 10.0, 20.0, 700.0, "Par", false, null);

            Venda venda1 = new Venda("1", Date.valueOf("2021-01-01"), "Pendente", "Venda pendente", 1, 664.58, "Mercado Livre",produto1);
            Venda venda2 = new Venda("2", Date.valueOf("2021-02-01"), "Pendente", "Venda pendente", 1, 664.58, "Mercado Livre",produto2);
            Venda venda3 = new Venda("3", Date.valueOf("2021-01-02"), "Pendente", "Venda pendente", 1, 1169.23, "Mercado Livre", produto3);
            Venda venda4 = new Venda("4", Date.valueOf("2021-02-02"), "Pendente", "Venda pendente", 1, 1169.23, "Mercado Livre", produto3);
            
            Componentes produtoComposicao1 = new Componentes(produto3, produto1, 1);
            Componentes produtoComposicao2 = new Componentes(produto3, produto2, 1);
            
            produto3.setProdutosComposicao(List.of(produtoComposicao1, produtoComposicao2));
            
            produto1.setVendas(List.of(venda1));
            produto2.setVendas(List.of(venda2));
            produto3.setVendas(List.of(venda3, venda4));

            // Verificar se os produtos já existem no banco de dados
            Optional<Produto> existingProduto1 = produtoRepository.findById(produto1.getId());
            Optional<Produto> existingProduto2 = produtoRepository.findById(produto2.getId());
            Optional<Produto> existingProduto3 = produtoRepository.findById(produto3.getId());

            if (existingProduto1.isEmpty()) {
                produtoRepository.save(produto1);
            }
            if (existingProduto2.isEmpty()) {
                produtoRepository.save(produto2);
            }
            if (existingProduto3.isEmpty()) {
                produtoRepository.save(produto3);
            }

            logger.info("LoadDataBase | Produtos carregados no banco de dados");
        };
    }
}