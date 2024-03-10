package rbe2024.fernando.request.model.repository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import groovy.sql.Sql

class BDConnector {
    static final String URL = "jdbc:postgresql://localhost:5432/postgres"
    static final String USUARIO = "root"
    static final String SENHA = "root"

    HikariDataSource dataSource
    Sql sql

    BDConnector() {
        def config = new HikariConfig()
        config.setJdbcUrl(URL)
        config.setUsername(USUARIO)
        config.setPassword(SENHA)
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        dataSource = new HikariDataSource(config)
        sql = Sql.newInstance(dataSource)
    }

    void fecharConexao() {
        sql.close()
        dataSource.close()
    }
}
