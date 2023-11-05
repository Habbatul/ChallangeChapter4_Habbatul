package com.habbatul.challange4.security;


import com.habbatul.challange4.enums.ERole;
import com.habbatul.challange4.service.security.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



/***
 * disini saya coba implementasi dari <a href="https://www.baeldung.com/spring-deprecated-websecurityconfigureradapter">...</a>
 * karena WebSecurityConfigurerAdapter deprecated sehingga disarankan membuat manual
 */

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    AuthEntryPointJwt unauthorizedHandler;

//   gausah makek manager nya bikin sendiri aja
//    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests()
                .antMatchers( "/auth/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**", "/error").permitAll()

                //akses semua user
                .antMatchers(HttpMethod.GET, "/merchant","/product").permitAll()
                .antMatchers("/auth/**").permitAll()

                //akses customer
                .antMatchers(HttpMethod.PUT, "/user/**").hasAuthority(ERole.CUSTOMER.name())
                .antMatchers(HttpMethod.DELETE, "/user/**").hasAuthority(ERole.CUSTOMER.name())
                .antMatchers( "/order/**/**").hasAuthority(ERole.CUSTOMER.name())

                //akses merhcant
                .antMatchers("/product/**", "/merchant/**").hasAuthority(ERole.MERCHANT.name())

                .anyRequest()
                .authenticated()
                .and()
                //pakai header bearer
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                //pakai cookies
                .addFilterBefore(cookieBasedAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    //pakai cookies
    @Bean
    public CookieBasedAuthenticationFilter cookieBasedAuthenticationFilter() {
        return new CookieBasedAuthenticationFilter();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    //manager buatan sendiri tanap webconfigureradapter

    /***
     * Sebenarnya parameter ada 4 tapi karena sudah didefinisikan dengan method
     * maka cukup satu saja yaoitu HttpSecurity
     * aslinya ada (HttpSecurity, BCryptPasswordEncoder, UserDetailService)
     * tapi bcrypt pakek method, dan userdetail service
     * sudah pakai autowired diatas, wokey
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                //ambil dari autowired diatas
                .userDetailsService(userDetailsService)
                //ambil dari method dibawah
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}