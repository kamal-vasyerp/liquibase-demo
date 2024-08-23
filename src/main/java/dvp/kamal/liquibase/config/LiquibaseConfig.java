package dvp.kamal.liquibase.config;

import ch.qos.logback.classic.LoggerContext;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Configuration
@DependsOn("loggerContext")
public class LiquibaseConfig {

    @Autowired
    private DataSource mainDataSource;

    @Autowired
    private ApplicationContext applicationContext;


    @Value("${spring.liquibase.contexts}")
    private String context;

    @Bean
    public List<SpringLiquibase> liquibaseBeans() {
        List<SpringLiquibase> liquibaseBeans = new ArrayList<>();

        try (Connection conn = mainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM db_config")) {

            while (rs.next()) {
                MDC.put("databaseName",rs.getString("db_name"));
                SpringLiquibase liquibase = new SpringLiquibase();
                liquibase.setDataSource(createDataSource(rs));
                liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.yaml");
                liquibase.setContexts(context);
                liquibase.setResourceLoader(applicationContext);

                DatabaseLogAppender dbAppender = getDatabaseLogAppender();
                if (dbAppender != null) {
                    dbAppender.setDatabaseName(rs.getString("db_name"));
                }

                liquibaseBeans.add(liquibase);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error configuring Liquibase", e);
        }

        return liquibaseBeans;
    }

    private DataSource createDataSource(ResultSet rs) throws SQLException {
        return DataSourceBuilder.create()
                .url(rs.getString("url"))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .build();
    }

    private DatabaseLogAppender getDatabaseLogAppender() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        return (DatabaseLogAppender) context.getLogger("liquibase").getAppender("DB");
    }
}
