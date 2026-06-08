package org.example.routes

import org.example.service.UserService
import org.example.models.*
import org.example.config.JwtConfig
import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

// ...existing code...

fun hashPassword(password: String): String {
    return BCrypt.withDefaults().hashToString(12, password.toCharArray())
}

fun verifyPassword(rawPassword: String, hashedPassword: String): Boolean {
    val result = BCrypt.verifyer().verify(rawPassword.toCharArray(), hashedPassword.toCharArray())
    return result.verified
}

fun Route.userRoutes(userService: UserService) {

    // ========== ROTAS PÚBLICAS ==========

    // Registrar usuário
    post("/users/register") {
        try {
            val request = call.receive<RegisterRequest>()

            // Validações
            if (request.email.isBlank() || request.password.isBlank() || request.name.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "Email, senha e nome são obrigatórios")
                )
                return@post
            }

            if (request.password.length < 6) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "A senha deve ter pelo menos 6 caracteres")
                )
                return@post
            }

            // Verificar se email já existe
            if (userService.emailExists(request.email)) {
                call.respond(
                    HttpStatusCode.Conflict,
                    ApiResponse(success = false, message = "Este email já está cadastrado")
                )
                return@post
            }

            // Hash da senha e criar usuário
            val hashedPassword = hashPassword(request.password)
            val user = userService.createUser(request.email, hashedPassword, request.name)

            if (user == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "Erro ao criar usuário")
                )
                return@post
            }

            call.respond(
                HttpStatusCode.Created,
                ApiResponse(
                    success = true,
                    message = "Usuário cadastrado com sucesso",
                    data = mapOf(
                        "email" to user.email,
                        "name" to user.name,
                        "id" to user.id.toString()
                    )
                )
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse(success = false, message = "Erro ao processar requisição: ${e.message}")
            )
        }
    }

    // Login
    post("/users/login") {
        try {
            val request = call.receive<LoginRequest>()

            if (request.email.isBlank() || request.password.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "Email e senha são obrigatórios")
                )
                return@post
            }

            val user = userService.getUserByEmail(request.email)
            if (user == null || !verifyPassword(request.password, user.hashedPassword)) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiResponse(success = false, message = "Email ou senha incorretos")
                )
                return@post
            }

            val token = JwtConfig.generateToken(user.email)
            call.respond(
                HttpStatusCode.OK,
                ApiResponse(
                    success = true,
                    message = "Login realizado com sucesso",
                    data = mapOf(
                        "email" to user.email,
                        "name" to user.name,
                        "id" to user.id.toString(),
                        "token" to token,
                        "expiresIn" to "10 horas"
                    )
                )
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse(success = false, message = "Erro ao processar requisição: ${e.message}")
            )
        }
    }

    // ========== ROTAS PROTEGIDAS (Requerem JWT) ==========

    authenticate("auth-jwt") {

        // Obter perfil do usuário
        get("/users/profile") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.payload?.getClaim("email")?.asString()

                if (email == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Email não encontrado no token")
                    )
                    return@get
                }

                val user = userService.getUserByEmail(email)
                if (user == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse(success = false, message = "Usuário não encontrado")
                    )
                    return@get
                }

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Perfil obtido com sucesso",
                        data = mapOf(
                            "id" to user.id.toString(),
                            "email" to user.email,
                            "name" to user.name
                        )
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(success = false, message = "Erro ao obter perfil: ${e.message}")
                )
            }
        }

        // Atualizar perfil
        put("/users/profile") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.payload?.getClaim("email")?.asString()

                if (email == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Email não encontrado no token")
                    )
                    return@put
                }

                val request = call.receive<UpdateProfileRequest>()

                if (request.name.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(success = false, message = "Nome não pode estar vazio")
                    )
                    return@put
                }

                userService.updateUserName(email, request.name)
                val updatedUser = userService.getUserByEmail(email)

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Perfil atualizado com sucesso",
                        data = mapOf(
                            "email" to updatedUser!!.email,
                            "name" to updatedUser.name
                        )
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "Erro ao atualizar perfil: ${e.message}")
                )
            }
        }

        // Alterar senha
        post("/users/change-password") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.payload?.getClaim("email")?.asString()

                if (email == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Email não encontrado no token")
                    )
                    return@post
                }

                val request = call.receive<ChangePasswordRequest>()
                val user = userService.getUserByEmail(email)

                if (user == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse(success = false, message = "Usuário não encontrado")
                    )
                    return@post
                }

                if (!verifyPassword(request.currentPassword, user.hashedPassword)) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Senha atual incorreta")
                    )
                    return@post
                }

                if (request.newPassword.length < 6) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(success = false, message = "A nova senha deve ter pelo menos 6 caracteres")
                    )
                    return@post
                }

                val hashedNewPassword = hashPassword(request.newPassword)
                userService.updatePassword(email, hashedNewPassword)

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(success = true, message = "Senha alterada com sucesso")
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "Erro ao alterar senha: ${e.message}")
                )
            }
        }

        // Deletar usuário
        delete("/users/{id}") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.payload?.getClaim("email")?.asString()

                if (email == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Não autorizado")
                    )
                    return@delete
                }

                val userId = call.parameters["id"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(success = false, message = "ID inválido")
                    )
                    return@delete
                }

                val success = userService.deleteUserById(userId)
                if (success) {
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(success = true, message = "Usuário deletado com sucesso")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse(success = false, message = "Erro ao deletar usuário")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "Erro ao processar requisição: ${e.message}")
                )
            }
        }

        // Listar usuários (apenas para teste)
        get("/users/list") {
            try {
                val allUsers = userService.getAllUsers()
                val userList = allUsers.map { user ->
                    mapOf(
                        "id" to user.id.toString(),
                        "email" to user.email,
                        "name" to user.name
                    )
                }

                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "success" to true,
                        "message" to "Lista de usuários obtida com sucesso",
                        "count" to userList.size,
                        "data" to userList
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(success = false, message = "Erro ao listar usuários: ${e.message}")
                )
            }
        }
    }
}


