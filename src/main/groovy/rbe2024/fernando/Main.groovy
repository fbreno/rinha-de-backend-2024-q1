package rbe2024.fernando

import com.sun.net.httpserver.HttpServer
import rbe2024.fernando.request.ServidorWeb


static void main(String[] args) {

    def server = HttpServer.create(new InetSocketAddress(8080), 0)
    def apiHandler = new ServidorWeb()
    server.createContext("/", apiHandler)
    server.setExecutor(null)
    server.start()

    println("Servidor HTTP iniciado na porta 8080")
}