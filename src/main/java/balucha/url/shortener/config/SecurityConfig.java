package balucha.url.shortener.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String REGEX_ACCOUNT_UUID_PATH = "^\\/account\\/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable()
                .and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/account", "/help").permitAll()
                .regexMatchers(REGEX_ACCOUNT_UUID_PATH).hasAuthority("USER")
                .antMatchers("/register").hasAuthority("USER")
                .anyRequest().authenticated()
                .and().httpBasic();
    }

}
