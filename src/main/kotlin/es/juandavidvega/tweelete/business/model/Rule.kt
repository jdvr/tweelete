package es.juandavidvega.tweelete.business.model

import es.juandavidvega.tweelete.business.model.tweet.Tweet
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import java.time.LocalDate

enum class RuleType {
    Include, Exclude
}

sealed class Rule(val type: RuleType) {
    abstract fun match(tweet: Tweet): Boolean
}

class TweetedAfterDateRule (val date: LocalDate, type: RuleType): Rule(type) {
    override fun match(tweet: Tweet): Boolean {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TweetedAfterDateRule

        return EqualsBuilder()
            .append(date, other.date)
            .append(type, other.type)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder().append(date).append(type).toHashCode()
    }


}

class TweetedBeforeDateRule(val date: LocalDate, type: RuleType): Rule(type) {
    override fun match(tweet: Tweet): Boolean {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TweetedBeforeDateRule

        return EqualsBuilder()
            .append(date, other.date)
            .append(type, other.type)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder().append(date).append(type).toHashCode()
    }
}

class TweetContains(val text: String, type: RuleType): Rule(type) {
    override fun match(tweet: Tweet): Boolean {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TweetContains

        return EqualsBuilder()
            .append(text, other.text)
            .append(type, other.type)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder().append(text).append(type).toHashCode()
    }
}

class NumberOfLikesSmallerThan(val number: Int, type: RuleType): Rule(type) {
    override fun match(tweet: Tweet): Boolean {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NumberOfLikesSmallerThan

        return EqualsBuilder()
            .append(number, other.number)
            .append(type, other.type)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder().append(number).append(type).toHashCode()
    }
}
