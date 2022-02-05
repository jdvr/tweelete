package es.juandavidvega.tweelete.business.repository

import es.juandavidvega.tweelete.business.model.Session
import java.util.Optional

interface SessionRepository {
    fun save(session: Session)
    fun get(id: String): Session?
    fun loadAllBy(userId: String): List<Session>
}
