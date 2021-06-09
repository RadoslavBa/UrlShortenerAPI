package balucha.url.shortener.component;

import lombok.val;
import org.apache.commons.lang3.Conversion;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.UUID;

@Component
public class UuidUrlConverter {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    public String uuidHextoUuid64(UUID uuid) {
        val bytes = Conversion.uuidToByteArray(uuid, new byte[16], 0, 16);
        return BASE64_URL_ENCODER.encodeToString(bytes);
    }

    public UUID uuid64toUuidHex(String url) {
        val decoded = Base64.getUrlDecoder().decode(url);
        return Conversion.byteArrayToUuid(decoded, 0);
    }
}
