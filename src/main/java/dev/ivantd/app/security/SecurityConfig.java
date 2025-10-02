
package dev.ivantd.app.security;

import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LogManager.getLogger(SecurityConfig.class);    
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        logger.info("Configurando la seguridad de la aplicación");
        
        httpSecurity.authorizeHttpRequests(auth -> auth
							    
			// Los recursos estaticos no requieren autenticacion
			//.requestMatchers("/js/**", "/adminlte/**",  "/css/**", "/images/**", "/ajax/**", "/pruebas/**").permitAll()
			.requestMatchers("/js/**", "/adminlte/**",  "/css/**", "/images/**", "/files/codigosqr/**", "/pruebas/**").permitAll()
			
			// Todas las demas URLs de la Aplicacion requieren autenticacion
			.anyRequest().authenticated()
		);
		
		httpSecurity.sessionManagement(session -> {
			session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
			.invalidSessionUrl("/formLogin")
			// Si el valor es 1, al iniciar sesion en el navegador 1, al iniciar sesion en el navegador 2, se destruye la sesion del navegador 1.
			.maximumSessions(3).expiredUrl("/formLogin")
			// Habilitar el objeto 'sessionRegistry()' para hacer un rastreo de los usuarios autenticados.
			.sessionRegistry(sessionRegistry());			
			session.sessionFixation().migrateSession();
		});
		
		// Configuracion del formulario de login personalizado
		httpSecurity.formLogin(form -> form.loginPage("/formLogin").loginProcessingUrl("/ingresar").defaultSuccessUrl("/", true).permitAll());
		
		// Configuracion de la URL personalizada para cerrar la sesión
		httpSecurity.logout(logout -> logout.logoutUrl("/salir").permitAll());
		
		// Para esta url que es publica y se enviara un formulario tipo POST, por eso deshabilitamos el csrf 
	    httpSecurity.csrf(csrf -> csrf.ignoringRequestMatchers("/respuestas/guardarRespuesta", "/rechazados/guardarRespuesta", "/webhook"));
							
		return httpSecurity.build();
		
    }

	@Bean
	UserDetailsManager users(DataSource dataSource) {
		JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
		users.setUsersByUsernameQuery("select u.username, u.password, u.estatus from Usuarios u where u.username = ?");
		users.setAuthoritiesByUsernameQuery("select u.username, p.perfil from UsuariosPerfiles up " + 
											"inner join Usuarios u on u.id = up.idUsuario " + 
											"inner join Perfiles p on p.id = up.idPerfil " +
											"where u.username = ?");
		
		return users;
	}

	@Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Bean PasswordEncoder (BCrypt) creado");
        return new BCryptPasswordEncoder();
    }

	 @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
