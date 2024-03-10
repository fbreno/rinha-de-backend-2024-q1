package rbe2024.fernando.request.handlers

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpExchange

class GenericHandler implements HttpHandler {
    def closure

    GenericHandler(closure) {
        this.closure = closure
    }

    @Override
    void handle(HttpExchange t) {
        closure.call(t)
    }
}
