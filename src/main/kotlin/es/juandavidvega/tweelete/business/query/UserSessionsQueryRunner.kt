package es.juandavidvega.tweelete.business.query

import es.juandavidvega.tweelete.business.model.Session
import es.juandavidvega.tweelete.business.repository.SessionRepository

class UserSessionsQueryRunner(private val sessionRepository: SessionRepository) {
    fun Run(query: UserSessionsQuery): List<Session> {
        return sessionRepository.loadAllBy(query.userId)
    }
}

data class UserSessionsQuery(val userId: String)


