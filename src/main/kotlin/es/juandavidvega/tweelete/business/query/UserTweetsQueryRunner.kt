package es.juandavidvega.tweelete.business.query

import es.juandavidvega.tweelete.business.client.TwitterClient
import es.juandavidvega.tweelete.business.model.Rule
import es.juandavidvega.tweelete.business.model.tweet.Tweet

class UserTweetsQueryRunner(private val twitterClient: TwitterClient) {
    fun Run(query: UserTweetsQuery): List<Tweet> {
        return twitterClient.userTweetsAndLikes(query.rules)
    }
}

data class UserTweetsQuery(val rules: List<Rule> = emptyList())


