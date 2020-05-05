import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.location
import data.Analyser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    Bot(args[0])
}

class Bot(token: String) : CoroutineScope {
    override val coroutineContext = Dispatchers.Default
    private val analyser = Analyser()

    init {
       bot {
            this.token = token
            dispatch {
                locationHandler()
            }
        }.startPolling()
    }

    private fun Dispatcher.locationHandler() = location { bot, update, location ->
        launch {
            val pollenValue = analyser.predict(location.latitude.toDouble(), location.longitude.toDouble())
            bot.sendMessage(update.message!!.chat.id, "Текущая ситуация: $pollenValue")
        }
    }
}