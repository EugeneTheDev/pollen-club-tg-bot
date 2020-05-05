import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.location
import com.github.kotlintelegrambot.entities.ParseMode
import data.Analyser
import data.PollenType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    PollenBot(args[0])
}

class PollenBot(token: String) : CoroutineScope {
    override val coroutineContext = Dispatchers.Default
    private val analyser = Analyser()
    private val currentQueries = mutableMapOf<Long, PollenType>()

    init {
       bot {
            this.token = token
            dispatch {
                locationHandler()
                birchHandler()
                oakHandler()
                alderHandler()
                sagebrushHandler()
                hazelHandler()
                cerealsHandler()
            }
        }.startPolling()
    }

    private fun Dispatcher.locationHandler() = location { bot, update, location ->
        launch {
            try {
                val chatId = update.message!!.chat.id
                if (chatId in currentQueries) {
                    val pollenType = currentQueries[chatId]!!
                    val pollenValue = analyser.predict(
                            latitude = location.latitude.toDouble(),
                            longitude = location.longitude.toDouble(),
                            pollenType = pollenType
                        )
                    bot.sendMessage(
                            chatId = chatId,
                            text = "Current situation for *${pollenType.toString().toLowerCase()}*: $pollenValue",
                            parseMode = ParseMode.MARKDOWN
                        )
                    currentQueries.remove(chatId)
                } else {
                    bot.sendMessage(chatId, "Choose pollen type first")
                }
            } catch (e: IllegalArgumentException) {
                bot.sendMessage(update.message!!.chat.id, "An error was occurred")
            }
        }
    }

    private fun Dispatcher.birchHandler() = command("birch") { bot, update ->
        launch {
            val charId = update.message!!.chat.id
            currentQueries[charId] = PollenType.BIRCH
            requestLocation(bot, charId)
        }
    }

    private fun Dispatcher.oakHandler() = command("oak") { bot, update ->
        launch {
            val charId = update.message!!.chat.id
            currentQueries[charId] = PollenType.OAK
            requestLocation(bot, charId)
        }
    }

    private fun Dispatcher.alderHandler() = command("alder") { bot, update ->
        launch {
            val charId = update.message!!.chat.id
            currentQueries[charId] = PollenType.ALDER
            requestLocation(bot, charId)
        }
    }

    private fun Dispatcher.sagebrushHandler() = command("sagebrush") { bot, update ->
        launch {
            val charId = update.message!!.chat.id
            currentQueries[charId] = PollenType.SAGEBRUSH
            requestLocation(bot, charId)
        }
    }

    private fun Dispatcher.hazelHandler() = command("hazel") { bot, update ->
        launch {
            val charId = update.message!!.chat.id
            currentQueries[charId] = PollenType.HAZEL
            requestLocation(bot, charId)
        }
    }

    private fun Dispatcher.cerealsHandler() = command("cereals") { bot, update ->
        launch {
            val charId = update.message!!.chat.id
            currentQueries[charId] = PollenType.CEREALS
            requestLocation(bot, charId)
        }
    }

    private fun requestLocation(bot: Bot, chatId: Long) {
        bot.sendMessage(chatId, "Cool. Now send location, please")
    }
}