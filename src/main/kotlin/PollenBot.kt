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
import java.net.InetSocketAddress
import java.net.Proxy

fun main(args: Array<String>) {
    val proxy = if (args.size == 3) Proxy(Proxy.Type.SOCKS, InetSocketAddress(args[1], args[2].toInt())) else null
    PollenBot(args[0], proxy)
}

class PollenBot(token: String, proxy: Proxy? = null) : CoroutineScope {
    override val coroutineContext = Dispatchers.Default
    private val analyser = Analyser()
    private val currentQueries = mutableMapOf<Long, PollenType>()

    init {
       bot {
            this.token = token
            if (proxy != null) {
                this.proxy = proxy
            }
            dispatch {
                startHandler()
                helpHandler()
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
                            text = """Current situation for *${pollenType.toString().toLowerCase()}*: 
                                        |*$pollenValue* ${prepareReport(pollenValue)}""".trimMargin(),
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

    private fun prepareReport(pollenValue: Int) = when(pollenValue) {
        1 -> "(Normal) \u2705"
        2 -> "(Medium) \u26a0\ufe0f"
        3 -> "(High) \ud83d\udd34"
        else -> "(Death) \ud83d\udc80"
    }

    private fun Dispatcher.startHandler() = command("start") { bot, update ->
        launch {
            bot.sendMessage(
                chatId = update.message!!.chat.id,
                text = "Hello! I will help you monitor current situation with pollen concentration in the air. Just choose pollen type and send location"
            )
        }
    }

    private fun Dispatcher.helpHandler() = command("help") { bot, update ->
        launch {
            bot.sendMessage(
                chatId = update.message!!.chat.id,
                text = """
                Commands list:
                /birch (береза)
                /oak (дуб)
                /alder (ольха)
                /sagebrush (полынь)
                /hazel (орешник)
                /cereals (злаки)
            """.trimIndent()
            )
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