package org.ligi.scr

import com.chibatching.kotpref.KotprefModel

object State : KotprefModel() {
    var lastPos by intPrefVar(default = 0)
    var lastUpdateSaved by booleanPrefVar(default = false)
    var lastUUID by stringNullablePrefVar(default = null)

    val yesIDs by stringSetPrefVal ()
    val noIDs by stringSetPrefVal ()
}