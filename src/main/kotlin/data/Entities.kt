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

enum class PollenType(val typeCode: Int) {
    BIRCH(1),
    OAK(2),
    ALDER(3),
    SAGEBRUSH(4),
    HAZEL(5),
    CEREALS(6)
}