package org.example.securityproject.config;

import org.example.securityproject.auth.RestAuthenticationEntryPoint;
import org.example.securityproject.repository.UserRepository;
import org.example.securityproject.services.CustomUserDetailsService;
import org.example.securityproject.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
// Ukljucivanje podrske za anotacije "@Pre*" i "@Post*" koje ce aktivirati autorizacione provere za svaki pristup metodi
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {
    @Value("${security.project.secret}")
    private String SECRET_KEY;

    @Autowired
    private UserRepository userRepository;

    // Servis koji se koristi za citanje podataka o korisnicima aplikacije
	@Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

     @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // 1. koji servis da koristi da izvuce podatke o korisniku koji zeli da se autentifikuje
        // prilikom autentifikacije, AuthenticationManager ce sam pozivati loadUserByUsername() metodu ovog servisa
        authProvider.setUserDetailsService(userDetailsService());
        // 2. kroz koji enkoder da provuce lozinku koju je dobio od klijenta u zahtevu 
        // da bi adekvatan hash koji dobije kao rezultat hash algoritma uporedio sa onim koji se nalazi u bazi (posto se u bazi ne cuva plain lozinka)
        authProvider.setPasswordEncoder(passwordEncoder());
    
        return authProvider;
    } 
    
     // Handler za vracanje 401 kada klijent sa neodogovarajucim korisnickim imenom i lozinkom pokusa da pristupi resursu
 	@Autowired
 	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    // Registrujemo authentication manager koji ce da uradi autentifikaciju korisnika za nas
 	@Bean
 	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
 	    return authConfig.getAuthenticationManager();
 	}
 	
	// Injektujemo implementaciju iz TokenUtils klase kako bismo mogli da koristimo njene metode za rad sa JWT u TokenAuthenticationFilteru
	@Autowired
	private TokenUtils tokenUtils;
	
	// Definisemo prava pristupa za zahteve ka odredjenim URL-ovima/rutama
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Koristi stateless sesiju jer je REST API
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Postavljanje handler-a za 401 grešku ako autentifikacija nije uspešna
        http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);

        http.authorizeRequests()
            // Dozvoljeni endpoint-i bez autentifikacije
            .antMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            .antMatchers(HttpMethod.POST, "/api/users/registerUser").permitAll() // Omogućava registraciju korisnika bez autentifikacije
            .antMatchers(HttpMethod.GET, "/api/users/confirm-account").permitAll() // Omogućava potvrdu korisničkog naloga bez autentifikacije
            .antMatchers(HttpMethod.POST, "/api/users/resetPassword").permitAll() // Omogućava resetovanje lozinke bez autentifikacije
            .anyRequest().authenticated() // Za sve ostale rute potrebno je biti autentifikovan
            .and()
            .cors().and()
            .addFilterBefore(new TokenAuthenticationFilter(tokenUtils, userDetailsService(), userRepository), BasicAuthenticationFilter.class);

        http.csrf().disable(); // Onemogućava CSRF zaštitu (za API aplikacije je često potrebno)

        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    // Definisanje statičkih resursa i nezaštićenih ruta
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .antMatchers("/v3/api-docs/**", "/webjars/**", "/**/*.html", "/**/*.css", "/**/*.js") 
            .antMatchers("/favicon.ico"); 
    }
}
