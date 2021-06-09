package balucha.url.shortener.persistence.entity;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(name = "Url")
@Table(name = "URL")
@Builder(toBuilder = true)
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class UrlEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private final UUID id;

    private final String name;

    private final int redirectType;

    private final long callNumber;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AccountEntity account;

    public UrlEntity increaseCallNumber() {
        long callNum = this.callNumber;
        callNum++;
        return this.toBuilder().callNumber(callNum).build();
    }
}
