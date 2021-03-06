package es.juandavidvega.tweelete.business.action

import assertk.assertThat
import assertk.assertions.*
import es.juandavidvega.tweelete.business.action.errors.InvalidSessionIdError
import es.juandavidvega.tweelete.business.model.RuleType
import es.juandavidvega.tweelete.business.model.Session
import es.juandavidvega.tweelete.business.model.TweetContainsRule
import es.juandavidvega.tweelete.business.model.TweetedAfterDateRule
import es.juandavidvega.tweelete.business.repository.SessionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import java.time.LocalDate


class AddRuleToSessionActionShould {


    @Test fun
    `Add rule to session`() {
        val givenSession = Session.createWith("dummySession", "anyUserId")
        val givenRules = listOf(
            TweetedAfterDateRule(LocalDate.now(), RuleType.Exclude),
            TweetContainsRule("test", RuleType.Include)
        )

        val repository = mockk<SessionRepository>()
        val sessionSlot = slot<Session>()
        every { repository.get(givenSession.id) } returns givenSession
        every { repository.save(session = capture(sessionSlot)) } returns Unit


        AddRuleToSessionAction(repository).execute(AddRuleToSessionCommand(givenSession.id, givenRules))

        assertThat(sessionSlot.isCaptured).isTrue()
        assertThat(sessionSlot.captured.id).isEqualTo(givenSession.id)
        assertThat(sessionSlot.captured.rules).containsExactlyInAnyOrder(*givenRules.toTypedArray())
    }

    @Test fun
    `Exception when session id is missing`() {
        val repository = mockk<SessionRepository>()
        every { repository.get(any()) } returns null

        val action = LoadTweetsAction(repository, mockk())

        assertThat { action.execute(LoadTweetsCommand("invalid")) }
            .isFailure()
            .isInstanceOf(InvalidSessionIdError::class)
    }
}
