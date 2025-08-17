package br.com.techcorp.ai.impl;

import br.com.techcorp.models.ResultadoClassificacao;
import br.com.techcorp.models.SetorEmail;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para comunicação com o servidor Python da HuggingFace
 * 
 * Este serviço faz chamadas HTTP para o servidor Python que executa
 * modelos reais de machine learning para classificação de e-mails.
 */
@Service
public class HuggingFaceService {

    private static final Logger logger = LoggerFactory.getLogger(HuggingFaceService.class);
    
    @Value("${huggingface.server.url:http://localhost:5000}")
    private String serverUrl;
    
    private final RestTemplate restTemplate;
    
    public HuggingFaceService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Classifica texto usando o modelo real da HuggingFace
     */
    public ResultadoClassificacao classificarComHuggingFace(String texto) {
        try {
            logger.info("🤖 Enviando texto para classificação na HuggingFace: {}", 
                       texto.substring(0, Math.min(100, texto.length())));
            
            // Preparar request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("texto", texto);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            // Fazer chamada para o servidor Python
            String url = serverUrl + "/classificar";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> resultado = response.getBody();
                
                // Processar resposta
                return processarRespostaHuggingFace(resultado, texto);
                
            } else {
                logger.warn("⚠️ Resposta não esperada do servidor HuggingFace: {}", response.getStatusCode());
                return null;
            }
            
        } catch (Exception e) {
            logger.error("❌ Erro na comunicação com servidor HuggingFace: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifica se o servidor Python está disponível
     */
    public boolean isServidorDisponivel() {
        try {
            String url = serverUrl + "/health";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> health = response.getBody();
                Boolean status = (Boolean) health.get("status");
                return "UP".equals(status);
            }
            
            return false;
            
        } catch (Exception e) {
            logger.debug("Servidor HuggingFace não disponível: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtém informações sobre o modelo da HuggingFace
     */
    public Map<String, Object> getInfoModelo() {
        try {
            String url = serverUrl + "/modelo/info";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
            
            return null;
            
        } catch (Exception e) {
            logger.error("❌ Erro ao obter informações do modelo: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Processa a resposta do servidor Python
     */
    private ResultadoClassificacao processarRespostaHuggingFace(Map<String, Object> resultado, String textoOriginal) {
        try {
            // Extrair dados da resposta
            String setorStr = (String) resultado.get("setor");
            Double confianca = (Double) resultado.get("confianca");
            String motivo = (String) resultado.get("motivo");
            String modeloUsado = (String) resultado.get("modelo_usado");
            
            // Converter string para enum
            SetorEmail setor = converterParaSetorEmail(setorStr);
            
            // Criar resultado
            ResultadoClassificacao resultadoClassificacao = new ResultadoClassificacao(setor, confianca, motivo);
            resultadoClassificacao.setVersaoModelo(modeloUsado);
            
            // Adicionar probabilidades se disponíveis
            if (resultado.containsKey("probabilidades")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> probMap = (Map<String, Object>) resultado.get("probabilidades");
                
                Map<String, Double> probabilidades = new HashMap<>();
                for (Map.Entry<String, Object> entry : probMap.entrySet()) {
                    if (entry.getValue() instanceof Number) {
                        probabilidades.put(entry.getKey(), ((Number) entry.getValue()).doubleValue());
                    }
                }
                
                resultadoClassificacao.setProbabilidadesSetores(probabilidades);
            }
            
            logger.info("✅ Classificação HuggingFace concluída: {} (Confiança: {:.1%})", 
                       setor, confianca);
            
            return resultadoClassificacao;
            
        } catch (Exception e) {
            logger.error("❌ Erro ao processar resposta da HuggingFace: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Converte string para enum SetorEmail
     */
    private SetorEmail converterParaSetorEmail(String setorStr) {
        if (setorStr == null) {
            return SetorEmail.ATENDIMENTO;
        }
        
        try {
            return SetorEmail.valueOf(setorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Setor não reconhecido: {}, usando ATENDIMENTO como padrão", setorStr);
            return SetorEmail.ATENDIMENTO;
        }
    }
    
    /**
     * Define a URL do servidor Python
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    
    /**
     * Obtém a URL atual do servidor
     */
    public String getServerUrl() {
        return serverUrl;
    }
}
