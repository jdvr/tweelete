package es.juandavidvega.tweelete.business.enviroment

enum class LogLevel {
    Info, Error
}

interface Logger {
    fun log(message: String, level: LogLevel = LogLevel.Info)
}
