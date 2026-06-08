package org.example.routes

import org.example.service.NotificationService
import org.example.models.ApiResponse
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Route.notificationRoutes(notificationService: NotificationService) {

    // Protegidas com autenticação JWT
    authenticate("auth-jwt") {

        // Obter notificações do usuário
        get("/notifications/{userId}") {
            try {
                val principal = call.principal<JWTPrincipal>()
                if (principal == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Não autorizado")
                    )
                    return@get
                }

                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(success = false, message = "UserId inválido")
                    )
                    return@get
                }

                val notifications = notificationService.getNotificationsByUser(userId)
                call.respond(HttpStatusCode.OK, notifications)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(success = false, message = "Erro ao buscar notificações: ${e.message}")
                )
            }
        }

        // Marcar notificação como lida
        put("/notifications/{id}/read") {
            try {
                val principal = call.principal<JWTPrincipal>()
                if (principal == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(success = false, message = "Não autorizado")
                    )
                    return@put
                }

                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(success = false, message = "ID inválido")
                    )
                    return@put
                }

                val success = notificationService.markAsRead(id)
                if (success) {
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(success = true, message = "Notificação marcada como lida")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse(success = false, message = "Erro ao atualizar notificação")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = "Erro ao processar requisição: ${e.message}")
                )
            }
        }

        // Deletar notificação
        delete("/notifications/{id}") {
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

                val success = notificationService.deleteNotification(id)
                if (success) {
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(success = true, message = "Notificação deletada com sucesso")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse(success = false, message = "Erro ao deletar notificação")
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
