package es.juandavidvega.tweelete.business.action

data class CreateSessionCommand(val sessionName: String, val userId: String)
