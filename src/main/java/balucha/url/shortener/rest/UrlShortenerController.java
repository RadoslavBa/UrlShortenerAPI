package balucha.url.shortener.rest;

import balucha.url.shortener.domain.*;
import balucha.url.shortener.service.AccountService;
import balucha.url.shortener.service.HelpService;
import balucha.url.shortener.service.UrlService;
import io.vavr.Tuple2;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController("/")
@AllArgsConstructor
public class UrlShortenerController {

    private final AccountService accountService;
    private final UrlService urlService;
    private final HelpService helpService;

    @GetMapping("help")
    public HelpResponse help() {
        return helpService.help();
    }

    @PostMapping("account")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest accountRequest) {
        val saved = accountService.create(accountRequest.getAccountId());
        val location = URI.create(new StringBuilder("/account/").append(saved.id()).toString());
        return ResponseEntity.created(location).body(saved);

    }

    @GetMapping("account/{id}")
    public AccountDto createAccount(@PathVariable UUID id) {
        return accountService.account(id);
    }

    @PostMapping("register")
    public String registerUrl(@Valid @RequestBody UrlRequest urlRequest) {
        return urlService.convertToShortUrl(urlRequest);
    }

    @GetMapping("statistic/{AccountId}")
    public Map<String, Long> statistic(@PathVariable String AccountId) {
        return urlService.statistic(AccountId);
    }

    @GetMapping("{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {
        Tuple2<String, Integer> originalUrlAndRedirectType = urlService.getOriginalUrlAndRelatedRedirectType(shortUrl);
        String urlName = originalUrlAndRedirectType._1;
        int redirectType = originalUrlAndRedirectType._2;
        return ResponseEntity.status(redirectType)
                .location(URI.create(urlName))
                .build();
    }
}
