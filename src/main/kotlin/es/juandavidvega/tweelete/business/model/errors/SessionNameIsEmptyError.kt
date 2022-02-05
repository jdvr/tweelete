package es.juandavidvega.tweelete.business.model.errors

import java.lang.RuntimeException

class SessionNameIsEmptyError: RuntimeException("Invalid session name with length 0")
