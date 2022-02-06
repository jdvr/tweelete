package es.juandavidvega.tweelete.business.model

import es.juandavidvega.tweelete.business.model.errors.SessionNameIsEmptyError
import es.juandavidvega.tweelete.business.model.errors.SessionUserIdIsEmptyError
import es.juandavidvega.tweelete.business.model.tweet.Tweet
import java.util.UUID

class Session private constructor(val name: String, val id: String, val userId: String) {
    val rules: MutableSet<Rule> = mutableSetOf()
    val tweets: MutableList<Tweet> = mutableListOf()
    val status: SessionStatus = SessionStatus.Draft

    fun process(tweets: List<Tweet>) {
        val validTweets = tweets.filter { it.matchesOneOf(rules) }
        this.tweets.addAll(validTweets)
    }

    fun updateRules(rules: List<Rule>) {
        this.rules.addAll(rules)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Session

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        fun createWith(name: String, userId: String): Session {
            if (name.isBlank()) {
                throw SessionNameIsEmptyError()
            }

            if (userId.isBlank()) {
                throw SessionUserIdIsEmptyError()
            }
            return Session(name, UUID.randomUUID().toString(), userId)
        }
    }
}

enum class SessionStatus {
    Draft, Running, Done
}

