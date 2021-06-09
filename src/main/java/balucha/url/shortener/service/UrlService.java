package balucha.url.shortener.service;

import balucha.url.shortener.domain.UrlRequest;
import io.vavr.Tuple2;

import java.util.Map;

public interface UrlService {

    String convertToShortUrl(UrlRequest urlRequest);

    Tuple2<String, Integer> getOriginalUrlAndRelatedRedirectType(String shortUrl);

    Map<String, Long> statistic(String accountId);
}
