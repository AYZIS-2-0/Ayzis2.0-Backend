package com.trust.ayzis.ayzis.repository;

import com.trust.ayzis.ayzis.model.BlingAccount;
import com.trust.ayzis.ayzis.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBlingAccountRepository extends JpaRepository<BlingAccount, Long> {

    List<BlingAccount> findByUsuarioAndIsActiveTrue(Usuario usuario);

    Optional<BlingAccount> findByIdAndUsuario(Long id, Usuario usuario);

    @Query("SELECT ba FROM BlingAccount ba WHERE ba.usuario = :usuario AND ba.accountName = :accountName AND ba.isActive = true")
    Optional<BlingAccount> findByUsuarioAndAccountNameAndIsActiveTrue(@Param("usuario") Usuario usuario,
            @Param("accountName") String accountName);

    @Query("SELECT ba FROM BlingAccount ba WHERE ba.tokenExpiresAt < CURRENT_TIMESTAMP AND ba.isActive = true")
    List<BlingAccount> findExpiredTokens();
}