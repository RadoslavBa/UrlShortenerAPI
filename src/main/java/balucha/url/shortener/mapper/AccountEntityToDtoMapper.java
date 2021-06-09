package balucha.url.shortener.mapper;

import balucha.url.shortener.domain.AccountDto;
import balucha.url.shortener.persistence.entity.AccountEntity;
import balucha.url.shortener.persistence.entity.UrlEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AccountEntityToDtoMapper implements Function<AccountEntity, AccountDto> {

    @Override
    public AccountDto apply(AccountEntity entity) {
        return AccountDto.builder()
                .id(entity.id())
                .name(entity.accountId())
                .urls(entity.urls().stream().map(UrlEntity::name).collect(Collectors.toList()))
                .build();
    }
}
