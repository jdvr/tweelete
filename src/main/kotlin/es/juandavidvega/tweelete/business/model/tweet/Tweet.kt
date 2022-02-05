package es.juandavidvega.tweelete.business.model.tweet

data class Tweet(val id: String, val text: String, val type: TweetType)

enum class TweetType {
    Like, Tweet
}
