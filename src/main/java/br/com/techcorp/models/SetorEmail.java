package br.com.techcorp.models;

/**
 * Enumeração dos setores disponíveis para classificação de e-mails
 * 
 * Cada setor representa uma área de negócio da empresa
 * e é usado pela IA para categorizar automaticamente
 * os e-mails recebidos.
 */
public enum SetorEmail {

    ATENDIMENTO("Atendimento ao Cliente"),
    FINANCEIRO("Financeiro"),
    COMPRAS("Compras"),
    VENDAS("Vendas"),
    RH("Recursos Humanos"),
    JURIDICO("Jurídico"),
    MARKETING("Marketing"),
    TI("Tecnologia da Informação"),
    OPERACOES("Operações");

    private final String descricao;

    SetorEmail(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna a descrição amigável do setor
     */
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
