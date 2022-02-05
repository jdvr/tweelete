package es.juandavidvega.tweelete.business.action

import es.juandavidvega.tweelete.business.action.errors.InvalidSessionIdError
import es.juandavidvega.tweelete.business.repository.SessionRepository

class AddRuleToSessionAction(private val repository: SessionRepository) {
    fun execute(command: AddRuleToSessionCommand) {
        val session = repository.get(command.sessionId) ?: throw InvalidSessionIdError(command.sessionId)
        session.updateRules(command.rules)
        repository.save(session)
    }

}
