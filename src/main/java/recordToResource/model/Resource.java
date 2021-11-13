package recordToResource.model;

import lombok.*;
import org.springframework.data.annotation.AccessType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@AccessType(AccessType.Type.FIELD)
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NamedQueries({
        @NamedQuery(name = "findAllResources", query = "select c from Resource c"),
        @NamedQuery(name = "findResourceByName",
                query = "select c from Resource c where c.name = :name")
})
public class Resource implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @Column
    @NotNull
    private String name;

    @Column
    private String description;

    public Resource(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
