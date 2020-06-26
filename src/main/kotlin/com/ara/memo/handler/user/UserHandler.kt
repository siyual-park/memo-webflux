package com.ara.memo.handler.user

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest

@Component
class UserHandler(
    private val createUserHandler: CreateUserHandler,
    private val readUsersHandler: ReadUsersHandler,
    private val readUserByIdHandler: ReadUserByIdHandler,
    private val updateUserByIdHandler: UpdateUserByIdHandler
) {
    fun create(request: ServerRequest) = createUserHandler.handleRequest(request)

    fun readAll(request: ServerRequest) = readUsersHandler.handleRequest(request)
    fun readById(request: ServerRequest) = readUserByIdHandler.handleRequest(request)

    fun updateById(request: ServerRequest) = updateUserByIdHandler.handleRequest(request)
}