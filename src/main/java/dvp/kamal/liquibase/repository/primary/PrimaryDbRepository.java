package dvp.kamal.liquibase.repository.primary;

import dvp.kamal.liquibase.model.primary.Department;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("primaryEntityManagerFactory")
public interface PrimaryDbRepository extends JpaRepository<Department,Long> {
}
