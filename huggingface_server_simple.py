#!/usr/bin/env python3
"""
Servidor Python SIMPLIFICADO para classificação de e-mails
Esta versão funciona sem PyTorch para teste imediato
"""

from flask import Flask, request, jsonify
import logging
import json
import re

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

class SimpleEmailClassifier:
    def __init__(self):
        self.setores = [
            "ATENDIMENTO", "FINANCEIRO", "COMPRAS", "VENDAS", 
            "RH", "JURIDICO", "MARKETING", "TI", "OPERACOES"
        ]
        
        # Sistema de palavras-chave avançado
        self.palavras_chave = {
            "ATENDIMENTO": {
                "problema": 0.95, "erro": 0.90, "ajuda": 0.85, "suporte": 0.88,
                "dúvida": 0.80, "questão": 0.75, "falha": 0.92, "defeito": 0.89,
                "travando": 0.87, "lento": 0.82, "crash": 0.94, "não consigo": 0.88
            },
            "FINANCEIRO": {
                "fatura": 0.96, "boleto": 0.94, "pagamento": 0.92, "cobrança": 0.90,
                "invoice": 0.95, "conta": 0.88, "valor": 0.85, "preço": 0.87,
                "orçamento": 0.89, "financeiro": 0.91, "contabilidade": 0.93
            },
            "COMPRAS": {
                "cotação": 0.96, "orçamento": 0.94, "fornecedor": 0.95, "produto": 0.88,
                "equipamento": 0.92, "material": 0.89, "compra": 0.91, "aquisição": 0.93
            },
            "VENDAS": {
                "proposta": 0.94, "orçamento": 0.92, "cliente": 0.95, "venda": 0.93,
                "consultoria": 0.91, "serviço": 0.88, "interesse": 0.89, "demonstração": 0.90
            },
            "RH": {
                "currículo": 0.98, "cv": 0.97, "vaga": 0.95, "emprego": 0.93,
                "seleção": 0.94, "candidato": 0.96, "recrutamento": 0.95, "trabalho": 0.89
            },
            "JURIDICO": {
                "contrato": 0.96, "acordo": 0.94, "legal": 0.95, "processo": 0.93,
                "advogado": 0.97, "jurídico": 0.96, "lei": 0.94, "cláusula": 0.95
            },
            "MARKETING": {
                "evento": 0.94, "parceria": 0.92, "divulgação": 0.93, "campanha": 0.95,
                "publicidade": 0.94, "mídia": 0.91, "promoção": 0.93, "lançamento": 0.94
            },
            "TI": {
                "sistema": 0.94, "software": 0.95, "aplicação": 0.93, "desenvolvimento": 0.92,
                "programação": 0.94, "banco de dados": 0.96, "servidor": 0.95, "rede": 0.93
            },
            "OPERACOES": {
                "logística": 0.94, "estoque": 0.95, "produção": 0.93, "qualidade": 0.91,
                "processo": 0.89, "operacional": 0.92, "manutenção": 0.88, "equipamento": 0.90
            }
        }
    
    def classificar_texto(self, texto: str) -> dict:
        """Classifica o texto usando sistema avançado de palavras-chave"""
        try:
            logger.info(f"🧠 Classificando texto: {texto[:100]}...")
            
            # Normalizar texto
            texto_normalizado = self._normalizar_texto(texto)
            
            # Calcular scores para cada setor
            scores = self._calcular_scores_avancados(texto_normalizado)
            
            # Encontrar setor com maior score
            melhor_setor = max(scores.items(), key=lambda x: x[1])[0]
            confianca = scores[melhor_setor]
            
            # Gerar motivo
            motivo = self._gerar_motivo(melhor_setor, confianca, texto_normalizado)
            
            # Calcular probabilidades
            probabilidades = self._calcular_probabilidades(scores)
            
            return {
                "setor": melhor_setor,
                "confianca": confianca,
                "motivo": motivo,
                "probabilidades": probabilidades,
                "modelo_usado": "HuggingFace-Simulado-Avancado",
                "sucesso": True
            }
            
        except Exception as e:
            logger.error(f"❌ Erro na classificação: {e}")
            return self._classificar_fallback(texto)
    
    def _normalizar_texto(self, texto: str) -> str:
        """Normaliza o texto para análise"""
        if not texto:
            return ""
        
        return texto.lower().strip()
    
    def _calcular_scores_avancados(self, texto: str) -> dict:
        """Calcula scores usando sistema avançado"""
        scores = {setor: 0.0 for setor in self.setores}
        
        for setor, palavras in self.palavras_chave.items():
            score = 0.0
            palavras_encontradas = 0
            
            for palavra, peso in palavras.items():
                if palavra in texto:
                    score += peso
                    palavras_encontradas += 1
            
            # Normalizar score
            if score > 0:
                score = self._normalizar_score(score, palavras_encontradas, len(palavras))
                scores[setor] = score
        
        # Aplicar regras de contexto
        self._aplicar_regras_contexto(texto, scores)
        
        return scores
    
    def _normalizar_score(self, score: float, palavras_encontradas: int, total_palavras: int) -> float:
        """Normaliza o score usando algoritmo avançado"""
        densidade = palavras_encontradas / total_palavras
        fator_qualidade = score / palavras_encontradas if palavras_encontradas > 0 else 0
        
        score_normalizado = score * densidade * fator_qualidade
        return min(score_normalizado, 1.0)
    
    def _aplicar_regras_contexto(self, texto: str, scores: dict):
        """Aplica regras de contexto para melhorar precisão"""
        # Regras específicas
        if "fatura" in texto and "pagamento" in texto:
            scores["FINANCEIRO"] += 0.15
        
        if "problema" in texto and "sistema" in texto:
            scores["TI"] += 0.12
            scores["ATENDIMENTO"] += 0.10
        
        if "cv" in texto or "currículo" in texto:
            scores["RH"] += 0.20
        
        if "cotação" in texto and "preço" in texto:
            scores["COMPRAS"] += 0.18
        
        if "proposta" in texto and "cliente" in texto:
            scores["VENDAS"] += 0.16
    
    def _calcular_probabilidades(self, scores: dict) -> dict:
        """Calcula probabilidades normalizadas"""
        probabilidades = {}
        total_score = sum(scores.values())
        
        if total_score > 0:
            for setor, score in scores.items():
                probabilidades[setor] = score / total_score
        else:
            # Distribuição uniforme se não há scores
            for setor in scores:
                probabilidades[setor] = 1.0 / len(scores)
        
        return probabilidades
    
    def _gerar_motivo(self, setor: str, confianca: float, texto: str) -> str:
        """Gera explicação para a classificação"""
        palavras_encontradas = []
        
        if setor in self.palavras_chave:
            for palavra, peso in self.palavras_chave[setor].items():
                if palavra in texto:
                    palavras_encontradas.append(f"{palavra}({peso:.2f})")
        
        if palavras_encontradas:
            return f"Classificado como {setor} com confiança {confianca:.1%} usando IA simulada. Palavras-chave: {', '.join(palavras_encontradas)}"
        else:
            return f"Classificado como {setor} com confiança {confianca:.1%} usando análise contextual"
    
    def _classificar_fallback(self, texto: str) -> dict:
        """Classificação de fallback simples"""
        return {
            "setor": "ATENDIMENTO",
            "confianca": 0.5,
            "motivo": "Classificação via fallback - erro no sistema principal",
            "probabilidades": {setor: 0.1 for setor in self.setores},
            "modelo_usado": "Fallback-Simples",
            "sucesso": True
        }

# Instanciar classificador
classifier = SimpleEmailClassifier()

@app.route('/health', methods=['GET'])
def health():
    """Endpoint de health check"""
    return jsonify({
        "status": "UP",
        "servico": "HuggingFace Email Classifier (Simulado)",
        "modelo_carregado": True,
        "dispositivo": "CPU",
        "versao": "Simulada-v1.0"
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
        "tipo": "HuggingFace Transformers (Simulado)",
        "modelo": "Sistema de Classificação Inteligente Simulado",
        "carregado": True,
        "dispositivo": "CPU",
        "setores_suportados": len(classifier.setores),
        "setores": classifier.setores,
        "versao": "Simulada-v1.0"
    })

if __name__ == '__main__':
    logger.info("🚀 Iniciando servidor HuggingFace SIMPLIFICADO...")
    logger.info("📡 Endpoints disponíveis:")
    logger.info("   - GET  /health")
    logger.info("   - POST /classificar")
    logger.info("   - GET  /modelo/info")
    logger.info("💡 Este é um servidor SIMULADO para teste imediato")
    logger.info("🔗 Para IA real, instale PyTorch e use huggingface_server.py")
    
    app.run(host='0.0.0.0', port=5000, debug=False)
