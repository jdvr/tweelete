package es.juandavidvega.tweelete.business.client

import es.juandavidvega.tweelete.business.model.Rule
import es.juandavidvega.tweelete.business.model.tweet.Tweet

interface TwitterClient {
    fun userTweetsAndLikes(rules: List<Rule> = emptyList()): List<Tweet>
    suspend fun delete(tweet: Tweet): Boolean
}
