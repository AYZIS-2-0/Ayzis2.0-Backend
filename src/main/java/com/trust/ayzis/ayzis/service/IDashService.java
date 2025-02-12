package com.trust.ayzis.ayzis.service;

import java.util.List;
import java.util.Optional;

import com.trust.ayzis.ayzis.dto.DashboardDTO;
import com.trust.ayzis.ayzis.dto.ProdutoMesDTO;
import com.trust.ayzis.ayzis.model.ProdInfo;

public interface IDashService {
    public List<ProdutoMesDTO> getDashboardData();
}
