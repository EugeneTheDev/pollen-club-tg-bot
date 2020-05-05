package data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.fixedRateTimer
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Implements k nearest neighbours algorithm
 */
class Analyser(private val kNeighbours: Int = 3) : CoroutineScope {
    override val coroutineContext = Dispatchers.Default

    private val downloader = Downloader()
    private lateinit var data: List<PollenInfo>

    init {
        fixedRateTimer(daemon = true, period = 4*60*60*1000) {
            launch {  data = downloader.downloadCurrentData().await() }
        }
    }

    suspend fun predict(latitude: Double, longitude: Double, pollenType: PollenType): Int {
        val valuesWithDistances = mutableListOf<Pair<Int, Double>>()

        data.filter { it.pollenType == pollenType.typeCode }.forEach {
            val distance = calculateDistance(latitude, longitude, it.latitude, it.longitude)
            valuesWithDistances.add(Pair(it.value, distance))
        }

        return valuesWithDistances.sortedBy { it.second }
            .subList(0, kNeighbours)
            .groupBy { it.first }
            .map { it.key to it.value.map { it.second }.sum() }
            .minBy { it.second }
            ?.first ?: throw IllegalArgumentException("Could not perform knn")

    }

    private fun calculateDistance(
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ) = sqrt((latitude1 - latitude2).pow(2) + (longitude1 - longitude2).pow(2))
}