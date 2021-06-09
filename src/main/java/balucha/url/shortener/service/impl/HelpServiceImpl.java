package balucha.url.shortener.service.impl;

import balucha.url.shortener.domain.HelpResponse;
import balucha.url.shortener.service.HelpService;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class HelpServiceImpl implements HelpService {

    @Override
    public HelpResponse help() {
        val map = new HashMap<String, String>();
        map.put("/account", "POST -> "  + createAccount());
        map.put("/account/{id}", "GET");
        map.put("/register", "POST -> " + register());
        map.put("/{shortUrl}", "GET");
        map.put("/statistic/{AccountId}", "GET");
        return HelpResponse.builder()
                .title(title())
                .description(description())
                .endpoints(map)
                .build();
    }

    private String title() {
        return "URL Shortener Application";
    }

    private String description() {
        return "URL Shortener Application is used to register shortened URL with created account. It uses SpringBoot framework and h2 database." +
                " To build and run application use command 'mvn clean install' and run without any attributes on embedded tomcat." +
                "\n" +
                "Endpoints /account and /help are available for all. The rest of endpoints /account/{id}, /register, /{shortUrl} " +
                "are available only with set Authorization Header." +
                "\n" +
                "Register request body contains optional attribute 'rootUrlName' to set root url name. http://localhost:8080 is by default.";
    }

    private String createAccount() {
        return "{\"AccountId\": \"Rado\"}";
    }

    private String register(){
        return "{\"URL\": \"http://myWeb/ahoj\", \"rootUrlName\": \"http://localhost:8080/\"}";
    }
}
