package es.juandavidvega.tweelete.business.model.errors

import java.lang.RuntimeException

class SessionUserIdIsEmptyError: RuntimeException("Invalid userId with length 0")
