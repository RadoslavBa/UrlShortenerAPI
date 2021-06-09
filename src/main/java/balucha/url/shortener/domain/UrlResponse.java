package balucha.url.shortener.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Accessors(fluent = true)
@Builder
@JsonAutoDetect(fieldVisibility = ANY)
@JsonInclude(NON_NULL)
@AllArgsConstructor
public class UrlResponse implements Serializable {

    private final String shortUrl;
}
