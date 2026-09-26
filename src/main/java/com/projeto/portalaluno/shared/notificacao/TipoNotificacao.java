package com.projeto.portalaluno.shared.notificacao;

public enum TipoNotificacao {
    CRIACAO_CONTA("Conta criada", "Sua conta foi criada com sucesso! Aguarde aprovação."),
    CONTA_APROVADA("Conta Aprovada", "Sua conta foi aprovada."),
    CONTA_REPROVADA("Conta Reprovada", "Sua conta foi reprovada."),
    DEFINIR_SENHA("Conta criada com sucesso", "Clique no link abaixo para definir senha."),
    REDEFINICAO_SENHA("Redefinição de Senha", "Clique no link abaixo para redefinir sua senha:"),
    NOVA_SOLICITACAO("Nova solicitação recebida","Nova solicitação criado pelo usuário:"),
    SOLICITACAO_CRIADA("Sua solicitação foi criada","Solicitação criada, aguarde avaliação.");

    private final String subject;
    private final String body;

    TipoNotificacao(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String getSubject() { return subject; }
    public String getBody() { return body; }
}
