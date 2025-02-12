package com.trust.ayzis.ayzis.service;

import java.util.List;
import java.sql.Date;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.trust.ayzis.ayzis.dto.ProdutoMesDTO;
import com.trust.ayzis.ayzis.model.IProdInfoRespository;
import com.trust.ayzis.ayzis.model.IProdutoRepository;
import com.trust.ayzis.ayzis.model.IVendaRepository;
import com.trust.ayzis.ayzis.model.ProdInfo;
import com.trust.ayzis.ayzis.model.Produto;
import com.trust.ayzis.ayzis.model.Venda;

@Service
public class DashService implements IDashService {

    @Autowired
    IProdInfoRespository prodInfoRepository;

    @Autowired
    private IVendaRepository vendaRepository;

    @Autowired
    private IProdutoRepository produtoRepository;

    public List<ProdutoMesDTO> getDashboardData() {
        List<ProdutoMesDTO> dashboardData = new ArrayList<>();
        List<Produto> produtos = produtoRepository.findAll();

        for (Produto produto : produtos) {
            List<Venda> vendas = vendaRepository.findByProduto(produto);

            for (Venda venda : vendas) {
                processVenda(produto, venda, dashboardData);
            }
        }
        return dashboardData;
    }

    private void processVenda(Produto produto, Venda venda, List<ProdutoMesDTO> dashboardData) {
        ProdInfo prodInfo = prodInfoRepository.findByProdutoAndMesAno(produto, venda.getDataVenda())
                .orElseGet(() -> createProdInfo(produto, venda));

        ProdutoMesDTO produtoMesDTO = findOrCreateProdutoMesDTO(produto, venda.getDataVenda(), dashboardData);

        if (produto.getProdutosComposicao().isEmpty()) {
            updateProdInfoForIndividual(prodInfo, venda, produtoMesDTO);
        } else {
            updateProdInfoForComposed(prodInfo, venda, produtoMesDTO);
        }

        updateTotals(prodInfo, produtoMesDTO);
        prodInfoRepository.save(prodInfo);
    }

    private ProdInfo createProdInfo(Produto produto, Venda venda) {
        ProdInfo prodInfo = new ProdInfo();
        prodInfo.setMesAno(venda.getDataVenda());
        prodInfo.setProduto(produto);
        return prodInfo;
    }

    private ProdutoMesDTO findOrCreateProdutoMesDTO(Produto produto, Date mesAno, List<ProdutoMesDTO> dashboardData) {
        for (ProdutoMesDTO dto : dashboardData) {
            if (dto.getProdutoId().equals(produto.getNome()) && dto.getData().equals(mesAno)) {
                return dto;
            }
        }
        ProdutoMesDTO newDto = new ProdutoMesDTO();
        newDto.setProdutoId(produto.getNome());
        newDto.setData(mesAno);
        dashboardData.add(newDto);
        return newDto;
    }

    private void updateProdInfoForIndividual(ProdInfo prodInfo, Venda venda, ProdutoMesDTO produtoMesDTO) {
        if (venda.getOrigem().equals("ML")) {
            updateProdInfoForML(prodInfo, venda, "Individual", produtoMesDTO);
        } else {
            updateProdInfoForDirect(prodInfo, venda, "Individual", produtoMesDTO);
        }
    }

    private void updateProdInfoForComposed(ProdInfo prodInfo, Venda venda, ProdutoMesDTO produtoMesDTO) {
        if (venda.getOrigem().equals("ML")) {
            updateProdInfoForML(prodInfo, venda, "Composta", produtoMesDTO);
        } else {
            updateProdInfoForDirect(prodInfo, venda, "Composta", produtoMesDTO);
        }
    }

    private void updateProdInfoForML(ProdInfo prodInfo, Venda venda, String type, ProdutoMesDTO produtoMesDTO) {
        switch (venda.getStatus()) {
            case "Entregue":
            case "Venda entregue":
                incrementField(prodInfo, "Venda" + type, venda.getQuantidade(), produtoMesDTO);
                break;
            case "Pendente":
                incrementField(prodInfo, "Pendente" + type, venda.getQuantidade(), produtoMesDTO);
                break;
            default:
                incrementField(prodInfo, "Cancelamento" + type, venda.getQuantidade(), produtoMesDTO);
                break;
        }
    }

    private void updateProdInfoForDirect(ProdInfo prodInfo, Venda venda, String type, ProdutoMesDTO produtoMesDTO) {
        switch (venda.getStatus()) {
            case "Entregue":
            case "Venda entregue":
                incrementField(prodInfo, "VendaDireta", venda.getQuantidade(), produtoMesDTO);
                break;
            case "Pendente":
                incrementField(prodInfo, "PendenteDireta", venda.getQuantidade(), produtoMesDTO);
                break;
            default:
                incrementField(prodInfo, "CancelamentoDireta", venda.getQuantidade(), produtoMesDTO);
                break;
        }
    }

    private void incrementField(ProdInfo prodInfo, String fieldName, int quantidade, ProdutoMesDTO produtoMesDTO) {
        try {
            java.lang.reflect.Field field = ProdInfo.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int currentValue = (int) field.get(prodInfo);
            field.set(prodInfo, currentValue + quantidade);

            // Update the corresponding field in ProdutoMesDTO
            java.lang.reflect.Field dtoField = ProdutoMesDTO.class.getDeclaredField(fieldName.toLowerCase());
            dtoField.setAccessible(true);
            int dtoCurrentValue = (int) dtoField.get(produtoMesDTO);
            dtoField.set(produtoMesDTO, dtoCurrentValue + quantidade);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void updateTotals(ProdInfo prodInfo, ProdutoMesDTO produtoMesDTO) {
        prodInfo.setVendaTotal(
                prodInfo.getVendaComposta() + prodInfo.getVendaDireta() + prodInfo.getVendaIndividual());
        prodInfo.setPendenteTotal(prodInfo.getPendenteComposta() + prodInfo.getPendenteDireta()
                + prodInfo.getPendenteIndividual());
        prodInfo.setCancelamentoTotal(prodInfo.getCancelamentoComposta() + prodInfo.getCancelamentoDireta()
                + prodInfo.getCancelamentoIndividual());

        produtoMesDTO.setVendaTotal(
                produtoMesDTO.getVendaComposta() + produtoMesDTO.getVendaDireta() + produtoMesDTO.getVendaIndividual());
        produtoMesDTO.setPendenteTotal(produtoMesDTO.getPendenteComposta() + produtoMesDTO.getPendenteDireta()
                + produtoMesDTO.getPendenteIndividual());
        produtoMesDTO.setCancelamentoTotal(produtoMesDTO.getCancelamentoComposta()
                + produtoMesDTO.getCancelamentoDireta() + produtoMesDTO.getCancelamentoIndividual());
    }
}