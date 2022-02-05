package es.juandavidvega.tweelete.business.action

import assertk.assertThat
import assertk.assertions.*
import es.juandavidvega.tweelete.business.action.errors.InvalidSessionIdError
import es.juandavidvega.tweelete.business.client.TwitterClient
import es.juandavidvega.tweelete.business.model.Session
import es.juandavidvega.tweelete.business.model.SessionStatus
import es.juandavidvega.tweelete.business.model.tweet.Tweet
import es.juandavidvega.tweelete.business.model.tweet.TweetType
import es.juandavidvega.tweelete.business.repository.SessionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test


class LoadTweetsActionShould {


    @Test fun
    `Given a session call twitter API to load all tweets into the session`() {
        val givenSession = Session.createWith("dummySession", "userId")
        val repository = mockk<SessionRepository>()
        val sessionSlot = slot<Session>()

        every { repository.get(givenSession.id) } returns givenSession
        every { repository.save(session = capture(sessionSlot)) } returns Unit

        val userTweets = listOf(Tweet("anyId", "content", TweetType.Like), Tweet("anyId1", "content1", TweetType.Tweet))
        val twitterClient = mockk<TwitterClient>()
        every { twitterClient.userTweetsAndLikes() } returns userTweets


        LoadTweetsAction(repository, twitterClient).execute(LoadTweetsCommand(givenSession.id))
        assertThat(sessionSlot.captured.id).isEqualTo(givenSession.id)
        assertThat(sessionSlot.captured.status).isEqualTo(SessionStatus.Draft)
        assertThat(sessionSlot.captured.tweets).isEqualTo(userTweets)
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
