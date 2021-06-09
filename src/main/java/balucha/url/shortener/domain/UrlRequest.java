package balucha.url.shortener.domain;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder(toBuilder = true)
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = ANY)
@NoArgsConstructor(access = PROTECTED, force = true)
@AllArgsConstructor(access = PRIVATE)
public class UrlRequest implements Serializable {

    @NotBlank(message = "URL cannot be blank!")
    @JsonProperty("URL")
    private String url;

    private Integer redirectType;

    private String rootUrlName;
}
