package rbe2024.fernando.request.handlers

import com.sun.net.httpserver.HttpHandler

class Route {
    Map<String, HttpHandler> routes = [:]

    void addRoute(String path, HttpHandler handler) {
        routes[path] = handler
    }

    HttpHandler getHandler(String path) {
        return routes.get(path)
    }
}
