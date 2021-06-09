package balucha.url.shortener.service.impl;

import balucha.url.shortener.mapper.AccountEntityToDtoMapper;
import balucha.url.shortener.persistence.entity.AccountEntity;
import balucha.url.shortener.persistence.repository.AccountRepository;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class AccountServiceImplTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;


    @Before
    public void setUp() {
        accountService = new AccountServiceImpl(accountRepository, new Pbkdf2PasswordEncoder(), new AccountEntityToDtoMapper());
    }

    @Test
    public void shouldCreateAccount() {
        val accountId = "Rado";
        val savedAccountEntity = savedAccountEntity();

        ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);
        doReturn(Optional.empty()).when(accountRepository).findByAccountIdIgnoreCase(accountId);
        doReturn(savedAccountEntity).when(accountRepository).saveAndFlush(any(AccountEntity.class));

        val created = accountService.create(accountId);

        verify(accountRepository).saveAndFlush(captor.capture());
        val accountToSave = captor.getValue();

        assertThat(accountToSave.accountId()).isEqualTo(accountId);
        assertThat(created.id()).isEqualTo(savedAccountEntity.id());
        assertThat(created.success()).isTrue();
        assertThat(created.description()).isEqualTo("Your account is opened");
        assertThat(created.password()).isNotNull();
    }

    @Test
    public void shouldFailedBecauseOfExistingAccount() {
        val accountId = "Rado";

        doReturn(Optional.of(AccountEntity.builder().build())).when(accountRepository).findByAccountIdIgnoreCase(accountId);

        val response = accountService.create(accountId);

        assertThat(response.id()).isNull();
        assertThat(response.password()).isNull();
        assertThat(response.success()).isFalse();
        assertThat(response.description()).isEqualTo("Account already exists");
    }

    @Test
    public void shouldGeneratePassword() {
        val generatedPassword = accountService.generatePassword();

        assertThat(generatedPassword).hasSize(8);
        assertThat(generatedPassword).containsPattern("^[A-Za-z0-9]+$");
    }

    private AccountEntity savedAccountEntity() {
        return AccountEntity.builder()
                .id(UUID.randomUUID())
                .build();
    }

}