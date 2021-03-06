package com.ara.memo.handler.error

import com.ara.memo.dto.error.factory.ErrorViews
import com.ara.memo.util.mapper.ErrorMapper
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.result.view.ViewResolver
import reactor.core.publisher.Mono

@Component
@Order(-2)
class ErrorWebExceptionHandler(
    resourceProperties: ResourceProperties,
    applicationContext: ApplicationContext,
    viewResolversProvider: ObjectProvider<List<ViewResolver>>,
    serverCodecConfigurer: ServerCodecConfigurer,
    private val errorMapper: ErrorMapper
) : AbstractErrorWebExceptionHandler(DefaultErrorAttributes(), resourceProperties, applicationContext) {
    init {
        setViewResolvers(viewResolversProvider.getIfAvailable { emptyList() })
        setMessageWriters(serverCodecConfigurer.writers)
        setMessageReaders(serverCodecConfigurer.readers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes) = RouterFunctions.route(
        RequestPredicates.all(), HandlerFunction { request -> handleErrorRequest(request) }
    )

    private fun handleErrorRequest(request: ServerRequest): Mono<ServerResponse> {
        val errorAttributes = getErrorAttributes(request, false)
        val status = errorAttributes["status"] as Int
        val path = errorAttributes["path"] as String
        val error = errorMapper.map(getError(request))

        return ServerResponse.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(
                ErrorViews.of(
                    path,
                    errorAttributes["error"] as String,
                    error
                )
            ))
    }
}
