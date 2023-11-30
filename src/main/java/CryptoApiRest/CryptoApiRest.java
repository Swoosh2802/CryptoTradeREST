package CryptoApiRest;

import CryptoApiRest.security.JWTAuthorizationFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class CryptoApiRest extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(CryptoApiRest.class,args);
    }

    @EnableWebSecurity
    @Configuration
    class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/users/login",
                                                            "/users/insertUser",
                                                            "/users/isConnected",
                                                            "/users/loginWithGmail",
                                                            "/contact/send").permitAll()
                    .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/cryptos", "/wallets/getClassement").permitAll()
                    .anyRequest().authenticated();
        }
    }
}

