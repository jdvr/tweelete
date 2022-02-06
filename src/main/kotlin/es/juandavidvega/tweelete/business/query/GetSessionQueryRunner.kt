package es.juandavidvega.tweelete.business.query

import es.juandavidvega.tweelete.business.action.errors.InvalidSessionIdError
import es.juandavidvega.tweelete.business.model.Session
import es.juandavidvega.tweelete.business.repository.SessionRepository

data class GetSessionQuery(val sessionId: String)

class GetSessionQueryRunner(private val repository: SessionRepository) {
    fun run(query: GetSessionQuery): Session {
        return repository.get(query.sessionId) ?: throw InvalidSessionIdError(query.sessionId)
    }
}
