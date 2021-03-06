package com.ara.memo.dao

import com.ara.memo.entity.Entity
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ReactiveDao<T: Entity<ID>, ID> {
    fun <S : T> save(entity: S): Mono<S>

    fun <S : T> saveAll(entities: Iterable<S>): Flux<S>

    fun <S : T> saveAll(entityStream: Publisher<S>): Flux<S>

    fun findById(id: ID): Mono<T>

    fun findById(id: Publisher<ID>): Mono<T>

    fun existsById(id: ID): Mono<Boolean>

    fun existsById(id: Publisher<ID>): Mono<Boolean>

    fun findAll(): Flux<T>

    fun findAllById(ids: Iterable<ID>): Flux<T>

    fun findAllById(idStream: Publisher<ID>): Flux<T>

    fun count(): Mono<Long>

    fun deleteById(id: ID): Mono<Unit>

    fun deleteById(id: Publisher<ID>): Mono<Unit>

    fun delete(entity: T): Mono<Unit>

    fun deleteAll(entities: Iterable<T>): Mono<Unit>

    fun deleteAll(entityStream: Publisher<out T>): Mono<Unit>

    fun deleteAll(): Mono<Unit>
}