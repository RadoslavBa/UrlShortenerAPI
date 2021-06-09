package balucha.url.shortener.service.impl;

import balucha.url.shortener.domain.AccountDto;
import balucha.url.shortener.domain.AccountResponse;
import balucha.url.shortener.mapper.AccountEntityToDtoMapper;
import balucha.url.shortener.persistence.entity.AccountEntity;
import balucha.url.shortener.persistence.repository.AccountRepository;
import balucha.url.shortener.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final String SUCCESS_DESC = "Your account is opened";
    private static final String FAIL_DESC = "Account already exists";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final int DEFAULT_LENGTH_PASSWORD = 8;
    private final AccountRepository accountRepository;
    private final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder;
    private final AccountEntityToDtoMapper toDomainMapper;

    @Override
    public AccountResponse create(String accountId) {
        val account = accountRepository.findByAccountIdIgnoreCase(accountId);
        return account.map(a -> failedResponse()).orElseGet(() -> createAccount(accountId));
    }

    @Override
    public AccountDto account(UUID id) {
        return accountRepository.findById(id).map(toDomainMapper).orElse(null);
    }

    private AccountResponse createAccount(String accountId) {
        String generatedPassword = generatePassword();
        val saved = accountRepository.saveAndFlush(createEntity(accountId, generatedPassword));
        return AccountResponse.builder()
                .id(saved.id())
                .password(generatedPassword)
                .success(true)
                .description(SUCCESS_DESC)
                .build();
    }

    @Override
    public AccountEntity loggedAccount() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof String)
                .map(principal -> accountRepository.findByAccountIdIgnoreCase((String) principal))
                .flatMap(i -> i)
                .orElseThrow(() -> new RuntimeException("Account doesn't exist!"));
    }

    private AccountEntity createEntity(String accountId, String generatedPassword) {
        String hashedPassword = pbkdf2PasswordEncoder.encode(generatedPassword);
        return AccountEntity.builder().password(hashedPassword).accountId(accountId).build();
    }

    protected String generatePassword() {
        val characters = new StringBuilder(LOWER).append(UPPER).append(DIGITS).toString();
        return RandomStringUtils.random(DEFAULT_LENGTH_PASSWORD, characters);
    }

    private AccountResponse failedResponse() {
        return AccountResponse.builder()
                .success(false)
                .description(FAIL_DESC)
                .build();
    }

}
