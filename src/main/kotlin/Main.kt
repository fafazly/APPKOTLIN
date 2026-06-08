package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.response.*

import org.example.config.DatabaseConfig
import org.example.config.JwtConfig
import org.example.repository.*
import org.example.service.*
import org.example.routes.*
import org.example.models.ApiResponse

/**
 * MAIN - Único ponto de entrada da aplicação
 */
fun main() {
    // Inicializar banco de dados
    try {
        DatabaseConfig.init()
        println("✅ Banco de dados inicializado com sucesso!")
    } catch (e: Exception) {
        println("❌ Falha ao inicializar banco de dados!")
        return
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureServer()
    }.start(wait = true)
}

/**
 * Configuração do servidor Ktor
 */
fun Application.configureServer() {
    // Configuração de JSON
    install(ContentNegotiation) {
        json()
    }

    // Configuração de Autenticação JWT
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "app-kotlin-api"
            verifier(JwtConfig.getVerifier())
            validate { credential ->
                val email = credential.payload.getClaim("email").asString()
                if (email != null && email.isNotBlank()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiResponse(
                        success = false,
                        message = "Token inválido ou expirado"
                    )
                )
            }
        }
    }

    // Inicializar repositories e services
    val userRepository = UserRepository
    val postRepository = PostRepository
    val commentRepository = CommentRepository
    val notificationRepository = NotificationRepository

    val userService = UserService(userRepository)
    val postService = PostService(postRepository)
    val commentService = CommentService(commentRepository)
    val notificationService = NotificationService(notificationRepository)

    // Configurar rotas
    routing {
        // Health check
        get("/health") {
            call.respond(
                ApiResponse(
                    success = true,
                    message = "API está funcionando"
                )
            )
        }

        // Incluir todas as rotas
        userRoutes(userService)
        postRoutes(postService)
        commentRoutes(commentService)
        notificationRoutes(notificationService)
    }

    println("✅ Servidor Ktor iniciado em http://0.0.0.0:8080")
}

