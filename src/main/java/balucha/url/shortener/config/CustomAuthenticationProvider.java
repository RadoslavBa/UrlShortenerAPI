package balucha.url.shortener.config;

import balucha.url.shortener.persistence.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Configurable
@AllArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder;
    private final AccountRepository accountRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        val username = authentication.getPrincipal().toString();
        val password = authentication.getCredentials().toString();

        return accountRepository.findByAccountIdIgnoreCase(username)
                .filter(account -> pbkdf2PasswordEncoder.matches(password, account.password()))
                .map(account -> new UsernamePasswordAuthenticationToken(account.accountId(), null, Collections.singletonList(new SimpleGrantedAuthority("USER"))))
                .orElseGet(() -> new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(new SimpleGrantedAuthority("UNAUTHORIZED"))));

    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
