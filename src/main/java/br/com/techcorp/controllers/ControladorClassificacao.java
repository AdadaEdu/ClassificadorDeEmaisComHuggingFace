package br.com.techcorp.controllers;

import br.com.techcorp.ai.ClassificadorEmails;
import br.com.techcorp.ai.impl.ClassificadorHuggingFaceReal;
import br.com.techcorp.models.Email;
import br.com.techcorp.models.ResultadoClassificacao;
import br.com.techcorp.models.SetorEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para a API de Classificação de E-mails
 *
 * Este controller fornece endpoints para classificar automaticamente
 * e-mails por setor usando IA REAL do Hugging Face.
 */
@RestController
@RequestMapping("/classificar")
@CrossOrigin(origins = "*")
public class ControladorClassificacao {

        private final ClassificadorEmails classificador;
        private final ClassificadorHuggingFaceReal classificadorHuggingFace;

        @Autowired
        public ControladorClassificacao(
                        ClassificadorHuggingFaceReal classificadorHuggingFace) {
                this.classificadorHuggingFace = classificadorHuggingFace;
                this.classificador = classificadorHuggingFace; // Usar o classificador Hugging Face como principal
        }

        /**
         * Endpoint principal para classificar um e-mail
         *
         * Recebe o conteúdo do e-mail (assunto e corpo) via JSON
         * e retorna a classificação automática por setor usando IA real
         */
        @PostMapping("/email")
        public ResponseEntity<Map<String, Object>> classificarEmail(
                        @RequestBody Email email) {

                try {
                        // Validação básica dos dados de entrada
                        if (email == null ||
                                        (email.getAssunto() == null || email.getAssunto().trim().isEmpty()) &&
                                                        (email.getCorpo() == null
                                                                        || email.getCorpo().trim().isEmpty())) {

                                return ResponseEntity.badRequest().body(Map.of(
                                                "sucesso", false,
                                                "mensagem", "Assunto ou corpo do e-mail é obrigatório",
                                                "timestamp", LocalDateTime.now()));
                        }

                        System.out.println("🤖 Classificando e-mail com IA Real Hugging Face: " +
                                        (email.getAssunto() != null
                                                        ? email.getAssunto().substring(0,
                                                                        Math.min(100, email.getAssunto().length()))
                                                        : "")
                                        + "...");

                        // Realizar a classificação
                        ResultadoClassificacao resultado = classificador.classificar(email);

                        // Preparar resposta
                        Map<String, Object> resposta = new HashMap<>();
                        resposta.put("sucesso", true);
                        resposta.put("email", Map.of(
                                        "remetente", email.getRemetente(),
                                        "destinatario", email.getDestinatario(),
                                        "assunto", email.getAssunto(),
                                        "corpo", email.getCorpo(),
                                        "dataRecebimento", email.getDataRecebimento()));
                        resposta.put("classificacao", Map.of(
                                        "setor", resultado.getSetor().name(),
                                        "descricaoSetor", resultado.getSetor().getDescricao(),
                                        "confianca", resultado.getConfianca(),
                                        "confiancaPorcentagem", resultado.getConfiancaPorcentagem(),
                                        "motivo", resultado.getMotivo(),
                                        "versaoModelo", resultado.getVersaoModelo()));
                        resposta.put("probabilidadesSetores", resultado.getProbabilidadesSetores());
                        resposta.put("timestamp", LocalDateTime.now());

                        // Adicionar informações sobre o modelo de IA
                        resposta.put("modeloIA", Map.of(
                                        "tipo", "Hugging Face Transformers (Modelo Real)",
                                        "carregado", classificadorHuggingFace.isModeloCarregado(),
                                        "precisao", classificadorHuggingFace.getPrecisao(),
                                        "precisaoPorcentagem",
                                        String.format("%.1f%%", classificadorHuggingFace.getPrecisao() * 100)));

                        System.out.println("✅ E-mail classificado com sucesso: " + resultado.getSetor() +
                                        " (Confiança: " + resultado.getConfiancaPorcentagem() + ")");
                        System.out.println("🤖 Modelo de IA: " + resultado.getVersaoModelo());

                        return ResponseEntity.ok(resposta);

                } catch (Exception e) {
                        System.err.println("❌ Erro na classificação do e-mail: " + e.getMessage());

                        Map<String, Object> resposta = Map.of(
                                        "sucesso", false,
                                        "mensagem", "Erro na classificação: " + e.getMessage(),
                                        "timestamp", LocalDateTime.now());

                        return ResponseEntity.internalServerError().body(resposta);
                }
        }

        /**
         * Endpoint para classificar apenas um texto
         *
         * Útil para testes rápidos ou quando se tem apenas o conteúdo textual
         */
        @PostMapping("/texto")
        public ResponseEntity<Map<String, Object>> classificarTexto(
                        @RequestBody Map<String, String> request) {

                try {
                        String texto = request.get("texto");
                        if (texto == null || texto.trim().isEmpty()) {
                                return ResponseEntity.badRequest().body(Map.of(
                                                "sucesso", false,
                                                "mensagem", "Texto é obrigatório",
                                                "timestamp", LocalDateTime.now()));
                        }

                        System.out.println("🤖 Classificando texto com IA Real Hugging Face: "
                                        + texto.substring(0, Math.min(100, texto.length())) + "...");

                        ResultadoClassificacao resultado = classificador.classificarTexto(texto);

                        Map<String, Object> resposta = new HashMap<>();
                        resposta.put("sucesso", true);
                        resposta.put("texto", texto);
                        resposta.put("classificacao", Map.of(
                                        "setor", resultado.getSetor().name(),
                                        "descricaoSetor", resultado.getSetor().getDescricao(),
                                        "confianca", resultado.getConfianca(),
                                        "confiancaPorcentagem", resultado.getConfiancaPorcentagem(),
                                        "motivo", resultado.getMotivo(),
                                        "versaoModelo", resultado.getVersaoModelo()));
                        resposta.put("probabilidadesSetores", resultado.getProbabilidadesSetores());
                        resposta.put("timestamp", LocalDateTime.now());

                        // Adicionar informações sobre o modelo de IA
                        resposta.put("modeloIA", Map.of(
                                        "tipo", "Hugging Face Transformers (Modelo Real)",
                                        "carregado", classificadorHuggingFace.isModeloCarregado(),
                                        "precisao", classificadorHuggingFace.getPrecisao(),
                                        "precisaoPorcentagem",
                                        String.format("%.1f%%", classificadorHuggingFace.getPrecisao() * 100)));

                        System.out.println("✅ Texto classificado com sucesso: " + resultado.getSetor() +
                                        " (Confiança: " + resultado.getConfiancaPorcentagem() + ")");
                        System.out.println("🤖 Modelo de IA: " + resultado.getVersaoModelo());

                        return ResponseEntity.ok(resposta);

                } catch (Exception e) {
                        System.err.println("❌ Erro na classificação do texto: " + e.getMessage());

                        Map<String, Object> resposta = Map.of(
                                        "sucesso", false,
                                        "mensagem", "Erro na classificação: " + e.getMessage(),
                                        "timestamp", LocalDateTime.now());

                        return ResponseEntity.internalServerError().body(resposta);
                }
        }

        /**
         * Endpoint para obter informações sobre o modelo de IA
         */
        @GetMapping("/modelo/info")
        public ResponseEntity<Map<String, Object>> obterInfoModelo() {
                try {
                        Map<String, Object> infoModelo = classificadorHuggingFace.getInfoModelo();

                        Map<String, Object> resposta = new HashMap<>();
                        resposta.put("sucesso", true);
                        resposta.put("tipoModelo", infoModelo.get("tipo"));
                        resposta.put("modelo", infoModelo.get("modelo"));
                        resposta.put("versao", "1.0.0");
                        resposta.put("precisao", infoModelo.get("precisao"));
                        resposta.put("precisaoPorcentagem", infoModelo.get("precisaoPorcentagem"));
                        resposta.put("carregado", infoModelo.get("carregado"));
                        resposta.put("setoresSuportados", SetorEmail.values().length);
                        resposta.put("setores", obterInfoSetores());
                        resposta.put("detalhesModelo", infoModelo);
                        resposta.put("timestamp", LocalDateTime.now());

                        return ResponseEntity.ok(resposta);

                } catch (Exception e) {
                        Map<String, Object> resposta = Map.of(
                                        "sucesso", false,
                                        "mensagem", "Erro ao obter informações do modelo: " + e.getMessage(),
                                        "timestamp", LocalDateTime.now());

                        return ResponseEntity.internalServerError().body(resposta);
                }
        }

        /**
         * Endpoint para obter status detalhado do modelo de IA
         */
        @GetMapping("/modelo/status")
        public ResponseEntity<Map<String, Object>> obterStatusModelo() {
                try {
                        Map<String, Object> infoModelo = classificadorHuggingFace.getInfoModelo();

                        Map<String, Object> resposta = new HashMap<>();
                        resposta.put("sucesso", true);
                        resposta.put("status", "OPERACIONAL");
                        resposta.put("modelo", infoModelo.get("tipo"));
                        resposta.put("carregado", infoModelo.get("carregado"));
                        resposta.put("precisao", infoModelo.get("precisao"));
                        resposta.put("precisaoPorcentagem", infoModelo.get("precisaoPorcentagem"));
                        resposta.put("diretorioModelos", infoModelo.get("diretorioModelos"));
                        resposta.put("urlModelo", infoModelo.get("urlModelo"));
                        resposta.put("timestamp", LocalDateTime.now());

                        return ResponseEntity.ok(resposta);

                } catch (Exception e) {
                        Map<String, Object> resposta = Map.of(
                                        "sucesso", false,
                                        "mensagem", "Erro ao obter status do modelo: " + e.getMessage(),
                                        "timestamp", LocalDateTime.now());

                        return ResponseEntity.internalServerError().body(resposta);
                }
        }

        /**
         * Endpoint de health check para verificar se a API está funcionando
         */
        @GetMapping("/health")
        public ResponseEntity<Map<String, Object>> health() {
                Map<String, Object> resposta = new HashMap<>();
                resposta.put("status", "UP");
                resposta.put("servico", "API de Classificação de E-mails com IA Real");
                resposta.put("tipoModelo", "Hugging Face Transformers (Modelo Real)");
                resposta.put("precisao", classificador.getPrecisao());
                resposta.put("precisaoPorcentagem", String.format("%.1f%%", classificador.getPrecisao() * 100));
                resposta.put("modeloCarregado", classificadorHuggingFace.isModeloCarregado());
                resposta.put("timestamp", LocalDateTime.now());

                return ResponseEntity.ok(resposta);
        }

        /**
         * Endpoint para testar cenários pré-definidos
         *
         * Útil para demonstrações e validação do sistema
         */
        @GetMapping("/testar-cenarios")
        public ResponseEntity<Map<String, Object>> testarCenarios() {
                try {
                        System.out.println("🧪 Executando testes de cenários com IA HuggingFace...");

                        var cenarios = new java.util.ArrayList<Map<String, Object>>();

                        // Cenário 1: E-mail de atendimento
                        var cenario1 = testarCenario(
                                        "Problema com sistema de login",
                                        "Olá, não consigo acessar minha conta. Aparece erro de senha inválida.",
                                        SetorEmail.ATENDIMENTO);
                        cenarios.add(cenario1);

                        // Cenário 2: E-mail financeiro
                        var cenario2 = testarCenario(
                                        "Fatura #2024-001",
                                        "Segue em anexo a fatura referente aos serviços de cloud do mês de janeiro.",
                                        SetorEmail.FINANCEIRO);
                        cenarios.add(cenario2);

                        // Cenário 3: E-mail de compras
                        var cenario3 = testarCenario(
                                        "Cotação - 50 notebooks Dell",
                                        "Prezados, segue nossa proposta comercial para 50 notebooks Dell Latitude.",
                                        SetorEmail.COMPRAS);
                        cenarios.add(cenario3);

                        // Cenário 4: E-mail de RH
                        var cenario4 = testarCenario(
                                        "Currículo - Desenvolvedor Full Stack",
                                        "Segue em anexo meu currículo para a vaga de desenvolvedor full stack.",
                                        SetorEmail.RH);
                        cenarios.add(cenario4);

                        // Cenário 5: E-mail de vendas
                        var cenario5 = testarCenario(
                                        "Interesse em consultoria de segurança",
                                        "Gostaríamos de conhecer seus serviços de consultoria em segurança da informação.",
                                        SetorEmail.VENDAS);
                        cenarios.add(cenario5);

                        Map<String, Object> resposta = new HashMap<>();
                        resposta.put("sucesso", true);
                        resposta.put("totalCenarios", cenarios.size());
                        resposta.put("cenarios", cenarios);
                        resposta.put("modeloIA", Map.of(
                                        "tipo", "Hugging Face Transformers (Modelo Real)",
                                        "carregado", classificadorHuggingFace.isModeloCarregado(),
                                        "precisao", classificadorHuggingFace.getPrecisao(),
                                        "precisaoPorcentagem",
                                        String.format("%.1f%%", classificadorHuggingFace.getPrecisao() * 100)));
                        resposta.put("timestamp", LocalDateTime.now());

                        System.out.println("✅ Testes de cenários concluídos com IA HuggingFace");

                        return ResponseEntity.ok(resposta);

                } catch (Exception e) {
                        System.err.println("❌ Erro nos testes de cenários: " + e.getMessage());

                        Map<String, Object> resposta = Map.of(
                                        "sucesso", false,
                                        "mensagem", "Erro nos testes de cenários: " + e.getMessage(),
                                        "timestamp", LocalDateTime.now());

                        return ResponseEntity.internalServerError().body(resposta);
                }
        }

        // Métodos auxiliares
        private Map<String, Object> obterInfoSetores() {
                var infoSetores = new HashMap<String, Object>();

                for (SetorEmail setor : SetorEmail.values()) {
                        infoSetores.put(setor.name(), Map.of(
                                        "descricao", setor.getDescricao()));
                }

                return infoSetores;
        }

        private Map<String, Object> testarCenario(String assunto, String corpo, SetorEmail setorEsperado) {
                String texto = assunto + " " + corpo;
                ResultadoClassificacao resultado = classificador.classificarTexto(texto);

                boolean correto = resultado.getSetor() == setorEsperado;

                Map<String, Object> cenario = new HashMap<>();
                cenario.put("assunto", assunto);
                cenario.put("corpo", corpo);
                cenario.put("setorEsperado", setorEsperado.name());
                cenario.put("setorPrevisto", resultado.getSetor().name());
                cenario.put("confianca", resultado.getConfianca());
                cenario.put("confiancaPorcentagem", resultado.getConfiancaPorcentagem());
                cenario.put("correto", correto);
                cenario.put("motivo", resultado.getMotivo());
                cenario.put("versaoModelo", resultado.getVersaoModelo());

                return cenario;
        }
}
