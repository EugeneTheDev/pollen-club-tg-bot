package data

import com.google.gson.annotations.SerializedName

data class PollenInfo(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val value: Int,
    @SerializedName("pollen_type") val pollenType: Int
)

data class PollenResponse(
    val status: Int,
    val result: List<PollenInfo>
)