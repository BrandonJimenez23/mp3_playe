package dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

public class DatabaseConfig {

    public static DataSource getDataSource() {
        Properties prop = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return null;
            }
            // Carga las propiedades del archivo
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(prop.getProperty("jdbc.url"));
        config.setUsername(prop.getProperty("jdbc.username"));
        config.setPassword(prop.getProperty("jdbc.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(10); // Ajusta el tamaño máximo del pool según tus necesidades
        config.setIdleTimeout(600000); // Tiempo de inactividad máximo antes de cerrar la conexión
        config.setMaxLifetime(1800000);
        return new HikariDataSource(config);
    }

}
