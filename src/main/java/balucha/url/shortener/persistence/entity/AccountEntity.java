package balucha.url.shortener.persistence.entity;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(name = "Account")
@Table(name = "ACCOUNT")
@Builder(toBuilder = true)
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class AccountEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private final UUID id;

    private final String accountId;

    private final String password;

    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "account")
    private final Set<UrlEntity> urls = new HashSet<>();


    public AccountEntity addUrl(UrlEntity url) {
        val newUrls = this.urls;
        newUrls.add(url);
        return this.toBuilder().urls(newUrls).build();
    }
}
