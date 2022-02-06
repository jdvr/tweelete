package es.juandavidvega.tweelete.business.action

import es.juandavidvega.tweelete.business.client.TwitterClient
import es.juandavidvega.tweelete.business.model.Session
import es.juandavidvega.tweelete.business.model.tweet.Tweet
import es.juandavidvega.tweelete.business.model.tweet.TweetType
import es.juandavidvega.tweelete.business.repository.SessionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import org.junit.jupiter.api.Test

class RunSessionActionShould {

    @Test
    fun
    `call session with wrapped dependencies and updates it on storage`() {
        val sessionRepository = mockk<SessionRepository>()
        val twitterClient = mockk<TwitterClient>()
        val givenSession = Session.createWith("anyName", "anyUserId")
        val givenTweets = listOf(
            Tweet("1", "asdf", TweetType.Tweet),
            Tweet("2", "asdfasdf", TweetType.Like)
        )
        givenSession.process(givenTweets)
        every { sessionRepository.get(givenSession.id) } returns givenSession
        every { sessionRepository.save(givenSession) } returns Unit
        every { twitterClient.delete(any()) } returns true

        RunSessionAction(sessionRepository, twitterClient, mockk(relaxed = true)).execute(RunSessionCommand(givenSession.id))

        verify {
            // I am not verifying session content, the internal change logic is tested at SessionShould
            sessionRepository.save(givenSession)
        }
        verifyAll {
            twitterClient.delete(givenTweets[0])
            twitterClient.delete(givenTweets[1])
        }
    }
}
