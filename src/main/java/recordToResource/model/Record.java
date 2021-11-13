package recordToResource.model;

import lombok.*;
import org.springframework.data.annotation.AccessType;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;


@Entity
@AccessType(AccessType.Type.FIELD)
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
@NamedQueries({
        @NamedQuery(name = "findRecordsByDateAndResource",
                query = "select c from Record c " +
                        "where c.date = :date and " +
                        "c.timeStart = :time " +
                        "and c.resource = :resource"),
        @NamedQuery(name = "findRecordsByResource",
                query = "select c from Record c " +
                        "where c.resource = :resource"),
        @NamedQuery(name = "findAllRecords",
                query = "select c from Record c"),
        @NamedQuery(name = "findEmptyRecordsByResourceAndDate",
                query = "select c from Record c " +
                        "where c.resource = :resource and c.date=:date " +
                        "and c.user = null order by c.timeStart"),
        @NamedQuery(name = "findAllRecordsByUser",
                query = "select c from Record c " +
                        "where c.user = :user " +
                        "order by c.date, c.timeStart")
})
public class Record implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "Resource")
    private Resource resource;

    @ManyToOne
    @JoinColumn(name = "User")
    private User user;

    @Column
    private Date date;

    @Column
    private Time timeStart;

    @Column
    private Duration duration;

    public Record (User user,
                   Resource resource,
                   Date date,
                   Time timeStart,
                   Duration duration){
        this.user = user;
        this.resource = resource;
        this.date = date;
        this.timeStart = timeStart;
        this.duration = duration;
    }
}
