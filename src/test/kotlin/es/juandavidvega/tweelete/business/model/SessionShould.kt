package es.juandavidvega.tweelete.business.model

import assertk.assertThat
import assertk.assertions.*
import es.juandavidvega.tweelete.business.action.CreateSessionCommand
import es.juandavidvega.tweelete.business.model.errors.InvalidSessionStatusForRun
import es.juandavidvega.tweelete.business.model.errors.SessionNameIsEmptyError
import es.juandavidvega.tweelete.business.model.tweet.Tweet
import es.juandavidvega.tweelete.business.model.tweet.TweetType
import io.mockk.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertFails

class SessionShould {

    @Test fun
    `filter out duplicate rules`() {
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

    @Test fun
    `storages all tweets for empty rules`() {
        val givenTweets = listOf(
            Tweet("validTweetId", "---anyText---", TweetType.Tweet),
            Tweet("anotherTweetId", "---otherText---", TweetType.Like)
        )

        val session = createSessionWith(name = "anyName", userId = "anyUserId")
        session.process(givenTweets)

        assertThat(session.tweets).hasSize(2)
    }

    @Test fun
    `only storage tweets that matches the rules`() {
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

        val session = createSessionWith("anyName", "anyUserId", givenRules)

        session.process(givenTweets)

        assertThat(session.tweets).containsExactlyInAnyOrder(givenValidTweet)

        session.process(listOf(givenOtherValidTweet))

        assertThat(session.tweets).containsExactlyInAnyOrder(givenValidTweet, givenOtherValidTweet)
    }

    @Test fun
    `delete tweets on run and move status to done`() {
        val mockDeleteTweet = mockk<DeleteTweet>()
        val mockUpdateProgress = mockk<UpdateProgress>()

        every { mockDeleteTweet(any()) } returns true
        every { mockUpdateProgress(any()) } returns Unit

        val givenTweets = listOf(
            Tweet("validTweetId", "---anyText---", TweetType.Tweet),
            Tweet("anotherTweetId", "---otherText---", TweetType.Like)
        )

        val session = createSessionWith(name = "anyName", userId = "anyUserId")
        session.process(givenTweets)
        session.run(mockDeleteTweet, mockUpdateProgress)

        verifyAll {
            mockDeleteTweet(givenTweets[0])
            mockDeleteTweet(givenTweets[1])
        }

        verifyAll {
            mockUpdateProgress(1)
            mockUpdateProgress(2)
        }

        assertThat(session.deletedTweets).containsExactlyInAnyOrder(*givenTweets.toTypedArray())
        assertThat(session.status).isEqualTo(SessionStatus.Done)
    }

    @Test fun
    `move status to pending if any delete fail`() {
        val mockDeleteTweet = mockk<DeleteTweet>()
        val mockUpdateProgress = mockk<UpdateProgress>()

        every { mockUpdateProgress(any()) } returns Unit

        val givenTweets = listOf(
            Tweet("validTweetId", "---anyText---", TweetType.Tweet),
            Tweet("anotherTweetId", "---otherText---", TweetType.Like)
        )

        every { mockDeleteTweet(givenTweets[0]) } returns true
        every { mockDeleteTweet(givenTweets[1]) } returns false


        val session = createSessionWith(name = "anyName", userId = "anyUserId")
        session.process(givenTweets)
        session.run(mockDeleteTweet, mockUpdateProgress)

        verifyAll {
            mockUpdateProgress(1)
        }

        assertThat(session.deletedTweets).containsExactlyInAnyOrder(givenTweets[0])
        assertThat(session.tweets).containsExactlyInAnyOrder(givenTweets[1])
        assertThat(session.status).isEqualTo(SessionStatus.Pending)
    }

    @Test fun
    `fail when invoke run over a Done session`() {
        val mockDeleteTweet = mockk<DeleteTweet>()
        val mockUpdateProgress = mockk<UpdateProgress>()

        every { mockUpdateProgress(any()) } returns Unit

        val givenTweets = listOf(
            Tweet("validTweetId", "---anyText---", TweetType.Tweet),
            Tweet("anotherTweetId", "---otherText---", TweetType.Like)
        )

        every { mockDeleteTweet(givenTweets[0]) } returns true
        every { mockDeleteTweet(givenTweets[1]) } returns false

        val session = createSessionWith(name = "anyName", userId = "anyUserId")
        session.process(givenTweets)
        session.run(mockDeleteTweet, mockUpdateProgress)

        assertThat(session.status).isEqualTo(SessionStatus.Pending)

        assertThat { session.run(mockDeleteTweet, mockUpdateProgress) }.isSuccess()

        assertThat(session.status).isEqualTo(SessionStatus.Pending)
    }

    @Test fun
    `run again for Peding session`() {
        val mockDeleteTweet = mockk<DeleteTweet>()
        val mockUpdateProgress = mockk<UpdateProgress>()

        every { mockDeleteTweet(any()) } returns true
        every { mockUpdateProgress(any()) } returns Unit

        val givenTweets = listOf(
            Tweet("validTweetId", "---anyText---", TweetType.Tweet),
            Tweet("anotherTweetId", "---otherText---", TweetType.Like)
        )

        val session = createSessionWith(name = "anyName", userId = "anyUserId")
        session.process(givenTweets)
        session.run(mockDeleteTweet, mockUpdateProgress)

        assertThat(session.status).isEqualTo(SessionStatus.Done)

        assertThat { session.run(mockDeleteTweet, mockUpdateProgress) }
            .isFailure()
            .isInstanceOf(InvalidSessionStatusForRun::class)
    }

    private fun createSessionWith(name: String, userId: String, givenRules: List<Rule> = emptyList()): Session {
        val session = Session.createWith(name, userId)
        session.updateRules(givenRules)
        return session
    }
}

