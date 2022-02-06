package es.juandavidvega.tweelete.business.model.errors

import es.juandavidvega.tweelete.business.model.SessionStatus
import java.lang.RuntimeException

class InvalidSessionStatusForRun(currentStatus: SessionStatus) : RuntimeException(
    "cannot run $currentStatus session, only ${SessionStatus.Draft} and ${SessionStatus.Pending} are runnable"
)
