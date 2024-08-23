package dvp.kamal.liquibase.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DependsOn("loggerContext")
public class LiquibaseBeansFactory implements InitializingBean {

    @Autowired
    private List<SpringLiquibase> liquibaseBeans;

    @Override
    public void afterPropertiesSet() throws Exception {
        for (SpringLiquibase liquibase : liquibaseBeans) {
            liquibase.afterPropertiesSet();
        }
    }
}