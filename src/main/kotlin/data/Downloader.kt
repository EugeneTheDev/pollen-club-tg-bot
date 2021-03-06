package data

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.lang.IllegalArgumentException

class Downloader : CoroutineScope {
    override val coroutineContext = Dispatchers.IO

    fun downloadCurrentData() = async {
        Fuel.get("https://pollen.club/new_test_sql/?request=pins")
            .responseObject<PollenResponse>()
            .third
            .component1()
            ?.result ?: throw IllegalArgumentException("Could not receive response")
    }
}