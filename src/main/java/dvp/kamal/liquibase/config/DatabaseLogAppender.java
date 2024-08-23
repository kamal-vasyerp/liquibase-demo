package dvp.kamal.liquibase.config;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.sql.*;
import java.util.Arrays;

public class DatabaseLogAppender extends AppenderBase<ILoggingEvent> {

    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private String dbName;

    public void setDatabaseName(String databaseName){
        this.dbName = databaseName;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        System.out.println("DatabaseLogAppender received an event: " + eventObject.getMessage());
        // Insert log event into database
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String sql = "INSERT INTO liquibase_exceptions (id,author,file_path,classname,exception_message,stack_trace,created_on,database_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                String[] messageString = eventObject.getMessage().split("::");
                String filePath = messageString[0].replaceFirst("^ChangeSet ", "");
                String Id = messageString[1];
                String author = messageString[2].replace("encountered an exception.", "").trim();
                stmt.setString(1,Id);
                stmt.setString(2,author);
                stmt.setString(3,filePath);
                if (eventObject.getThrowableProxy() != null) {
                    String className = eventObject.getThrowableProxy().getClassName();
                    String exceptionMessage = eventObject.getThrowableProxy().getMessage();
                    stmt.setString(4,className);
                    stmt.setString(5,exceptionMessage);
                    stmt.setString(6, Arrays.toString(eventObject.getThrowableProxy().getStackTraceElementProxyArray()));
                } else {
                    stmt.setNull(4, java.sql.Types.VARCHAR);
                    stmt.setNull(5, java.sql.Types.VARCHAR);
                    stmt.setNull(6, java.sql.Types.VARCHAR);
                }
                stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
                stmt.setString(8,dbName);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            addError("Failed to insert log event into database", e);
        }
    }

    // Setters for the database connection properties
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}
