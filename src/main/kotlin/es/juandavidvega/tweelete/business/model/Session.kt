package es.juandavidvega.tweelete.business.model

import es.juandavidvega.tweelete.business.model.errors.InvalidSessionStatusForRun
import es.juandavidvega.tweelete.business.model.errors.SessionNameIsEmptyError
import es.juandavidvega.tweelete.business.model.errors.SessionUserIdIsEmptyError
import es.juandavidvega.tweelete.business.model.tweet.Tweet
import java.util.UUID

typealias DeleteTweet = (t: Tweet) -> Boolean
typealias UpdateProgress = (count: Int) -> Unit

class Session private constructor(val name: String, val id: String, val userId: String) {
    val rules: MutableSet<Rule> = mutableSetOf()
    val tweets: MutableSet<Tweet> = mutableSetOf()
    val deletedTweets: MutableSet<Tweet> = mutableSetOf()
    var status: SessionStatus = SessionStatus.Draft

    fun process(tweets: List<Tweet>) {
        val validTweets = tweets.filter { it.matchesOneOf(rules) }
        this.tweets.addAll(validTweets)
    }

    fun updateRules(rules: List<Rule>) {
        this.rules.addAll(rules)
    }

    fun run(delete: DeleteTweet, updateProgress: UpdateProgress) {
        // TODO: This must be async
        if (status !in RunnableStatuses) {
            throw InvalidSessionStatusForRun(status)
        }
        status = SessionStatus.Running
        val deletedTweets = mutableSetOf<Tweet>()
        tweets.forEach {
            val deleted = delete(it)
            if (deleted){
                deletedTweets.add(it)
                updateProgress(deletedTweets.size)
            }
        }
        this.tweets.removeAll(deletedTweets)
        this.deletedTweets.addAll(deletedTweets)
        status = if (this.tweets.isEmpty()) {
            SessionStatus.Done
        } else {
            SessionStatus.Pending
        }
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
        val RunnableStatuses = setOf(SessionStatus.Pending, SessionStatus.Draft)
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
    Draft, Running, Pending, Done
}

