package com.trust.ayzis.ayzis.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.trust.ayzis.ayzis.model.BlingAccount;
import com.trust.ayzis.ayzis.repository.IBlingAccountRepository;

@Service
public class BlingTokenScheduler {

    Logger logger = LogManager.getLogger(getClass());

    @Autowired
    private IBlingAccountRepository blingAccountRepository;

    @Autowired
    private BlingService blingService;

    @Scheduled(fixedRate = 3600000)
    public void renovarTokensExpirados() {
        logger.info("Verificando tokens expirados para renovação...");

        try {
            List<BlingAccount> expiredAccounts = blingAccountRepository.findExpiredTokens();

            for (BlingAccount account : expiredAccounts) {
                try {
                    if (account.getRefreshToken() != null) {
                        logger.info("Renovando token para a conta: " + account.getId());
                        blingService.atualizarToken(account);
                    } else {
                        logger.warn("Conta sem refresh token: " + account.getId());
                    }
                } catch (Exception e) {
                    logger.error("Erro ao renovar token para a conta {}: {}", account.getId(), e.getMessage());
                }
                if (expiredAccounts.isEmpty()) {
                    logger.info("Nenhum token expirado encontrado.");
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao verificar tokens expirados: {}", e.getMessage());
        }
    }
}