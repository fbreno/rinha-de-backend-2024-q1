package rbe2024.fernando.request.model.domain

// Transacao.groovy
class Transacao {
    int valor
    String tipo
    String descricao
    Date realizadaEm

    Transacao(int valor, String tipo, String descricao) {
        this.valor = valor
        this.tipo = tipo
        this.descricao = descricao
        this.realizadaEm = new Date()
    }
}
