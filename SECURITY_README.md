# Sistema de Autenticação e Autorização - Ayzis 2.0

## Visão Geral
Esta aplicação implementa um sistema completo de autenticação e autorização usando Spring Security e JWT (JSON Web Tokens).

## Funcionalidades Implementadas

### 🔐 Autenticação
- Login com email/senha
- Registro de novos usuários
- Tokens JWT para autenticação stateless
- Verificação de status de autenticação

### 👥 Sistema de Usuários
- Três níveis de permissão: USER, MANAGER, ADMIN
- Gerenciamento completo de usuários
- Perfil do usuário autenticado
- Alteração de senha

### 🛡️ Autorização
- Proteção de endpoints por role
- Controle de acesso granular
- Middleware JWT automático

## Estrutura de Roles

### USER (Usuário Padrão)
- Acesso de leitura aos dados de componentes, produtos e vendas
- Pode atualizar seu próprio perfil
- Pode alterar sua própria senha

### MANAGER (Gerente)
- Tudo que USER pode fazer
- Pode criar e editar componentes, produtos e vendas
- Pode visualizar lista de usuários
- Pode acessar estatísticas básicas

### ADMIN (Administrador)
- Acesso total ao sistema
- Pode deletar registros
- Pode gerenciar usuários (criar, editar, desativar, alterar roles)
- Pode acessar todas as estatísticas
- Pode realizar todas as operações administrativas

## Endpoints da API

### 🔑 Autenticação (`/api/auth`)

#### POST `/api/auth/register`
Registra um novo usuário
```json
{
  "nome": "João Silva",
  "email": "joao@exemplo.com",
  "senha": "senha123",
  "confirmarSenha": "senha123",
  "role": "USER"
}
```

#### POST `/api/auth/login`
Realiza login
```json
{
  "email": "joao@exemplo.com",
  "senha": "senha123"
}
```

#### GET `/api/auth/me`
Retorna dados do usuário autenticado

#### GET `/api/auth/check`
Verifica se o usuário está autenticado

#### POST `/api/auth/logout`
Realiza logout (limpa contexto)

#### GET `/api/auth/check-email?email=teste@teste.com`
Verifica se email já está em uso

### 👤 Gerenciamento de Usuários (`/api/usuarios`)

#### GET `/api/usuarios` (ADMIN/MANAGER)
Lista todos os usuários

#### GET `/api/usuarios/ativos` (ADMIN/MANAGER)
Lista usuários ativos

#### GET `/api/usuarios/{id}` (ADMIN/MANAGER)
Busca usuário por ID

#### GET `/api/usuarios/buscar?nome=João` (ADMIN/MANAGER)
Busca usuários por nome

#### GET `/api/usuarios/role/{role}` (ADMIN)
Lista usuários por role

#### GET `/api/usuarios/me`
Dados do usuário autenticado

#### PUT `/api/usuarios/me`
Atualiza dados do usuário autenticado

#### PUT `/api/usuarios/me/senha`
Altera senha do usuário autenticado
```json
{
  "senhaAtual": "senhaAntiga",
  "novaSenha": "novaSenha123"
}
```

#### PUT `/api/usuarios/{id}` (ADMIN)
Atualiza usuário por ID

#### PUT `/api/usuarios/{id}/status` (ADMIN)
Altera status ativo/inativo
```json
{
  "ativo": true
}
```

#### PUT `/api/usuarios/{id}/role` (ADMIN)
Altera role do usuário
```json
{
  "role": "MANAGER"
}
```

#### DELETE `/api/usuarios/{id}` (ADMIN)
Deleta usuário

#### GET `/api/usuarios/admin/estatisticas` (ADMIN)
Estatísticas de usuários

## Configuração Inicial

### Usuários Padrão
O sistema cria automaticamente os seguintes usuários para teste:

1. **Administrador**
   - Email: `admin@ayzis.com`
   - Senha: `admin123`
   - Role: ADMIN

2. **Manager**
   - Email: `manager@ayzis.com`
   - Senha: `manager123`
   - Role: MANAGER

3. **Usuário**
   - Email: `usuario@ayzis.com`
   - Senha: `usuario123`
   - Role: USER

⚠️ **IMPORTANTE**: Altere essas senhas padrão em produção!

## Como Usar

### 1. Autenticação
```javascript
// Login
const response = await fetch('/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    email: 'admin@ayzis.com',
    senha: 'admin123'
  })
});

const data = await response.json();
const token = data.token;
```

### 2. Fazendo Requisições Autenticadas
```javascript
// Usar o token em requisições subsequentes
const response = await fetch('/api/usuarios', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});
```

### 3. Verificar Autenticação
```javascript
const response = await fetch('/api/auth/check', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

## Proteção dos Endpoints Existentes

### Componentes (`/api/v1/componentes`)
- **GET**: Requer autenticação (USER, MANAGER ou ADMIN)
- **POST**: Requer MANAGER ou ADMIN 
- **PATCH**: Requer MANAGER ou ADMIN
- **DELETE**: Requer ADMIN

### Produtos (`/api/v1/produtos`)
- **GET**: Requer autenticação (USER, MANAGER ou ADMIN)
- **POST**: Requer MANAGER ou ADMIN
- **POST** `/mass`: Requer MANAGER ou ADMIN
- **PATCH**: Requer MANAGER ou ADMIN
- **DELETE**: Requer ADMIN

### Vendas (`/api/v1/vendas`)
- **GET**: Requer autenticação (USER, MANAGER ou ADMIN)
- **POST**: Requer MANAGER ou ADMIN
- **POST** `/mass`: Requer MANAGER ou ADMIN
- **PATCH**: Requer MANAGER ou ADMIN
- **DELETE**: Requer ADMIN

### Estatísticas (`/api/v1/estatisticas`)
- **GET** `/soma-qtd`: Requer autenticação (USER, MANAGER ou ADMIN)
- **GET** `/soma-valores`: Requer autenticação (USER, MANAGER ou ADMIN)
- **GET** `/soma-qtd-produtos`: Requer autenticação (USER, MANAGER ou ADMIN)
- **GET** `/soma-valores-produtos`: Requer autenticação (USER, MANAGER ou ADMIN)

## Configurações do JWT

As configurações do JWT podem ser alteradas no `application.properties`:

```properties
# Chave secreta (deve ter pelo menos 256 bits)
jwt.secret=MySecretKeyForJWTTokenGenerationThatNeedsToBeAtLeast256BitsLongForSecurity2024

# Tempo de expiração em milissegundos (24 horas)
jwt.expiration=86400000

# Tempo de expiração do refresh token (7 dias)
jwt.refresh-expiration=604800000
```

## CORS Configuration

O sistema está configurado para aceitar requisições dos seguintes origins:
- `http://localhost:3000`
- `http://localhost:3001`
- `http://127.0.0.1:3000`
- `http://127.0.0.1:3001`
- `https://*.vercel.app`
- `https://*.netlify.app`

## Tratamento de Erros

### Códigos de Status HTTP
- `200`: Sucesso
- `201`: Criado (registro)
- `400`: Erro de validação/dados inválidos
- `401`: Não autenticado
- `403`: Não autorizado (sem permissão)
- `404`: Recurso não encontrado
- `500`: Erro interno do servidor

### Formato de Resposta de Erro
```json
{
  "error": true,
  "message": "Descrição do erro",
  "timestamp": 1640995200000
}
```

## Segurança

### Boas Práticas Implementadas
- Senhas criptografadas com BCrypt
- Tokens JWT assinados
- Validação de entrada de dados
- Proteção CORS configurada
- Sessões stateless
- Logs de segurança

### Recomendações para Produção
1. Use HTTPS sempre
2. Configure um `jwt.secret` forte e único
3. Monitore logs de tentativas de login
4. Implemente rate limiting
5. Configure backup regular do banco de dados
6. Altere senhas padrão

## Testando a API

Use ferramentas como Postman, Insomnia ou curl para testar os endpoints:

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ayzis.com","senha":"admin123"}'

# Usar token retornado
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

## Integração com Frontend

O sistema está pronto para integração com aplicações React, Vue, Angular ou qualquer outro frontend. Certifique-se de:

1. Armazenar o token de forma segura (localStorage/sessionStorage)
2. Incluir o token no header `Authorization`
3. Tratar respostas 401/403 adequadamente
4. Implementar logout que remove o token
5. Renovar tokens antes da expiração (se implementado)

---

Esta implementação fornece uma base sólida e segura para autenticação e autorização em sua aplicação Ayzis 2.0.
