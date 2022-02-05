package es.juandavidvega.tweelete.business.model

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SessionShould {

    @Test
    fun `Session filter out duplicate rules`() {
        val givenContainsRule = TweetContains("anyText", RuleType.Exclude)
        val initialRules = listOf(
            givenContainsRule,
            NumberOfLikesSmallerThan(90, RuleType.Include)
        )
        val moreRules = listOf(
            givenContainsRule,
            TweetedBeforeDateRule(LocalDate.now(), RuleType.Exclude)
        )
        val session = Session.createWith("anyName", "anyUserId")

        session.updateRules(initialRules)
        session.updateRules(moreRules)

        assertThat(session.rules).containsExactlyInAnyOrder(
            givenContainsRule,
            NumberOfLikesSmallerThan(90, RuleType.Include),
            TweetedBeforeDateRule(LocalDate.now(), RuleType.Exclude)
        )
    }
}

