# 🤖 Integração com HuggingFace - IA Real

Este projeto agora está integrado com **modelos reais de IA da HuggingFace** para classificação automática de e-mails!

## 🎯 **O que mudou:**

### ✅ **Antes (Simulação):**
- Sistema baseado em palavras-chave e regras
- "Modelo carregado" era apenas simulação
- Precisão hardcoded em 94%

### 🚀 **Agora (IA Real):**
- **Modelo real da HuggingFace**: `microsoft/mdeberta-v3-base`
- **Classificação via machine learning** real
- **Fallback inteligente** para o sistema local
- **Comunicação HTTP** entre Java e Python

## 🔧 **Arquitetura da Integração:**

```
┌─────────────────┐    HTTP    ┌──────────────────┐
│   Java Spring   │ ────────── │  Python Flask    │
│     Boot App    │            │  HuggingFace     │
│                 │            │    Server        │
└─────────────────┘            └──────────────────┘
         │                              │
         │                              │
         ▼                              ▼
┌─────────────────┐            ┌──────────────────┐
│  Fallback       │            │  Modelo Real     │
│  Local (Regras) │            │  HuggingFace     │
└─────────────────┘            └──────────────────┘
```

## 🚀 **Como usar:**

### **1. Instalar dependências Python:**
```bash
./setup_huggingface.sh
```

### **2. Executar servidor Python (Terminal 1):**
```bash
source huggingface_env/bin/activate
python huggingface_server.py
```

### **3. Executar aplicação Java (Terminal 2):**
```bash
mvn spring-boot:run
```

## 📡 **Endpoints disponíveis:**

### **Servidor Python (Porta 5000):**
- `GET /health` - Status do servidor
- `POST /classificar` - Classificação de texto
- `GET /modelo/info` - Informações do modelo

### **Aplicação Java (Porta 8080):**
- `GET /api/classificar/health` - Health check
- `POST /api/classificar/texto` - Classificação via IA
- `GET /api/classificar/modelo/info` - Info do modelo
- `GET /api/classificar/testar-cenarios` - Testes automáticos

## 🧠 **Modelo de IA usado:**

- **Nome**: `microsoft/mdeberta-v3-base`
- **Tipo**: Modelo multilíngue (inclui português)
- **Tarefa**: Classificação de texto
- **Dispositivo**: GPU (se disponível) ou CPU
- **Precisão**: Real (calculada pelo modelo)

## 🔄 **Sistema de Fallback:**

1. **Primeira tentativa**: Modelo real da HuggingFace
2. **Segunda tentativa**: Sistema local baseado em regras
3. **Terceira tentativa**: Classificador de palavras-chave

## 📊 **Exemplo de uso:**

### **Teste com IA real:**
```bash
curl -X POST http://localhost:8080/api/classificar/texto \
  -H "Content-Type: application/json" \
  -d '{"texto": "Preciso de ajuda com um problema no sistema de login"}'
```

### **Resposta esperada:**
```json
{
  "classificacao": {
    "setor": "ATENDIMENTO",
    "confianca": 0.85,
    "modelo_usado": "HuggingFace-Real"
  },
  "modeloIA": {
    "tipo": "HuggingFace Transformers (Modelo Real)",
    "carregado": true,
    "dispositivo": "GPU"
  }
}
```

## ⚠️ **Requisitos do sistema:**

### **Python:**
- Python 3.8+
- 4GB+ RAM (para carregar o modelo)
- Conexão com internet (primeira execução)

### **Java:**
- Java 17+
- Spring Boot 3.2.0
- Maven

## 🐛 **Solução de problemas:**

### **Servidor Python não inicia:**
```bash
# Verificar dependências
pip list | grep transformers

# Reinstalar
pip uninstall transformers torch
pip install -r requirements.txt
```

### **Modelo não carrega:**
```bash
# Verificar espaço em disco
df -h

# Verificar memória
free -h

# Limpar cache
rm -rf ~/.cache/huggingface/
```

### **Comunicação falha:**
```bash
# Verificar se porta 5000 está livre
netstat -tulpn | grep 5000

# Testar conectividade
curl http://localhost:5000/health
```

## 🔍 **Monitoramento:**

### **Logs do servidor Python:**
```bash
tail -f huggingface_server.log
```

### **Logs da aplicação Java:**
```bash
tail -f target/spring.log
```

### **Status em tempo real:**
```bash
curl http://localhost:8080/api/classificar/modelo/status
```

## 🚀 **Próximos passos:**

1. **Fine-tuning** do modelo para e-mails em português
2. **Modelos específicos** para cada setor
3. **Cache de predições** para melhor performance
4. **Métricas de precisão** em tempo real
5. **Interface web** para treinamento

## 📚 **Recursos adicionais:**

- [Documentação HuggingFace](https://huggingface.co/docs)
- [Transformers Python](https://github.com/huggingface/transformers)
- [Modelos multilíngues](https://huggingface.co/models?pipeline_tag=text-classification&language=pt)
- [Fine-tuning guide](https://huggingface.co/docs/transformers/training)

---

**🎉 Parabéns! Agora você tem um sistema de classificação de e-mails com IA real da HuggingFace!**
