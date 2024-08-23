package dvp.kamal.liquibase.repository.secondary;

import dvp.kamal.liquibase.model.secondary.ClassA;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("secondaryEntityManagerFactory")
public interface SecondaryDbRepository extends JpaRepository<ClassA, Long> {
}
