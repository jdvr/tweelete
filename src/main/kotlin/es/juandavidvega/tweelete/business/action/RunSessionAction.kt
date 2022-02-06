package es.juandavidvega.tweelete.business.action

import es.juandavidvega.tweelete.business.action.errors.InvalidSessionIdError
import es.juandavidvega.tweelete.business.client.TwitterClient
import es.juandavidvega.tweelete.business.enviroment.Logger
import es.juandavidvega.tweelete.business.model.DeleteTweet
import es.juandavidvega.tweelete.business.repository.SessionRepository

class RunSessionAction(private val repository: SessionRepository, private val twitterClient: TwitterClient, private val logger: Logger) {
    fun execute(command: RunSessionCommand) {
        val session = repository.get(command.sessionId) ?: throw InvalidSessionIdError(command.sessionId)
        val deleteTweet: DeleteTweet = {
            twitterClient.delete(it)
        }
        val count = session.tweets.size
        session.run(deleteTweet) {
            logger.log("(Session = ${session.id}): Delete tweet $it out of $count tweets")
        }
        repository.save(session)
    }
}
