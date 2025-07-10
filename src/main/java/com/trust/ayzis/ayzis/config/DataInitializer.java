package com.trust.ayzis.ayzis.config;

import com.trust.ayzis.ayzis.model.IUsuarioRepository;
import com.trust.ayzis.ayzis.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        try {
            // Verificar se já existe um administrador
            long adminCount = usuarioRepository.countByRole(Usuario.Role.ADMIN);

            if (adminCount == 0) {
                logger.info("Nenhum administrador encontrado. Criando usuário administrador padrão...");

                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setEmail("admin@ayzis.com");
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setRole(Usuario.Role.ADMIN);
                admin.setAtivo(true);

                usuarioRepository.save(admin);

                logger.info("Usuário administrador criado com sucesso!");
                logger.info("Email: admin@ayzis.com");
                logger.info("Senha: admin123");
                logger.warn("ATENÇÃO: Altere a senha padrão do administrador após o primeiro login!");

            } else {
                logger.info("Administrador já existe no sistema.");
            }

            // Criar usuário de teste se não existir
            if (!usuarioRepository.existsByEmail("usuario@ayzis.com")) {
                logger.info("Criando usuário de teste...");

                Usuario usuario = new Usuario();
                usuario.setNome("Usuário Teste");
                usuario.setEmail("usuario@ayzis.com");
                usuario.setSenha(passwordEncoder.encode("usuario123"));
                usuario.setRole(Usuario.Role.USER);
                usuario.setAtivo(true);

                usuarioRepository.save(usuario);

                logger.info("Usuário de teste criado!");
                logger.info("Email: usuario@ayzis.com");
                logger.info("Senha: usuario123");
            }

            // Criar manager de teste se não existir
            if (!usuarioRepository.existsByEmail("manager@ayzis.com")) {
                logger.info("Criando manager de teste...");

                Usuario manager = new Usuario();
                manager.setNome("Manager Teste");
                manager.setEmail("manager@ayzis.com");
                manager.setSenha(passwordEncoder.encode("manager123"));
                manager.setRole(Usuario.Role.MANAGER);
                manager.setAtivo(true);

                usuarioRepository.save(manager);

                logger.info("Manager de teste criado!");
                logger.info("Email: manager@ayzis.com");
                logger.info("Senha: manager123");
            }

        } catch (Exception e) {
            logger.error("Erro ao inicializar dados: {}", e.getMessage(), e);
        }
    }
}
