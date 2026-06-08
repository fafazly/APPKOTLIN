# 🧪 GUIA DE TESTES DA API

## ▶️ Iniciar a API

```bash
cd C:\Users\RIAN.NOTEFAFAZLY\IdeaProjects\KotlinApp
./gradlew run
```

Esperado: API iniciará em `http://localhost:8080`

---

## 📝 Endpoints de Teste

### 1️⃣ Health Check (Teste Rápido)

```bash
curl -X GET http://localhost:8080/health
```

**Resposta Esperada:**
```json
{
  "success": true,
  "message": "API está funcionando"
}
```

---

### 2️⃣ Registrar Novo Usuário

```bash
curl -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@test.com",
    "password": "senha123456",
    "name": "Usuário Teste"
  }'
```

**Resposta Esperada:**
```json
{
  "success": true,
  "message": "Usuário cadastrado com sucesso",
  "data": {
    "id": "1",
    "email": "usuario@test.com",
    "name": "Usuário Teste"
  }
}
```

---

### 3️⃣ Login

```bash
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@test.com",
    "password": "senha123456"
  }'
```

**Resposta Esperada:**
```json
{
  "success": true,
  "message": "Login realizado com sucesso",
  "data": {
    "id": "1",
    "email": "usuario@test.com",
    "name": "Usuário Teste",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": "10 horas"
  }
}
```

**⚠️ IMPORTANTE:** Copie o `token` para usar nos próximos testes!

---

### 4️⃣ Obter Perfil (Protegido com JWT)

```bash
curl -X GET http://localhost:8080/users/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Substitua:** `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...` pelo token obtido no login

**Resposta Esperada:**
```json
{
  "success": true,
  "message": "Perfil obtido com sucesso",
  "data": {
    "id": "1",
    "email": "usuario@test.com",
    "name": "Usuário Teste"
  }
}
```

---

### 5️⃣ Atualizar Perfil (Protegido com JWT)

```bash
curl -X PUT http://localhost:8080/users/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_AQUI" \
  -d '{
    "name": "Novo Nome"
  }'
```

**Resposta Esperada:**
```json
{
  "success": true,
  "message": "Perfil atualizado com sucesso",
  "data": {
    "email": "usuario@test.com",
    "name": "Novo Nome"
  }
}
```

---

### 6️⃣ Alterar Senha (Protegido com JWT)

```bash
curl -X POST http://localhost:8080/users/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_AQUI" \
  -d '{
    "currentPassword": "senha123456",
    "newPassword": "novaSenha654321"
  }'
```

**Resposta Esperada:**
```json
{
  "success": true,
  "message": "Senha alterada com sucesso"
}
```

---

### 7️⃣ Listar Usuários (Protegido com JWT)

```bash
curl -X GET http://localhost:8080/users/list \
  -H "Authorization: Bearer TOKEN_AQUI"
```

**Resposta Esperada:**
```json
{
  "success": true,
  "message": "Lista de usuários obtida com sucesso",
  "count": 1,
  "data": [
    {
      "id": "1",
      "email": "usuario@test.com",
      "name": "Novo Nome"
    }
  ]
}
```

---

## 🔐 Testes de Segurança

### ❌ Erro: Registrar com Email Duplicado

```bash
curl -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@test.com",
    "password": "senha123456",
    "name": "Outro Usuário"
  }'
```

**Resposta Esperada (HTTP 409):**
```json
{
  "success": false,
  "message": "Este email já está cadastrado"
}
```

---

### ❌ Erro: Login com Senha Errada

```bash
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@test.com",
    "password": "senhaErrada"
  }'
```

**Resposta Esperada (HTTP 401):**
```json
{
  "success": false,
  "message": "Email ou senha incorretos"
}
```

---

### ❌ Erro: Acessar Rota Protegida sem JWT

```bash
curl -X GET http://localhost:8080/users/profile
```

**Resposta Esperada (HTTP 401):**
```json
{
  "success": false,
  "message": "Token inválido ou expirado"
}
```

---

### ❌ Erro: JWT Inválido

```bash
curl -X GET http://localhost:8080/users/profile \
  -H "Authorization: Bearer invalid_token_here"
```

**Resposta Esperada (HTTP 401):**
```json
{
  "success": false,
  "message": "Token inválido ou expirado"
}
```

---

## 📊 Checklist de Testes

Copie e execute cada teste em ordem:

```
☐ 1. Health Check
☐ 2. Registrar Usuário
☐ 3. Login
☐ 4. Obter Perfil (com JWT)
☐ 5. Atualizar Perfil (com JWT)
☐ 6. Alterar Senha (com JWT)
☐ 7. Listar Usuários (com JWT)
☐ 8. Registrar Email Duplicado (DEVE FALHAR)
☐ 9. Login Senha Errada (DEVE FALHAR)
☐ 10. Acesso sem JWT (DEVE FALHAR)
☐ 11. JWT Inválido (DEVE FALHAR)
```

---

## 🛠️ Ferramentas Recomendadas para Teste

### Opção 1: Postman (UI)
1. Download: https://www.postman.com/downloads/
2. Criar nova requisição
3. Copiar os testes acima

### Opção 2: curl (Terminal/PowerShell)
- Já instalado no Windows
- Execute os comandos acima

### Opção 3: REST Client (VS Code)
1. Instalar extensão: REST Client
2. Criar arquivo `test.rest`
3. Copiar os testes

### Opção 4: Insomnia (alternativa ao Postman)
1. Download: https://insomnia.rest/
2. Interface similar ao Postman

---

## 📄 Formato das Respostas

### Resposta de Sucesso (HTTP 200)
```json
{
  "success": true,
  "message": "Descrição do sucesso",
  "data": {
    "campo1": "valor1",
    "campo2": "valor2"
  }
}
```

### Resposta de Erro (HTTP 4xx/5xx)
```json
{
  "success": false,
  "message": "Descrição do erro"
}
```

---

## 🔍 Debugging

### Se receber erro de conexão:
```bash
# Verificar se a API está rodando
netstat -ano | findstr :8080

# Verificar logs do Gradle
./gradlew run
```

### Se receber erro de banco de dados:
```sql
-- Verificar conexão MySQL
mysql -u root -p
SHOW DATABASES;
USE appkotlin;
SHOW TABLES;
```

### Se receber erro de compilação:
```bash
./gradlew clean build
```

---

## 📝 Variáveis Úteis

Para facilitar os testes, guarde estes valores:

```
URL_BASE: http://localhost:8080

Usuário de Teste:
  email: usuario@test.com
  password: senha123456
  name: Usuário Teste

Token JWT: (obtido após login)
  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## ✅ Resultado Esperado

Se todos os testes passarem:
- ✅ API respondendo corretamente
- ✅ Banco de dados funcionando
- ✅ Autenticação JWT implementada
- ✅ Validações funcionando
- ✅ Hashing de senha funcionando

**Parabéns! 🎉 Sua API está funcional!**

---

## 🆘 Problemas Comuns

### Problema: "Connection refused"
**Solução:** API não está rodando. Execute `./gradlew run`

### Problema: "Unresolved reference"
**Solução:** Execute `./gradlew clean build` primeiro

### Problema: "Access denied for user 'root'@'localhost'"
**Solução:** Verificar credenciais em `config/DatabaseConfig.kt`

### Problema: "JDBC Driver not found"
**Solução:** As dependências devem estar corretas. Execute `./gradlew build`

---

**Happy Testing! 🧪**

