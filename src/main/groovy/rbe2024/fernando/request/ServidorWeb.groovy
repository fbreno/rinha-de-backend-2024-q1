package rbe2024.fernando.request

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.json.JsonSlurper
import rbe2024.fernando.request.model.domain.Cliente
import rbe2024.fernando.request.model.domain.Transacao
import rbe2024.fernando.request.model.repository.BDConnector
import rbe2024.fernando.request.model.repository.ClienteRepository


class ServidorWeb implements HttpHandler {
    Map<Integer, Cliente> clientes = [:]

    @Override
    void handle(HttpExchange t) {
        def path = t.getRequestURI().getPath()
        def method = t.getRequestMethod()

        switch (path) {
            case ~/\/clientes\/(\d+)\/transacoes/:
                handleTransacoes(t, method)
                break
            case ~/\/clientes\/(\d+)\/extrato/:
                handleExtrato(t, method)
                break
            default:
                sendResponse(t, 404, "Not Found")
        }
    }

    void handleTransacoes(HttpExchange t, String method) {
        def id = extractIdFromPath(t.getRequestURI().getPath())

        if (!clientes.containsKey(id)) {
            sendResponse(t, 404, "Cliente not found")
            return
        }

        if (method == "POST") {
            handleTransacoesPost(t, id)
        } else {
            sendResponse(t, 405, "Method Not Allowed")
        }
    }

    void handleTransacoesPost(HttpExchange t, int id) {
        def requestBody = new JsonSlurper().parse(new InputStreamReader(t.getRequestBody(), "UTF-8"))

        if (!validateTransacaoPayload(requestBody)) {
            sendResponse(t, 422, "Unprocessable Entity")
            return
        }

        def cliente = clientes[id]
        def transacao = new Transacao(requestBody.valor, requestBody.tipo, requestBody.descricao)

        if (transacao.tipo == "d" && (cliente.saldo - transacao.valor) < -cliente.limite) {
            sendResponse(t, 422, "Unprocessable Entity")
            return
        }

        if (transacao.tipo == "c") {
            cliente.saldo += transacao.valor
        } else {
            cliente.saldo -= transacao.valor
        }

        cliente.transacoes.add(transacao)

        sendResponse(t, 200, "{\"limite\": ${cliente.limite}, \"saldo\": ${cliente.saldo}}")
    }

    void handleExtrato(HttpExchange t, String method) {
        def id = extractIdFromPath(t.getRequestURI().getPath()) as Integer

        BDConnector bdConnector = new BDConnector()
        def clientRepository = new ClienteRepository(bdConnector)
        def cliente = clientRepository.getById(id)

        if (!cliente) {
            sendResponse(t, 404, "Cliente not found")
            return
        }

        if (method == "GET") {
            handleExtratoGet(t, id)
        } else {
            sendResponse(t, 405, "Method Not Allowed")
        }
    }

    void handleExtratoGet(HttpExchange t, int id) {
        def cliente = clientes[id]

        def extrato = [
                saldo: [
                        total: cliente.saldo,
                        data_extrato: new Date().toString(),
                        limite: cliente.limite
                ],
                ultimas_transacoes: cliente.transacoes.sort { it.realizadaEm }.reverse().take(10).collect {
                    [
                            valor: it.valor,
                            tipo: it.tipo,
                            descricao: it.descricao,
                            realizada_em: it.realizadaEm.toString()
                    ]
                }
        ]

        sendResponse(t, 200, extrato.toString())
    }

    boolean validateTransacaoPayload(def payload) {
        return payload.valor instanceof Integer &&
                payload.valor > 0 &&
                payload.tipo in ["c", "d"] &&
                payload.descricao instanceof String && payload.descricao.length() > 0 && payload.descricao.length() <= 10
    }

    def extractIdFromPath(String path) {
        def matcher = (path =~ /\/clientes\/(\d+)/)
        return matcher ? matcher[0][1] as Integer : null
    }

    void sendResponse(HttpExchange t, int statusCode, String response) {
        t.sendResponseHeaders(statusCode, response.length())
        def os = t.getResponseBody()

        os.write(response.getBytes())
        os.close()
    }
}
