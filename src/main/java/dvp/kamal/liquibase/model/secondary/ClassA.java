package dvp.kamal.liquibase.model.secondary;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ClassA {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long aId;
    private String aName;
}
