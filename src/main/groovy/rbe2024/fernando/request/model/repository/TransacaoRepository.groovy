package rbe2024.fernando.request.model.repository

class TransacaoRepository {

    BDConnector bdConnector

    TransacaoRepository(BDConnector bdConnector) {
        this.bdConnector = bdConnector
    }



}
