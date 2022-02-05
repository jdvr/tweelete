package es.juandavidvega.tweelete.business.action

import es.juandavidvega.tweelete.business.model.Session
import es.juandavidvega.tweelete.business.repository.SessionRepository

class CreateSessionAction(private val repository: SessionRepository) {
    fun execute(command: CreateSessionCommand) {
        repository.save(Session.createWith(command.sessionName, ""))
    }
}
