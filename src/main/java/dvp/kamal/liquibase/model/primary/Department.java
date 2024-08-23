package dvp.kamal.liquibase.model.primary;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "department")
@Data
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private long departmentId;

    @Column(name = "name")
    private String departmentName;
    private String location;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;
}
