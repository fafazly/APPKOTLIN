package org.example.routes

import org.example.service.PostService
import org.example.models.ApiResponse
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Route.postRoutes(postService: PostService) {

    // Lista todos os posts (público)
    get("/posts") {
        try {
            val posts = postService.getAllPosts()
            call.respond(HttpStatusCode.OK, posts)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse(success = false, message = "Erro ao listar posts: ${e.message}")
            )
        }
    }

    // Posts de um usuário (público)
    get("/posts/user/{userId}") {
        try {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "UserId inválido")
                )
                return@get
            }
            val posts = postService.getPostsByUser(userId)
            call.respond(HttpStatusCode.OK, posts)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse(success = false, message = "Erro ao buscar posts: ${e.message}")
            )
        }
    }

    // Protegidas com autenticação JWT
    authenticate("auth-jwt") {
        delete("/posts/{id}") {
            try {
                val principal = call.principal<JWTPrincipal>()
                if (principal == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Não autorizado")
                    )
                    return@delete
                }

                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(success = false, message = "ID inválido")
                    )
                    return@delete
                }

                val success = postService.deletePost(id)
                if (success) {
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(success = true, message = "Post deletado com sucesso")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse(success = false, message = "Erro ao deletar post")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "Erro ao processar requisição: ${e.message}")
                )
            }
        }
    }
}
