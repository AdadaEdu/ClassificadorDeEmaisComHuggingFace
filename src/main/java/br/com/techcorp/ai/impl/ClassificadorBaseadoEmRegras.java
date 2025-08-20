package br.com.techcorp.ai.impl;

import br.com.techcorp.ai.ClassificadorEmails;
import br.com.techcorp.models.*;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Implementação do classificador de e-mails baseado em regras
 * 
 * Este classificador simula um sistema de IA usando palavras-chave,
 * padrões regex e regras de contexto para categorizar automaticamente
 * e-mails por setor. É uma solução acadêmica que demonstra
 * os conceitos de classificação de texto.
 * 
 * @Primary - Usado como fallback quando o classificador principal não está
 *          disponível
 */
@Component
@Primary
public class ClassificadorBaseadoEmRegras implements ClassificadorEmails {

    // Mapa de palavras-chave para cada setor
    private static final Map<SetorEmail, List<String>> PALAVRAS_CHAVE = new HashMap<>();

    // Mapa de padrões regex para casos específicos
    private static final Map<SetorEmail, List<Pattern>> PADROES = new HashMap<>();

    // Precisão simulada do modelo
    private double precisao = 0.85;

    // Inicialização estática das palavras-chave e padrões
    static {
        // Palavras-chave para ATENDIMENTO
        PALAVRAS_CHAVE.put(SetorEmail.ATENDIMENTO, Arrays.asList(
                "problema", "erro", "bug", "não funciona", "ajuda", "suporte", "assistência",
                "dúvida", "questão", "falha", "defeito", "travando", "lento", "crash",
                "não consigo", "preciso de ajuda", "como fazer", "instruções"));

        // Palavras-chave para FINANCEIRO
        PALAVRAS_CHAVE.put(SetorEmail.FINANCEIRO, Arrays.asList(
                "fatura", "boleto", "pagamento", "cobrança", "invoice", "conta", "valor",
                "preço", "orçamento", "financeiro", "contabilidade", "imposto", "taxa",
                "vencimento", "multa", "juros", "desconto", "reembolso", "estorno"));

        // Palavras-chave para COMPRAS
        PALAVRAS_CHAVE.put(SetorEmail.COMPRAS, Arrays.asList(
                "cotação", "orçamento", "fornecedor", "produto", "equipamento", "material",
                "compra", "aquisição", "proposta", "preço", "quantidade", "entrega",
                "fornecimento", "pedido", "solicitação", "comparativo", "melhor preço"));

        // Palavras-chave para VENDAS
        PALAVRAS_CHAVE.put(SetorEmail.VENDAS, Arrays.asList(
                "proposta", "orçamento", "cliente", "venda", "consultoria", "serviço",
                "interesse", "demonstração", "apresentação", "comercial", "negócio",
                "parceria", "contrato", "proposta comercial", "preços", "condições"));

        // Palavras-chave para RH
        PALAVRAS_CHAVE.put(SetorEmail.RH, Arrays.asList(
                "currículo", "cv", "vaga", "emprego", "seleção", "candidato", "recrutamento",
                "trabalho", "carreira", "oportunidade", "entrevista", "contratação",
                "benefícios", "salário", "horário", "folha de pagamento", "férias"));

        // Palavras-chave para JURIDICO
        PALAVRAS_CHAVE.put(SetorEmail.JURIDICO, Arrays.asList(
                "contrato", "acordo", "legal", "processo", "advogado", "jurídico", "lei",
                "cláusula", "termo", "obrigação", "direito", "responsabilidade",
                "litígio", "arbitragem", "conformidade", "regulamentação", "auditoria"));

        // Palavras-chave para MARKETING
        PALAVRAS_CHAVE.put(SetorEmail.MARKETING, Arrays.asList(
                "evento", "parceria", "divulgação", "campanha", "publicidade", "mídia",
                "promoção", "lançamento", "conferência", "workshop", "palestra",
                "redes sociais", "branding", "posicionamento", "mercado", "público-alvo"));

        // Palavras-chave para TI
        PALAVRAS_CHAVE.put(SetorEmail.TI, Arrays.asList(
                "sistema", "software", "aplicação", "desenvolvimento", "programação",
                "banco de dados", "servidor", "rede", "infraestrutura", "tecnologia",
                "manutenção", "atualização", "backup", "segurança", "firewall"));

        // Palavras-chave para OPERACOES
        PALAVRAS_CHAVE.put(SetorEmail.OPERACOES, Arrays.asList(
                "logística", "estoque", "produção", "qualidade", "processo", "operacional",
                "manutenção", "equipamento", "facilidade", "armazém", "distribuição",
                "planejamento", "execução", "monitoramento", "controle"));

        // Padrões regex para casos específicos
        PADROES.put(SetorEmail.FINANCEIRO, Arrays.asList(
                Pattern.compile("fatura\\s*#?\\d+", Pattern.CASE_INSENSITIVE),
                Pattern.compile("boleto\\s*\\d+", Pattern.CASE_INSENSITIVE),
                Pattern.compile("\\$\\s*\\d+[.,]\\d{2}", Pattern.CASE_INSENSITIVE),
                Pattern.compile("r\\$\\s*\\d+[.,]\\d{2}", Pattern.CASE_INSENSITIVE)));

        PADROES.put(SetorEmail.RH, Arrays.asList(
                Pattern.compile("cv\\s*\\.", Pattern.CASE_INSENSITIVE),
                Pattern.compile("currículo", Pattern.CASE_INSENSITIVE),
                Pattern.compile("vaga\\s+para", Pattern.CASE_INSENSITIVE)));
    }

    @Override
    public ResultadoClassificacao classificar(Email email) {
        String texto = email.getTextoParaClassificacao();
        return classificarTexto(texto);
    }

    @Override
    public ResultadoClassificacao classificarTexto(String texto) {
        // Validação de entrada
        if (texto == null || texto.trim().isEmpty()) {
            return new ResultadoClassificacao(
                    SetorEmail.ATENDIMENTO,
                    0.5,
                    "Texto vazio - setor padrão aplicado");
        }

        // Normalização do texto para análise
        String textoNormalizado = normalizarTexto(texto);

        // Cálculo dos scores para cada setor
        Map<SetorEmail, Double> scores = calcularScores(textoNormalizado);

        // Encontrar o setor com maior pontuação
        SetorEmail melhorSetor = scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(SetorEmail.ATENDIMENTO);

        double confianca = scores.get(melhorSetor);
        String motivo = gerarMotivo(melhorSetor, textoNormalizado, confianca);

        // Criar resultado da classificação
        ResultadoClassificacao resultado = new ResultadoClassificacao(melhorSetor, confianca, motivo);
        resultado.setProbabilidadesSetores(converterScoresParaProbabilidades(scores));
        resultado.setVersaoModelo("Regras-v1.0");

        return resultado;
    }

    /**
     * Normaliza o texto removendo acentos, caracteres especiais e padronizando
     * espaços
     */
    private String normalizarTexto(String texto) {
        return texto.toLowerCase()
                .replaceAll("[áàâãä]", "a")
                .replaceAll("[éèêë]", "e")
                .replaceAll("[íìîï]", "i")
                .replaceAll("[óòôõö]", "o")
                .replaceAll("[úùûü]", "u")
                .replaceAll("[ç]", "c")
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Calcula os scores para cada setor baseado nas palavras-chave encontradas
     */
    private Map<SetorEmail, Double> calcularScores(String textoNormalizado) {
        Map<SetorEmail, Double> scores = new HashMap<>();

        // Inicializar scores com zero
        for (SetorEmail setor : SetorEmail.values()) {
            scores.put(setor, 0.0);
        }

        // Calcular scores baseados em palavras-chave
        for (Map.Entry<SetorEmail, List<String>> entry : PALAVRAS_CHAVE.entrySet()) {
            SetorEmail setor = entry.getKey();
            List<String> palavrasChave = entry.getValue();

            double score = 0.0;
            for (String palavraChave : palavrasChave) {
                if (textoNormalizado.contains(palavraChave)) {
                    score += 1.0;
                }
            }

            // Normalizar score baseado no número de palavras-chave encontradas
            if (score > 0) {
                score = Math.min(score / palavrasChave.size() * 2, 1.0);
                scores.put(setor, scores.get(setor) + score);
            }
        }

        // Aplicar padrões regex para casos específicos
        for (Map.Entry<SetorEmail, List<Pattern>> entry : PADROES.entrySet()) {
            SetorEmail setor = entry.getKey();
            List<Pattern> padroes = entry.getValue();

            for (Pattern padrao : padroes) {
                if (padrao.matcher(textoNormalizado).find()) {
                    double scoreAtual = scores.get(setor);
                    scores.put(setor, scoreAtual + 0.3);
                }
            }
        }

        // Aplicar regras de contexto para melhorar a precisão
        aplicarRegrasContexto(textoNormalizado, scores);

        return scores;
    }

    /**
     * Aplica regras de contexto para refinar a classificação
     */
    private void aplicarRegrasContexto(String texto, Map<SetorEmail, Double> scores) {
        // Regra: Se contém "fatura" e "pagamento" → FINANCEIRO
        if (texto.contains("fatura") && texto.contains("pagamento")) {
            scores.put(SetorEmail.FINANCEIRO, scores.get(SetorEmail.FINANCEIRO) + 0.5);
        }

        // Regra: Se contém "problema" e "sistema" → TI ou ATENDIMENTO
        if (texto.contains("problema") && texto.contains("sistema")) {
            scores.put(SetorEmail.TI, scores.get(SetorEmail.TI) + 0.3);
            scores.put(SetorEmail.ATENDIMENTO, scores.get(SetorEmail.ATENDIMENTO) + 0.2);
        }

        // Regra: Se contém "cv" ou "currículo" → RH
        if (texto.contains("cv") || texto.contains("currículo")) {
            scores.put(SetorEmail.RH, scores.get(SetorEmail.RH) + 0.8);
        }

        // Regra: Se contém "cotação" e "preço" → COMPRAS
        if (texto.contains("cotação") && texto.contains("preço")) {
            scores.put(SetorEmail.COMPRAS, scores.get(SetorEmail.COMPRAS) + 0.4);
        }

        // Regra: Se contém "proposta" e "cliente" → VENDAS
        if (texto.contains("proposta") && texto.contains("cliente")) {
            scores.put(SetorEmail.VENDAS, scores.get(SetorEmail.VENDAS) + 0.4);
        }
    }

    /**
     * Converte os scores em probabilidades normalizadas
     */
    private Map<String, Double> converterScoresParaProbabilidades(Map<SetorEmail, Double> scores) {
        Map<String, Double> probabilidades = new HashMap<>();
        double totalScore = scores.values().stream().mapToDouble(Double::doubleValue).sum();

        if (totalScore > 0) {
            for (Map.Entry<SetorEmail, Double> entry : scores.entrySet()) {
                double probabilidade = entry.getValue() / totalScore;
                probabilidades.put(entry.getKey().name(), probabilidade);
            }
        }

        return probabilidades;
    }

    /**
     * Gera uma explicação para a classificação realizada
     */
    private String gerarMotivo(SetorEmail setor, String texto, double confianca) {
        List<String> palavrasEncontradas = new ArrayList<>();

        if (PALAVRAS_CHAVE.containsKey(setor)) {
            for (String palavraChave : PALAVRAS_CHAVE.get(setor)) {
                if (texto.contains(palavraChave)) {
                    palavrasEncontradas.add(palavraChave);
                }
            }
        }

        if (palavrasEncontradas.isEmpty()) {
            return String.format("Classificado como %s com confiança %.1f%% (análise contextual)",
                    setor.getDescricao(), confianca * 100);
        }

        return String.format("Classificado como %s com confiança %.1f%% baseado nas palavras-chave: %s",
                setor.getDescricao(), confianca * 100, String.join(", ", palavrasEncontradas));
    }

    @Override
    public double getPrecisao() {
        return this.precisao;
    }
}
