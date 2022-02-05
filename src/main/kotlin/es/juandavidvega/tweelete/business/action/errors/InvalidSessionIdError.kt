package es.juandavidvega.tweelete.business.action.errors

import java.lang.RuntimeException

class InvalidSessionIdError(id: String) : RuntimeException("Invalid session id: $id")
