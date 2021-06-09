package balucha.url.shortener.service.impl;

import balucha.url.shortener.component.UuidUrlConverter;
import balucha.url.shortener.domain.UrlRequest;
import balucha.url.shortener.persistence.entity.AccountEntity;
import balucha.url.shortener.persistence.entity.UrlEntity;
import balucha.url.shortener.persistence.repository.AccountRepository;
import balucha.url.shortener.persistence.repository.UrlRepository;
import balucha.url.shortener.service.AccountService;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UrlServiceImplTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private UrlServiceImpl urlService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private UrlRepository urlRepository;


    @Before
    public void setUp() {
        urlService = spy(new UrlServiceImpl(new UuidUrlConverter(), urlRepository, accountRepository, accountService));
    }

    @Test
    public void shouldRegisterNewUrl() {
        val request = UrlRequest.builder().url("www.example.com/test").redirectType(301).rootUrlName("http://example.com/").build();
        ArgumentCaptor<UrlEntity> urlEntityCaptor = ArgumentCaptor.forClass(UrlEntity.class);

        doReturn(AccountEntity.builder().build()).when(accountService).loggedAccount();
        doReturn(savedUrlEntity()).when(urlRepository).saveAndFlush(ArgumentMatchers.any(UrlEntity.class));

        val convertedUrl = urlService.convertToShortUrl(request);

        verify(urlRepository).saveAndFlush(urlEntityCaptor.capture());
        val urlValue = urlEntityCaptor.getValue();

        assertThat(urlValue.name()).contains("www.example.com/test");
        assertThat(convertedUrl).contains("http://example.com/");

    }

    @Test
    public void shouldGetExistingUrlForLoggedUser() {
        val request = UrlRequest.builder().url("www.example.com/test").redirectType(301).rootUrlName("http://example.com/").build();
        val urlEntity = urlEntity();

        doReturn(AccountEntity.builder().urls(singleton(urlEntity)).build()).when(accountService).loggedAccount();
        doReturn(Optional.of(urlEntity)).when(urlRepository).findByNameIgnoreCase("www.example.com/test");

        val convertedUrl = urlService.convertToShortUrl(request);
        assertThat(convertedUrl).contains("http://example.com/");
    }

    private UrlEntity urlEntity() {
        return UrlEntity.builder().id(UUID.randomUUID()).name("www.example.com/test").build();
    }

    @Test
    public void shouldGetStatisticsForLoggedUser() {
        val accountUuid = UUID.randomUUID();
        val accountId = "Rado";
        val account  = AccountEntity.builder().urls(singleton(urlEntity())).id(accountUuid).build();

        doReturn(Optional.of(account)).when(accountRepository).findByAccountIdIgnoreCase(accountId);
        doReturn(account).when(accountService).loggedAccount();

        val statistics = urlService.statistic(accountId);

        assertThat(statistics).hasSize(1);
        assertThat(statistics.get("www.example.com/test")).isEqualTo(0);
    }

    @Test
    public void shouldGetOriginUrl() {
        val accountUuid = UUID.randomUUID();
        val accountId = "Rado";
        val account  = AccountEntity.builder().urls(singleton(urlEntity())).id(accountUuid).build();

        doReturn(Optional.of(account)).when(accountRepository).findByAccountIdIgnoreCase(accountId);
        doReturn(account).when(accountService).loggedAccount();

        val statistics = urlService.statistic(accountId);

        assertThat(statistics).hasSize(1);
        assertThat(statistics.get("www.example.com/test")).isEqualTo(0);
    }

    private UrlEntity savedUrlEntity() {
        return UrlEntity.builder()
                .id(UUID.randomUUID())
                .build();
    }


}