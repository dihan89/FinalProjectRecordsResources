package recordToResource.model;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "phone"))
@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "findAll", query = "select c from User c"),
        @NamedQuery(name = "findByPhone",
                query = "select c from User c where c.phone = :phone"),
        @NamedQuery(name = "findUser",
                query = "select c from User c where c.name = :name " +
                        "and c.surname = :surname and" +
                        " c.phone = :phone")
})
public class User implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private String name;

    @Column
    private String surname;

    @Column
    private String phone;

    public User(String name, String surname, String phone) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
    }
}
