package es.juandavidvega.tweelete.business.action

import es.juandavidvega.tweelete.business.action.errors.InvalidSessionIdError
import es.juandavidvega.tweelete.business.client.TwitterClient
import es.juandavidvega.tweelete.business.repository.SessionRepository

class LoadTweetsAction(private val repository: SessionRepository, private val twitterClient: TwitterClient) {
    fun execute(command: LoadTweetsCommand) {
        val session = repository.get(command.sessionId) ?: throw InvalidSessionIdError(command.sessionId)
        val tweets = twitterClient.userTweetsAndLikes()

        session.process(tweets)

        repository.save(session)
    }
}

data class LoadTweetsCommand(val sessionId: String)
