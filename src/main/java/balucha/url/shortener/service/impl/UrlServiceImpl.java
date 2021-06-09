package balucha.url.shortener.service.impl;

import balucha.url.shortener.component.UuidUrlConverter;
import balucha.url.shortener.domain.UrlRequest;
import balucha.url.shortener.domain.UrlResponse;
import balucha.url.shortener.exception.ValidationException;
import balucha.url.shortener.persistence.entity.AccountEntity;
import balucha.url.shortener.persistence.entity.UrlEntity;
import balucha.url.shortener.persistence.repository.AccountRepository;
import balucha.url.shortener.persistence.repository.UrlRepository;
import balucha.url.shortener.service.AccountService;
import balucha.url.shortener.service.UrlService;
import io.vavr.API;
import io.vavr.Tuple2;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@AllArgsConstructor
public class UrlServiceImpl implements UrlService {

    private static final String ROOT_URL_NAME = "http://localhost:8080/";
    private static final int DEFAULT_REDIRECT_TYPE = 302;
    private static final List<Integer> ALLOWED_REDIRECT_TYPE = Arrays.asList(301, DEFAULT_REDIRECT_TYPE);
    private final UuidUrlConverter uuidUrlConverter;
    private final UrlRepository urlRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Override
    public UrlResponse convertToShortUrl(UrlRequest urlRequest) {
        AccountEntity loggedAccount = accountService.loggedAccount();
        // filter non-existing url for logged account
        Optional<UrlEntity> urlEntity = Optional.of(urlRequest)
                .filter(request -> notExistingUrl(loggedAccount, request))
                .map(this::createEntity);

        UUID uuidHex = urlEntity
                .map(entity -> save(entity, loggedAccount))
                .map(UrlEntity::id)
                .orElseGet(() -> existingUrlId(urlRequest));
        String uuid64 = uuidUrlConverter.uuidHextoUuid64(uuidHex);
        return UrlResponse.builder().shortUrl(new StringBuilder(rootUrlName(urlRequest)).append(uuid64).toString()).build();
    }

    @Override
    public Tuple2<String, Integer> getOriginalUrlAndRelatedRedirectType(String shortUrl) {
        UUID uuidHex = uuidUrlConverter.uuid64toUuidHex(shortUrl);
        return urlRepository.findById(uuidHex)
                .filter(url -> isAuthorizedForThisAction(url.account()))
                .map(UrlEntity::increaseCallNumber)
                .map(urlRepository::saveAndFlush)
                .map(url -> API.Tuple(url.name(), url.redirectType()))
                .orElseThrow(() -> new ValidationException("Url doesn't exist!"));
    }

    @Override
    public Map<String, Long> statistic(String accountId) {
        val account = accountRepository.findByAccountIdIgnoreCase(accountId);
        return account
                .filter(this::isAuthorizedForThisAction)
                .map(a -> a.urls().stream().collect(Collectors.toMap(UrlEntity::name, UrlEntity::callNumber)))
                .orElse(null);
    }

    private UrlEntity save(UrlEntity urlToSaved, AccountEntity loggedAccount) {
//        val account = accountRepository.saveAndFlush(loggedAccount.addUrl(urlToSaved));
        return urlRepository.saveAndFlush(urlToSaved.toBuilder().account(loggedAccount).build());
    }

    private boolean notExistingUrl(AccountEntity loggedAccount, UrlRequest request) {
        return loggedAccount.urls().isEmpty() || !getUrlNamesRelatedToLoggedUser(loggedAccount).contains(request.url());
    }

    private String rootUrlName(UrlRequest urlRequest) {
        return Objects.isNull(urlRequest.rootUrlName()) ? ROOT_URL_NAME : urlRequest.rootUrlName();
    }

    private UUID existingUrlId(UrlRequest urlRequest) {
        return urlRepository.findByNameIgnoreCase(urlRequest.url()).map(UrlEntity::id).orElse(null);
    }

    private boolean isAuthorizedForThisAction(AccountEntity accountEntity) {
        if (!Objects.equals(accountEntity.id(), accountService.loggedAccount().id())) {
            throw new ValidationException("You are not authorized for this action!");
        }
        return true;
    }

    private List<String> getUrlNamesRelatedToLoggedUser(AccountEntity account) {
        return account.urls().stream().map(UrlEntity::name).collect(toList());
    }

    private UrlEntity createEntity(UrlRequest urlRequest) {
        return UrlEntity.builder()
                .name(urlRequest.url())
                .redirectType(redirectType(urlRequest))
                .build();
    }

    private int redirectType(UrlRequest urlRequest) {
        val redirectType = urlRequest.redirectType();
        if (Objects.nonNull(redirectType) && (!ALLOWED_REDIRECT_TYPE.contains(redirectType))) {
            throw new ValidationException("Redirect type can be only: " + ALLOWED_REDIRECT_TYPE.toString());
        }
        return Objects.isNull(urlRequest.redirectType()) ? DEFAULT_REDIRECT_TYPE : urlRequest.redirectType();
    }

}
