package cufa.conecta.com.config

import cufa.conecta.com.config.autenticacao.AutenticacaoEntryPoint
import cufa.conecta.com.config.autenticacao.AutenticacaoFilter
import cufa.conecta.com.resources.AutenticacaoRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguracao(
    private val autenticacaoRepository: AutenticacaoRepository,
    private val autenticacaoEntryPoint: AutenticacaoEntryPoint
) {
    companion object {
        private val ALLOWED_URLS =
            arrayOf(
                AntPathRequestMatcher("/swagger-ui/**"),
                AntPathRequestMatcher("/swagger-ui.html"),
                AntPathRequestMatcher("/swagger-resources/**"),
                AntPathRequestMatcher("/configuration/ui"),
                AntPathRequestMatcher("/configuration/security"),
                AntPathRequestMatcher("/public/**"),
                AntPathRequestMatcher("/webjars/**"),
                AntPathRequestMatcher("/v3-docs/**"),
                AntPathRequestMatcher("/actuator/*"),
                AntPathRequestMatcher("/users/**"),
                AntPathRequestMatcher("/roles/**"),
                AntPathRequestMatcher("/error/**"),
                AntPathRequestMatcher("/actuator/**"),
                AntPathRequestMatcher("/empresas/**"),
                AntPathRequestMatcher("/funcionarios/**"),
                AntPathRequestMatcher("/publicacoes/**"),
                AntPathRequestMatcher("/curriculos/**"),
                AntPathRequestMatcher("/candidaturas/**"),
                AntPathRequestMatcher("/experiencias/**"),
                AntPathRequestMatcher("/usuarios/**")
            )
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .headers { it.frameOptions { options -> options.disable() } }
            .cors { }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                it.anyRequest().permitAll()
            }
            .exceptionHandling {
                it.authenticationEntryPoint(autenticacaoEntryPoint)
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

        http.addFilterBefore(jwtAuthenticationFilterBean(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun jwtAuthenticationEntryPointBean(): AutenticacaoEntryPoint = AutenticacaoEntryPoint()

    @Bean
    fun jwtAuthenticationFilterBean(): AutenticacaoFilter {
        return AutenticacaoFilter(autenticacaoRepository, jwtAuthenticationUtilBean())
    }

    @Bean
    fun jwtAuthenticationUtilBean(): GerenciadorTokenJwt = GerenciadorTokenJwt()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:5173") // frontend
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        configuration.exposedHeaders = listOf("Authorization", "Content-Disposition")

        val urlBasedCorsSource = UrlBasedCorsConfigurationSource()
        urlBasedCorsSource.registerCorsConfiguration("/**", configuration)
        return urlBasedCorsSource
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager
}