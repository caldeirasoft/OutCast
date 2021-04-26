package com.caldeirasoft.outcast.data.db.customparcelers

import android.os.Parcel
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parceler

object InstantParceler : Parceler<Instant> {
    override fun create(parcel: Parcel): Instant = Instant.fromEpochSeconds(parcel.readLong())

    override fun Instant.write(parcel: Parcel, flags: Int) {
        parcel.writeLong(this.epochSeconds)
    }
}

object NullableInstantParceler : Parceler<Instant?> {
    override fun create(parcel: Parcel): Instant? =
        when(parcel.readLong()) {
            Instant.DISTANT_PAST.epochSeconds -> null
            else -> Instant.fromEpochSeconds(parcel.readLong())
        }

    override fun Instant?.write(parcel: Parcel, flags: Int) {
        this?.let { parcel.writeLong(this.epochSeconds) }
            ?: parcel.writeLong(Instant.DISTANT_PAST.epochSeconds)
    }
}