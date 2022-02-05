package es.juandavidvega.tweelete.business.action

import assertk.assertThat
import assertk.assertions.*
import assertk.thrownError
import es.juandavidvega.tweelete.business.model.Session
import es.juandavidvega.tweelete.business.model.SessionStatus
import es.juandavidvega.tweelete.business.model.errors.SessionNameIsEmptyError
import es.juandavidvega.tweelete.business.repository.SessionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test


class CreateSessionActionShould {


    @Test fun
    `Create and empty session`() {
        val sessionSlot = slot<Session>()
        val repository = mockk<SessionRepository>()
        every { repository.save(session = capture(sessionSlot)) } returns Unit

        val givenSessionName = "mySession"
        val givenUserId = "userId"

        CreateSessionAction(repository).execute(CreateSessionCommand(givenSessionName, givenUserId))

        assertThat(sessionSlot.captured.id).isNotEmpty()
        assertThat(sessionSlot.captured.name).isEqualTo(givenSessionName)
        assertThat(sessionSlot.captured.userId).isEqualTo(givenUserId)
        assertThat(sessionSlot.captured.status).isEqualTo(SessionStatus.Draft)
    }


    // FIXME this must be part of SessionShould
    @Test fun
    `Exception when creating a session with an empty name`() {
        val repository = mockk<SessionRepository>()

        val action = CreateSessionAction(repository)

        assertThat { action.execute(CreateSessionCommand("", "userId")) }
            .isFailure()
            .isInstanceOf(SessionNameIsEmptyError::class)
    }

    @Test fun
     `Exception when creating a session with an blank name`() {
        val repository = mockk<SessionRepository>()

        val action = CreateSessionAction(repository)

        assertThat { action.execute(CreateSessionCommand("          ", "userId")) }
            .isFailure()
            .isInstanceOf(SessionNameIsEmptyError::class)
    }

}
