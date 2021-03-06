package com.ara.memo.service.user

import com.ara.memo.dao.user.UserDao
import com.ara.memo.entity.user.User
import com.ara.memo.service.user.exception.UserNotExistException
import com.ara.memo.util.patch.Patch
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserResource(private val dao: UserDao) {
    fun createAll(users: Iterable<User>) = dao.saveAll(users)

    fun create(user: User) = dao.save(user)

    fun updateById(id: String, patch: Patch<User>) = findById(id).flatMap { update(it, patch) }

    fun updateByUsername(username: String, patch: Patch<User>) = findByUsername(username).flatMap { update(it, patch) }

    fun update(user: User, patch: Patch<User>) = Mono.fromCallable { patch.apply(user) }.flatMap { dao.save(it) }

    fun deleteAll() = dao.deleteAll()

    fun deleteById(id: String) = dao.existsById(id)
        .flatMap {
            when (it) {
                true -> dao.deleteById(id)
                false -> Mono.error(UserNotExistException)
            }
        }

    fun deleteByUsername(username: String) = dao.existsByUsername(username)
        .flatMap {
            when (it) {
                true -> dao.deleteByUsername(username)
                false -> Mono.error(UserNotExistException)
            }
        }

    fun findAll() = dao.findAll()

    fun find(user: User) = when (user.id) {
        null -> findByUsername(user.username)
        else -> findById(user.id)
    }

    fun findById(id: String) = dao.findById(id)
        .switchIfEmpty(Mono.error(UserNotExistException))

    fun findByUsername(username: String) = dao.findByUsername(username)
        .switchIfEmpty(Mono.error(UserNotExistException))

    fun existsById(id: String) = dao.existsById(id)

    fun existsByUsername(username: String) = dao.existsByUsername(username)
}