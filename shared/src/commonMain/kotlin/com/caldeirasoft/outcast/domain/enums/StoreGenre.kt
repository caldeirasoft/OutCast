@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.enums

import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
enum class StoreGenre(val id: Int)
{
    Arts(1301),
    Business(1321),
    Comedy(1303),
    Education(1304),
    Fiction(1483),
    Government(1511),
    Health_Fitness(1512),
    History(1487),
    Kids_Family(1305),
    Leisure(1502),
    Music(1310),
    News(1489),
    Religion_Spirtuality(1314),
    Science(1533),
    Society_Culture(1324),
    Sports(1545),
    TV_Film(1309),
    Technology(1318),
    True_Crime(1488)
}
