package balucha.url.shortener.service;

import balucha.url.shortener.domain.AccountDto;
import balucha.url.shortener.domain.AccountResponse;
import balucha.url.shortener.persistence.entity.AccountEntity;

import java.util.UUID;

public interface AccountService {

    AccountResponse create(String accountId);

    AccountDto account(UUID id);

    AccountEntity loggedAccount();

}
