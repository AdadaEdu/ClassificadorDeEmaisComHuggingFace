package br.com.techcorp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicação principal da API de Classificação de E-mails com IA Real
 *
 * Esta é a classe principal que inicia a aplicação Spring Boot.
 * A aplicação fornece uma API REST para classificar automaticamente
 * o conteúdo de e-mails usando modelos reais de IA da HuggingFace.
 *
 * @author TechCorp Solutions
 * @version 1.0.0
 */
@SpringBootApplication
public class AplicacaoClassificacaoEmails {

    public static void main(String[] args) {
        SpringApplication.run(AplicacaoClassificacaoEmails.class, args);
        System.out.println("🚀 API de Classificação de E-mails com IA REAL iniciada com sucesso!");
        System.out.println("📧 Endpoint principal: http://localhost:8080/api");
        System.out.println("🤖 Endpoint de classificação: http://localhost:8080/api/classificar");
        System.out.println("🧠 Modelo de IA: HuggingFace Transformers");
        System.out.println("📊 Informações do modelo: http://localhost:8080/api/classificar/modelo/info");
        System.out.println("🔍 Status do modelo: http://localhost:8080/api/classificar/modelo/status");
        System.out.println("⏳ Aguardando carregamento do modelo de IA...");
    }
}
