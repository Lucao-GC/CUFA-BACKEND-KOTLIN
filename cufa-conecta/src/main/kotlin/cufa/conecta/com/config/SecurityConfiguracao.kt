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
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
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
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .headers { it.frameOptions { options -> options.disable() } }
            .cors { }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                it.requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/configuration/ui",
                    "/configuration/security",
                    "/webjars/**",
                    "/v3-docs/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/public/**",
                    "/error",
                    "/error/**"
                ).permitAll()
                it.requestMatchers("/actuator/**").permitAll()
                // Cadastro e login (usuário e empresa)
                it.requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                it.requestMatchers(HttpMethod.POST, "/usuarios/login").permitAll()
                it.requestMatchers(HttpMethod.POST, "/usuarios/logout").permitAll()
                it.requestMatchers(HttpMethod.POST, "/empresas").permitAll()
                it.requestMatchers(HttpMethod.POST, "/empresas/login").permitAll()
                it.requestMatchers(HttpMethod.POST, "/empresas/logout").permitAll()
                // Listagem pública de vagas (somente GET na raiz; /publicacoes/empresa exige JWT de empresa)
                it.requestMatchers(HttpMethod.GET, "/publicacoes").permitAll()
                it.requestMatchers(HttpMethod.GET, "/empresas/all").permitAll()
                it.anyRequest().authenticated()
            }
            .exceptionHandling {
                it.authenticationEntryPoint(autenticacaoEntryPoint)
                // Sem JWT (ou expirado), o contexto fica anônimo: Spring devolveria 403; APIs usam 401 para o app limpar sessão.
                it.accessDeniedHandler { _, response, _ ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                }
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
        // Antes só `localhost:5173` — Expo Web (8081), outras portas e IP da LAN geravam bloqueio no browser.
        // `allowedOriginPatterns` aceita porta variável; `*` cobre origens não previstas em dev.
        configuration.allowedOriginPatterns = listOf(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "http://[::1]:*",
            "https://localhost:*",
            "https://127.0.0.1:*",
            "*"
        )
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
        configuration.allowedHeaders = listOf("*")
        // Com `*` nas origens, credenciais não podem ser true (regra do CORS).
        configuration.allowCredentials = false
        configuration.exposedHeaders =
            listOf("Authorization", "Content-Disposition", "Set-Cookie")

        val urlBasedCorsSource = UrlBasedCorsConfigurationSource()
        urlBasedCorsSource.registerCorsConfiguration("/**", configuration)
        return urlBasedCorsSource
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager
}