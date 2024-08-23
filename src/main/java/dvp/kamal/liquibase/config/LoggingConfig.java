package dvp.kamal.liquibase.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class LoggingConfig {

    @Value("${logging.level.liquibase}")
    private String liquibaseLoggingLevel;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Autowired
    DataSource mainDataSource;

    @Bean
    public LoggerContext loggerContext(){
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        configureLogging(context);
        return context;
    }

    private void configureLogging(LoggerContext context) {
        // Console Appender
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(context);
        consoleAppender.setName("CONSOLE");

        // Pattern for Console logging
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} %highlight(%-5level) %cyan(%-50.50logger{19}) : %msg%n");
        encoder.start();

        consoleAppender.setEncoder(encoder);
        consoleAppender.start();

        Map<String,DatabaseLogAppender> dbAppenderMap = new HashMap<>();
        try (Connection conn = mainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM db_config")) {
            while(rs.next()) {
                String databaseName = rs.getString("db_name");
                //Database Appender
                DatabaseLogAppender dbAppender = new DatabaseLogAppender();
                dbAppender.setContext(context);
                dbAppender.setName("DB-" + databaseName);
//                dbAppender.setName("DB");
                dbAppender.setDbUrl(dbUrl);
                dbAppender.setDbUser(dbUser);
                dbAppender.setDbPassword(dbPassword);

                ThresholdFilter dbFilter = new ThresholdFilter();
                dbFilter.setLevel(Level.ERROR.toString());
                dbFilter.start();
                dbAppender.addFilter(dbFilter);

                dbAppender.start();
                dbAppenderMap.put(databaseName,dbAppender);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error configuring Liquibase", e);
        }

        //Liquibase Error logging Configuration
        Logger liquibaseLogger = context.getLogger("liquibase");
        String databaseName = MDC.get("databaseName");
        liquibaseLogger.setLevel(Level.ERROR);
        liquibaseLogger.addAppender(dbAppenderMap.get(databaseName));
        liquibaseLogger.setAdditive(false);


        Logger liquibaseConsoleLogger = context.getLogger("liquibase");
        liquibaseConsoleLogger.setLevel(getLogbackLevel(liquibaseLoggingLevel));
        liquibaseConsoleLogger.addAppender(consoleAppender);
        liquibaseConsoleLogger.setAdditive(false);
    }

    private Level getLogbackLevel(String level){
        return switch (level) {
            case "OFF" -> Level.OFF;
            case "ERROR" -> Level.ERROR;
            case "WARN" -> Level.WARN;
            case "DEBUG" -> Level.DEBUG;
            case "TRACE" -> Level.TRACE;
            case "ALL" -> Level.ALL;
            default -> Level.INFO;
        };
    }

}
