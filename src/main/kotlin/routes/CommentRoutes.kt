package org.example.routes

import org.example.service.CommentService
import org.example.models.ApiResponse
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Route.commentRoutes(commentService: CommentService) {

    // Obter comentários de um post (público)
    get("/comments/{postId}") {
        try {
            val postId = call.parameters["postId"]?.toIntOrNull()
            if (postId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "PostId inválido")
                )
                return@get
            }
            val comments = commentService.getCommentsByPost(postId)
            call.respond(HttpStatusCode.OK, comments)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse(success = false, message = "Erro ao buscar comentários: ${e.message}")
            )
        }
    }

    // Protegidas com autenticação JWT
    authenticate("auth-jwt") {
        delete("/comments/{id}") {
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

                val success = commentService.deleteComment(id)
                if (success) {
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(success = true, message = "Comentário deletado com sucesso")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse(success = false, message = "Erro ao deletar comentário")
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
