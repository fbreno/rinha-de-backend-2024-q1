package rbe2024.fernando.request.model.domain

// Cliente.groovy
class Cliente {
    int id
    int limite
    int saldo
    List<Transacao> transacoes = []

    Cliente(int id, int limite) {
        this.id = id
        this.limite = limite
        this.saldo = 0
    }

}
