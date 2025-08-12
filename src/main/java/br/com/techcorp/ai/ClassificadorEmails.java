package br.com.techcorp.ai;

import br.com.techcorp.models.Email;
import br.com.techcorp.models.ResultadoClassificacao;

/**
 * Interface para o sistema de classificação de e-mails por IA
 * 
 * Esta interface define os métodos necessários para
 * classificar automaticamente e-mails por setor usando
 * inteligência artificial.
 */
public interface ClassificadorEmails {

    /**
     * Classifica um e-mail e retorna o setor mais apropriado
     *
     * @param email E-mail a ser classificado
     * @return Resultado da classificação com setor e confiança
     */
    ResultadoClassificacao classificar(Email email);

    /**
     * Classifica um texto e retorna o setor mais apropriado
     *
     * @param texto Texto a ser classificado
     * @return Resultado da classificação com setor e confiança
     */
    ResultadoClassificacao classificarTexto(String texto);

    /**
     * Retorna a precisão atual do modelo
     *
     * @return Precisão em porcentagem (0.0 a 1.0)
     */
    double getPrecisao();
}
