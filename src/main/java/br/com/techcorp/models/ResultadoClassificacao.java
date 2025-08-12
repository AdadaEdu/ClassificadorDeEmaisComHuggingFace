package br.com.techcorp.models;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Resultado da classificação de um e-mail por IA
 * 
 * Esta classe contém o resultado da análise automática
 * realizada pelo sistema de classificação, incluindo
 * o setor identificado e o nível de confiança.
 */
public class ResultadoClassificacao {

    private SetorEmail setor;
    private Double confianca;
    private String motivo;
    private LocalDateTime dataClassificacao;
    private Map<String, Double> probabilidadesSetores;
    private String versaoModelo;

    // Construtor padrão
    public ResultadoClassificacao() {
        this.dataClassificacao = LocalDateTime.now();
    }

    // Construtor com campos principais
    public ResultadoClassificacao(SetorEmail setor, Double confianca, String motivo) {
        this();
        this.setor = setor;
        this.confianca = confianca;
        this.motivo = motivo;
    }

    // Getters e Setters
    public SetorEmail getSetor() {
        return setor;
    }

    public void setSetor(SetorEmail setor) {
        this.setor = setor;
    }

    public Double getConfianca() {
        return confianca;
    }

    public void setConfianca(Double confianca) {
        this.confianca = confianca;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getDataClassificacao() {
        return dataClassificacao;
    }

    public void setDataClassificacao(LocalDateTime dataClassificacao) {
        this.dataClassificacao = dataClassificacao;
    }

    public Map<String, Double> getProbabilidadesSetores() {
        return probabilidadesSetores;
    }

    public void setProbabilidadesSetores(Map<String, Double> probabilidadesSetores) {
        this.probabilidadesSetores = probabilidadesSetores;
    }

    public String getVersaoModelo() {
        return versaoModelo;
    }

    public void setVersaoModelo(String versaoModelo) {
        this.versaoModelo = versaoModelo;
    }

    /**
     * Retorna a confiança em formato de porcentagem
     */
    public String getConfiancaPorcentagem() {
        if (confianca != null) {
            return String.format("%.1f%%", confianca * 100);
        }
        return "0.0%";
    }

    @Override
    public String toString() {
        return "ResultadoClassificacao{" +
                "setor=" + setor +
                ", confianca=" + confianca +
                ", motivo='" + motivo + '\'' +
                ", dataClassificacao=" + dataClassificacao +
                '}';
    }
}
