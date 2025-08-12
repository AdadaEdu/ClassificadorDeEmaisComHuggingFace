package br.com.techcorp.models;

import java.time.LocalDateTime;

/**
 * Modelo de dados simplificado para representar um e-mail
 * 
 * Esta classe contém apenas os campos essenciais necessários
 * para a classificação automática por IA, sem persistência
 * em banco de dados.
 */
public class Email {

    private String remetente;
    private String destinatario;
    private String assunto;
    private String corpo;
    private LocalDateTime dataRecebimento;

    // Construtor padrão
    public Email() {
        this.dataRecebimento = LocalDateTime.now();
    }

    // Construtor com campos principais
    public Email(String remetente, String destinatario, String assunto, String corpo) {
        this();
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.assunto = assunto;
        this.corpo = corpo;
    }

    // Getters e Setters
    public String getRemetente() {
        return remetente;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public LocalDateTime getDataRecebimento() {
        return dataRecebimento;
    }

    public void setDataRecebimento(LocalDateTime dataRecebimento) {
        this.dataRecebimento = dataRecebimento;
    }

    /**
     * Retorna o texto completo para classificação
     * Combina assunto e corpo para análise da IA
     */
    public String getTextoParaClassificacao() {
        StringBuilder texto = new StringBuilder();
        
        if (assunto != null && !assunto.trim().isEmpty()) {
            texto.append(assunto).append(" ");
        }
        
        if (corpo != null && !corpo.trim().isEmpty()) {
            texto.append(corpo);
        }
        
        return texto.toString().trim();
    }

    @Override
    public String toString() {
        return "Email{" +
                "remetente='" + remetente + '\'' +
                ", destinatario='" + destinatario + '\'' +
                ", assunto='" + assunto + '\'' +
                ", dataRecebimento=" + dataRecebimento +
                '}';
    }
}
