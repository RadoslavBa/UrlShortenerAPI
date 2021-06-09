package balucha.url.shortener.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Accessors(fluent = true)
@Builder
@JsonAutoDetect(fieldVisibility = ANY)
@JsonInclude(NON_NULL)
@AllArgsConstructor
public class HelpResponse implements Serializable {

    private final String title;
    private final String description;
    private final Map<String, String> endpoints;

}
