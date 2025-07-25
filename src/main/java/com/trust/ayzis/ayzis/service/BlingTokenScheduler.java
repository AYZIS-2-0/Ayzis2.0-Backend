package com.trust.ayzis.ayzis.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.trust.ayzis.ayzis.model.BlingAccount;
import com.trust.ayzis.ayzis.repository.IBlingAccountRepository;

public class BlingTokenScheduler {

    Logger logger = LogManager.getLogger(getClass());

    @Autowired
    private IBlingAccountRepository blingAccountRepository;

    @Autowired
    private BlingService blingService;

    @Scheduled(fixedRate = 3600000)
    public void renovarTokensExpirados() {
        logger.info("Verificando tokens expirados para renovação...");

    }
}
