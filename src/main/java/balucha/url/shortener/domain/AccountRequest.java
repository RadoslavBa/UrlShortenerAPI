package balucha.url.shortener.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class AccountRequest implements Serializable {

    @NotBlank(message = "Account ID cannot be blank!")
    @JsonProperty("AccountId")
    private String accountId;


}
