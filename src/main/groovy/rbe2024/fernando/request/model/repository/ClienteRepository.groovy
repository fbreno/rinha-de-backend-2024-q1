package rbe2024.fernando.request.model.repository

import rbe2024.fernando.request.model.domain.Cliente

class ClienteRepository {
    BDConnector bdConnector

    ClienteRepository(BDConnector bdConnector) {
        this.bdConnector = bdConnector
    }

    def getById(Integer id){
        def result = bdConnector.sql.firstRow("SELECT * FROM cliente WHERE id = ?", [id])
        if (result) {
            return new Cliente(result.id, result.nome, result.saldo, result.limite)
        } else {
            return null
        }
    }
}
