package pl.wj.ordermanager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.wj.ordermanager.user.model.dto.UserCredentialsDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@RequiredArgsConstructor
public class JsonObjectAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            return authenticaticateUser(request);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private Authentication authenticaticateUser(HttpServletRequest request) throws IOException {
        UserCredentialsDto userCredentialsDto = extractCredentialsFromRequest(request.getReader());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userCredentialsDto.getUsername(), userCredentialsDto.getPassword());
        setDetails(request, token);
        return this.getAuthenticationManager().authenticate(token);
    }

    private UserCredentialsDto extractCredentialsFromRequest(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return objectMapper.readValue(stringBuilder.toString(), UserCredentialsDto.class);
    }
}
