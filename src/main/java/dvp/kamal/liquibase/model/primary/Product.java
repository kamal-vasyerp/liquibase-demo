package dvp.kamal.liquibase.model.primary;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private long productId;

    @Column(name = "product_name")
    private String productName;
    private String price;
    @Column(name = "product_tax")
    private String productTax;
    private String description;
}
