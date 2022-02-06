package es.juandavidvega.tweelete.business.model

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import es.juandavidvega.tweelete.business.model.tweet.Tweet
import es.juandavidvega.tweelete.business.model.tweet.TweetType
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SessionShould {

    @Test
    fun `Session filter out duplicate rules`() {
        val givenContainsRule = TweetContainsRule("anyText", RuleType.Exclude)
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

    @Test
    fun `Session only storage tweets that matches the rules`() {
        val givenRules = listOf(
            TweetContainsRule("anyText", RuleType.Exclude),
            TweetContainsRule("otherText", RuleType.Exclude),
        )
        val givenValidTweet = Tweet("validTweetId", "---anyText---", TweetType.Tweet)
        val givenOtherValidTweet = Tweet("validTweetId2", "---otherText---", TweetType.Like)
        val givenTweets = listOf(
            givenValidTweet,
            Tweet("invalidTweetId", "whatever", TweetType.Tweet),
            Tweet("invalidTweetId2", "whatever", TweetType.Like),
            Tweet("invalidTweetId3", "whatever", TweetType.Tweet)

        )

        val session = createSessionWith(givenRules)

        session.process(givenTweets)

        assertThat(session.tweets).containsExactlyInAnyOrder(givenValidTweet)

        session.process(listOf(givenOtherValidTweet))

        assertThat(session.tweets).containsExactlyInAnyOrder(givenValidTweet, givenOtherValidTweet)
    }

    private fun createSessionWith(givenRules: List<Rule> = emptyList()): Session {
        val session = Session.createWith("anyName", "anyUserId")
        session.updateRules(givenRules)
        return session
    }
}

