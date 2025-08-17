#!/usr/bin/env python3
"""
Servidor Python para classificação de e-mails usando modelos reais da HuggingFace
Este servidor se comunica com a aplicação Java via HTTP
"""

from flask import Flask, request, jsonify
from transformers import pipeline, AutoTokenizer, AutoModelForSequenceClassification
import torch
import numpy as np
import logging
import os
from typing import Dict, List, Tuple
import json

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

class EmailClassifier:
    def __init__(self):
        self.model = None
        self.tokenizer = None
        self.classifier = None
        self.setores = [
            "ATENDIMENTO", "FINANCEIRO", "COMPRAS", "VENDAS", 
            "RH", "JURIDICO", "MARKETING", "TI", "OPERACOES"
        ]
        self.carregar_modelo()
    
    def carregar_modelo(self):
        """Carrega o modelo de classificação da HuggingFace"""
        try:
            logger.info("🤖 Carregando modelo de classificação da HuggingFace...")
            
            # Usar um modelo multilíngue para português
            model_name = "microsoft/mdeberta-v3-base"  # Modelo multilíngue robusto
            
            logger.info(f"📥 Baixando modelo: {model_name}")
            
            # Carregar tokenizer e modelo
            self.tokenizer = AutoTokenizer.from_pretrained(model_name)
            self.model = AutoModelForSequenceClassification.from_pretrained(
                model_name, 
                num_labels=len(self.setores),
                problem_type="multi_label_classification"
            )
            
            # Criar pipeline de classificação
            self.classifier = pipeline(
                "text-classification",
                model=self.model,
                tokenizer=self.tokenizer,
                device=0 if torch.cuda.is_available() else -1
            )
            
            logger.info("✅ Modelo carregado com sucesso!")
            logger.info(f"🔧 Dispositivo: {'GPU' if torch.cuda.is_available() else 'CPU'}")
            
        except Exception as e:
            logger.error(f"❌ Erro ao carregar modelo: {e}")
            self.model = None
            self.classifier = None
    
    def classificar_texto(self, texto: str) -> Dict:
        """Classifica o texto usando o modelo da HuggingFace"""
        try:
            if not self.classifier:
                return self._classificar_fallback(texto)
            
            # Preparar texto para classificação
            texto_limpo = self._limpar_texto(texto)
            
            # Fazer predição
            logger.info(f"🧠 Classificando texto: {texto_limpo[:100]}...")
            
            # Usar o modelo para classificação
            resultado = self.classifier(texto_limpo)
            
            # Processar resultado
            if isinstance(resultado, list) and len(resultado) > 0:
                # Para modelos que retornam lista
                predicao = resultado[0]
                label = predicao['label']
                score = predicao['score']
            else:
                # Para modelos que retornam dict
                predicao = resultado
                label = predicao.get('label', 'ATENDIMENTO')
                score = predicao.get('score', 0.5)
            
            # Mapear para nossos setores
            setor_mapeado = self._mapear_setor(label)
            
            # Calcular confiança
            confianca = float(score)
            
            # Gerar motivo
            motivo = self._gerar_motivo(setor_mapeado, confianca, texto_limpo)
            
            # Calcular probabilidades para todos os setores
            probabilidades = self._calcular_probabilidades(texto_limpo)
            
            return {
                "setor": setor_mapeado,
                "confianca": confianca,
                "motivo": motivo,
                "probabilidades": probabilidades,
                "modelo_usado": "HuggingFace-Real",
                "sucesso": True
            }
            
        except Exception as e:
            logger.error(f"❌ Erro na classificação: {e}")
            return self._classificar_fallback(texto)
    
    def _classificar_fallback(self, texto: str) -> Dict:
        """Classificação de fallback baseada em palavras-chave"""
        logger.info("🔄 Usando classificador de fallback...")
        
        # Sistema de palavras-chave simples
        palavras_chave = {
            "ATENDIMENTO": ["problema", "erro", "ajuda", "suporte", "dúvida", "falha"],
            "FINANCEIRO": ["fatura", "boleto", "pagamento", "cobrança", "conta", "valor"],
            "COMPRAS": ["cotação", "orçamento", "fornecedor", "produto", "compra"],
            "VENDAS": ["proposta", "cliente", "venda", "consultoria", "interesse"],
            "RH": ["currículo", "cv", "vaga", "emprego", "seleção", "candidato"],
            "JURIDICO": ["contrato", "legal", "processo", "advogado", "lei"],
            "MARKETING": ["evento", "campanha", "publicidade", "promoção"],
            "TI": ["sistema", "software", "tecnologia", "manutenção"],
            "OPERACOES": ["logística", "estoque", "produção", "qualidade"]
        }
        
        texto_lower = texto.lower()
        scores = {}
        
        for setor, palavras in palavras_chave.items():
            score = sum(1 for palavra in palavras if palavra in texto_lower)
            scores[setor] = score
        
        # Encontrar setor com maior score
        melhor_setor = max(scores.items(), key=lambda x: x[1])[0]
        confianca = min(0.8, 0.3 + (scores[melhor_setor] * 0.1))
        
        return {
            "setor": melhor_setor,
            "confianca": confianca,
            "motivo": f"Classificado via fallback como {melhor_setor}",
            "probabilidades": {setor: 0.1 for setor in self.setores},
            "modelo_usado": "Fallback-Palavras-Chave",
            "sucesso": True
        }
    
    def _limpar_texto(self, texto: str) -> str:
        """Limpa e normaliza o texto para classificação"""
        if not texto:
            return ""
        
        # Remover caracteres especiais e normalizar
        texto_limpo = texto.lower().strip()
        return texto_limpo
    
    def _mapear_setor(self, label: str) -> str:
        """Mapeia o label do modelo para nossos setores"""
        # Mapeamento simples baseado no texto
        label_lower = label.lower()
        
        mapeamento = {
            "atendimento": "ATENDIMENTO",
            "financeiro": "FINANCEIRO",
            "compras": "COMPRAS",
            "vendas": "VENDAS",
            "rh": "RH",
            "juridico": "JURIDICO",
            "marketing": "MARKETING",
            "ti": "TI",
            "operacoes": "OPERACOES"
        }
        
        for key, value in mapeamento.items():
            if key in label_lower:
                return value
        
        return "ATENDIMENTO"  # Setor padrão
    
    def _gerar_motivo(self, setor: str, confianca: float, texto: str) -> str:
        """Gera explicação para a classificação"""
        return f"Classificado como {setor} com confiança {confianca:.1%} usando modelo real da HuggingFace"
    
    def _calcular_probabilidades(self, texto: str) -> Dict[str, float]:
        """Calcula probabilidades para todos os setores"""
        # Distribuição simples baseada no texto
        probabilidades = {}
        total = len(self.setores)
        
        for setor in self.setores:
            # Probabilidade base
            prob = 1.0 / total
            
            # Ajustar baseado em palavras-chave
            palavras_chave = {
                "ATENDIMENTO": ["problema", "erro", "ajuda"],
                "FINANCEIRO": ["fatura", "pagamento", "conta"],
                "COMPRAS": ["cotação", "fornecedor", "produto"],
                "VENDAS": ["proposta", "cliente", "venda"],
                "RH": ["currículo", "vaga", "emprego"],
                "JURIDICO": ["contrato", "legal", "processo"],
                "MARKETING": ["evento", "campanha", "promoção"],
                "TI": ["sistema", "software", "tecnologia"],
                "OPERACOES": ["logística", "estoque", "produção"]
            }
            
            if setor in palavras_chave:
                for palavra in palavras_chave[setor]:
                    if palavra in texto.lower():
                        prob += 0.05
            
            probabilidades[setor] = min(1.0, prob)
        
        # Normalizar
        total_prob = sum(probabilidades.values())
        if total_prob > 0:
            for setor in probabilidades:
                probabilidades[setor] /= total_prob
        
        return probabilidades

# Instanciar classificador
classifier = EmailClassifier()

@app.route('/health', methods=['GET'])
def health():
    """Endpoint de health check"""
    return jsonify({
        "status": "UP",
        "servico": "HuggingFace Email Classifier",
        "modelo_carregado": classifier.model is not None,
        "dispositivo": "GPU" if torch.cuda.is_available() else "CPU"
    })

@app.route('/classificar', methods=['POST'])
def classificar():
    """Endpoint principal para classificação"""
    try:
        data = request.get_json()
        
        if not data or 'texto' not in data:
            return jsonify({
                "sucesso": False,
                "erro": "Campo 'texto' é obrigatório"
            }), 400
        
        texto = data['texto']
        
        if not texto or not texto.strip():
            return jsonify({
                "sucesso": False,
                "erro": "Texto não pode estar vazio"
            }), 400
        
        # Fazer classificação
        resultado = classifier.classificar_texto(texto)
        
        return jsonify(resultado)
        
    except Exception as e:
        logger.error(f"❌ Erro no endpoint: {e}")
        return jsonify({
            "sucesso": False,
            "erro": str(e)
        }), 500

@app.route('/modelo/info', methods=['GET'])
def modelo_info():
    """Informações sobre o modelo"""
    return jsonify({
        "tipo": "HuggingFace Transformers",
        "modelo": "microsoft/mdeberta-v3-base",
        "carregado": classifier.model is not None,
        "dispositivo": "GPU" if torch.cuda.is_available() else "CPU",
        "setores_suportados": len(classifier.setores),
        "setores": classifier.setores
    })

if __name__ == '__main__':
    logger.info("🚀 Iniciando servidor HuggingFace...")
    logger.info("📡 Endpoints disponíveis:")
    logger.info("   - GET  /health")
    logger.info("   - POST /classificar")
    logger.info("   - GET  /modelo/info")
    
    app.run(host='0.0.0.0', port=5000, debug=False)
