package es.juandavidvega.tweelete.business.model.tweet

import es.juandavidvega.tweelete.business.model.Rule

data class Tweet(val id: String, val text: String, val type: TweetType) {
    fun matchesOneOf(rules: Set<Rule>): Boolean {
        return rules.any { it.match(this) }
    }
}

enum class TweetType {
    Like, Tweet
}
