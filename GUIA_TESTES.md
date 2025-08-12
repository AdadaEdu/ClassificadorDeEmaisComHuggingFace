# 🧪 Guia Prático de Testes - API de Classificação de E-mails

## 📋 Preparação Inicial

### 1. **Verificar Pré-requisitos**
```bash
# Verificar Java
java -version
# Deve mostrar: openjdk version "17.x.x"

# Verificar Maven
mvn -version
# Deve mostrar: Apache Maven 3.x.x
```

### 2. **Iniciar a Aplicação**
```bash
# Navegar para o diretório do projeto
cd api-classificacao-emails

# Compilar o projeto
mvn clean compile

# Executar a aplicação
mvn spring-boot:run
```

### 3. **Verificar Funcionamento**
- A aplicação deve iniciar em `http://localhost:8080`
- Você deve ver as mensagens de inicialização no console
- Health check disponível em: `http://localhost:8080/api/classificar/health`

## 🚀 Testes com Postman

### **Configuração do Postman**

1. **Criar Collection:**
   - Nome: "API Classificação E-mails"
   - Descrição: "Testes para API de classificação automática"

2. **Configurar Variáveis:**
   - Base URL: `http://localhost:8080/api`
   - Porta: `8080`

---

## 🧪 **TESTE 1: Health Check**

### **Configuração:**
- **Método:** `GET`
- **URL:** `{{base_url}}/classificar/health`

### **Execução:**
1. Clique em "Send"
2. Verifique se retorna status `200 OK`

### **Resposta Esperada:**
```json
{
  "status": "UP",
  "servico": "API de Classificação de E-mails",
  "tipoModelo": "Baseado em Regras",
  "precisao": 0.85,
  "timestamp": "2024-01-15T10:30:00"
}
```

### **✅ Critério de Sucesso:**
- Status HTTP: 200
- Campo "status": "UP"
- Campo "precisao": 0.85

---

## 🧪 **TESTE 2: Informações do Modelo**

### **Configuração:**
- **Método:** `GET`
- **URL:** `{{base_url}}/classificar/modelo/info`

### **Execução:**
1. Clique em "Send"
2. Verifique se retorna status `200 OK`

### **Resposta Esperada:**
```json
{
  "sucesso": true,
  "tipoModelo": "Classificador Baseado em Regras",
  "versao": "1.0.0",
  "precisao": 0.85,
  "precisaoPorcentagem": "85.0%",
  "setoresSuportados": 9,
  "setores": {
    "ATENDIMENTO": {"descricao": "Atendimento ao Cliente"},
    "FINANCEIRO": {"descricao": "Financeiro"},
    "COMPRAS": {"descricao": "Compras"},
    "VENDAS": {"descricao": "Vendas"},
    "RH": {"descricao": "Recursos Humanos"},
    "JURIDICO": {"descricao": "Jurídico"},
    "MARKETING": {"descricao": "Marketing"},
    "TI": {"descricao": "Tecnologia da Informação"},
    "OPERACOES": {"descricao": "Operações"}
  }
}
```

### **✅ Critério de Sucesso:**
- Status HTTP: 200
- Campo "sucesso": true
- Campo "setoresSuportados": 9
- Todos os 9 setores devem estar presentes

---

## 🧪 **TESTE 3: Classificar E-mail de ATENDIMENTO**

### **Configuração:**
- **Método:** `POST`
- **URL:** `{{base_url}}/classificar/email`
- **Headers:** `Content-Type: application/json`

### **Body (JSON):**
```json
{
  "remetente": "cliente@exemplo.com",
  "destinatario": "contato@empresa.com",
  "assunto": "Problema com sistema de login",
  "corpo": "Olá, não consigo acessar minha conta. Aparece erro de senha inválida. Preciso de ajuda urgente."
}
```

### **Execução:**
1. Cole o JSON no body
2. Clique em "Send"
3. Verifique se retorna status `200 OK`

### **Resposta Esperada:**
```json
{
  "sucesso": true,
  "classificacao": {
    "setor": "ATENDIMENTO",
    "descricaoSetor": "Atendimento ao Cliente",
    "confianca": 0.85,
    "confiancaPorcentagem": "85.0%",
    "motivo": "Classificado como Atendimento ao Cliente com confiança 85.0% baseado nas palavras-chave: problema, ajuda, sistema"
  }
}
```

### **✅ Critério de Sucesso:**
- Status HTTP: 200
- Campo "setor": "ATENDIMENTO"
- Campo "confianca" > 0.7
- Campo "motivo" deve mencionar palavras-chave encontradas

---

## 🧪 **TESTE 4: Classificar E-mail FINANCEIRO**

### **Configuração:**
- **Método:** `POST`
- **URL:** `{{base_url}}/classificar/email`
- **Headers:** `Content-Type: application/json`

### **Body (JSON):**
```json
{
  "remetente": "financeiro@fornecedor.com",
  "destinatario": "contato@empresa.com",
  "assunto": "Fatura #2024-001 - Serviços de Cloud",
  "corpo": "Segue em anexo a fatura referente aos serviços de cloud do mês de janeiro. Valor: R$ 1.250,00. Vencimento: 15/02/2024."
}
```

### **Execução:**
1. Cole o JSON no body
2. Clique em "Send"
3. Verifique se retorna status `200 OK`

### **Resposta Esperada:**
```json
{
  "sucesso": true,
  "classificacao": {
    "setor": "FINANCEIRO",
    "descricaoSetor": "Financeiro",
    "confianca": 0.90,
    "confiancaPorcentagem": "90.0%",
    "motivo": "Classificado como Financeiro com confiança 90.0% baseado nas palavras-chave: fatura, valor, vencimento"
  }
}
```

### **✅ Critério de Sucesso:**
- Status HTTP: 200
- Campo "setor": "FINANCEIRO"
- Campo "confianca" > 0.8
- Campo "motivo" deve mencionar palavras-chave financeiras

---

## 🧪 **TESTE 5: Classificar E-mail de RH**

### **Configuração:**
- **Método:** `POST`
- **URL:** `{{base_url}}/classificar/email`
- **Headers:** `Content-Type: application/json`

### **Body (JSON):**
```json
{
  "remetente": "candidato@exemplo.com",
  "destinatario": "contato@empresa.com",
  "assunto": "Currículo - Desenvolvedor Full Stack",
  "corpo": "Segue em anexo meu currículo para a vaga de desenvolvedor full stack. Tenho 5 anos de experiência em Java, Spring Boot e React."
}
```

### **Execução:**
1. Cole o JSON no body
2. Clique em "Send"
3. Verifique se retorna status `200 OK`

### **Resposta Esperada:**
```json
{
  "sucesso": true,
  "classificacao": {
    "setor": "RH",
    "descricaoSetor": "Recursos Humanos",
    "confianca": 0.95,
    "confiancaPorcentagem": "95.0%",
    "motivo": "Classificado como Recursos Humanos com confiança 95.0% baseado nas palavras-chave: currículo, vaga, experiência"
  }
}
```

### **✅ Critério de Sucesso:**
- Status HTTP: 200
- Campo "setor": "RH"
- Campo "confianca" > 0.9
- Campo "motivo" deve mencionar palavras-chave de RH

---

## 🧪 **TESTE 6: Classificar Apenas Texto**

### **Configuração:**
- **Método:** `POST`
- **URL:** `{{base_url}}/classificar/texto`
- **Headers:** `Content-Type: application/json`

### **Body (JSON):**
```json
{
  "texto": "Cotação para 50 notebooks Dell Latitude com entrega em 30 dias"
}
```

### **Execução:**
1. Cole o JSON no body
2. Clique em "Send"
3. Verifique se retorna status `200 OK`

### **Resposta Esperada:**
```json
{
  "sucesso": true,
  "texto": "Cotação para 50 notebooks Dell Latitude com entrega em 30 dias",
  "classificacao": {
    "setor": "COMPRAS",
    "descricaoSetor": "Compras",
    "confianca": 0.80,
    "confiancaPorcentagem": "80.0%",
    "motivo": "Classificado como Compras com confiança 80.0% baseado nas palavras-chave: cotação, notebooks, entrega"
  }
}
```

### **✅ Critério de Sucesso:**
- Status HTTP: 200
- Campo "setor": "COMPRAS"
- Campo "confianca" > 0.7
- Campo "texto" deve ser igual ao enviado

---

## 🧪 **TESTE 7: Cenários Automáticos**

### **Configuração:**
- **Método:** `GET`
- **URL:** `{{base_url}}/classificar/testar-cenarios`

### **Execução:**
1. Clique em "Send"
2. Verifique se retorna status `200 OK`

### **Resposta Esperada:**
```json
{
  "sucesso": true,
  "totalCenarios": 5,
  "cenarios": [
    {
      "assunto": "Problema com sistema de login",
      "setorEsperado": "ATENDIMENTO",
      "setorPrevisto": "ATENDIMENTO",
      "correto": true,
      "confianca": 0.85
    },
    {
      "assunto": "Fatura #2024-001",
      "setorEsperado": "FINANCEIRO",
      "setorPrevisto": "FINANCEIRO",
      "correto": true,
      "confianca": 0.90
    }
    // ... mais cenários
  ]
}
```

### **✅ Critério de Sucesso:**
- Status HTTP: 200
- Campo "totalCenarios": 5
- Pelo menos 4 dos 5 cenários devem estar "correto": true
- Todos os cenários devem ter confiança > 0.7

---

## 🧪 **TESTE 8: Validação de Erros**

### **Teste 8.1: E-mail sem Assunto e Corpo**
- **Método:** `POST`
- **URL:** `{{base_url}}/classificar/email`
- **Body:** `{}`

**✅ Esperado:** Status 400 (Bad Request)

### **Teste 8.2: Texto Vazio**
- **Método:** `POST`
- **URL:** `{{base_url}}/classificar/texto`
- **Body:** `{"texto": ""}`

**✅ Esperado:** Status 400 (Bad Request)

### **Teste 8.3: Body Inválido**
- **Método:** `POST`
- **URL:** `{{base_url}}/classificar/email`
- **Body:** `{"invalid": "data"}`

**✅ Esperado:** Status 400 (Bad Request)

---

## 📊 **Análise dos Resultados**

### **Métricas de Qualidade:**
1. **Taxa de Sucesso:** Todos os testes devem retornar status 200 (exceto validação de erros)
2. **Precisão da Classificação:** Confiança > 0.7 para a maioria dos casos
3. **Tempo de Resposta:** < 2 segundos para cada requisição
4. **Consistência:** Mesmo input deve sempre retornar mesmo resultado

### **Cenários de Borda:**
- ✅ **E-mails com múltiplos setores:** Sistema deve escolher o mais relevante
- ✅ **Texto com palavras-chave mistas:** Deve aplicar regras de contexto
- ✅ **E-mails muito longos:** Deve processar todo o conteúdo
- ✅ **Caracteres especiais:** Deve normalizar corretamente

---

## 🎯 **Para Apresentação na Faculdade**

### **Sequência de Demonstração:**
1. **Início (1 min):** Mostrar a aplicação rodando e logs
2. **Health Check (30s):** Confirmar que está funcionando
3. **Teste de Atendimento (1 min):** E-mail de problema técnico
4. **Teste Financeiro (1 min):** E-mail de fatura/cobrança
5. **Teste de RH (1 min):** E-mail de currículo
6. **Cenários Automáticos (1 min):** Validação do sistema
7. **Explicação Técnica (2 min):** Como funciona a IA

### **Pontos de Destaque:**
- 🚀 **Sistema Funcional:** Todos os testes passam
- 🤖 **IA Inteligente:** Classificação baseada em regras contextuais
- 📱 **API REST:** Endpoints bem documentados e testáveis
- 🇧🇷 **Português:** Interface e mensagens em português
- 🎓 **Acadêmico:** Perfeito para demonstração e aprendizado

---

## 🐛 **Solução de Problemas Comuns**

### **Problema: "Connection refused"**
```bash
# Verificar se a aplicação está rodando
netstat -ano | findstr :8080

# Reiniciar a aplicação
mvn spring-boot:run
```

### **Problema: "Invalid JSON"**
- Verificar se o Content-Type está correto
- Validar sintaxe JSON no body
- Usar aspas duplas, não simples

### **Problema: "Timeout"**
- Verificar se não há processos pesados rodando
- Reiniciar a aplicação
- Verificar logs do console

---

**🎯 Boa sorte nos testes! A API deve funcionar perfeitamente para sua apresentação.** 🚀
