package com.github.kgtkr.mhxxSwitchCIS

import java.awt.*
import java.awt.color.*

fun Color.deff(c:Color):Int{
    val xyzA=this.toXYZ()
    val xyzB=c.toXYZ()

    val r=((xyzA[0]-xyzB[0])*255).toInt()
    val g=((xyzA[1]-xyzB[1])*255).toInt()
    val b=((xyzA[2]-xyzB[2])*255).toInt()

    return r*r+g*g+b*b
}

fun Color.toXYZ():FloatArray{
    return ColorSpace.getInstance(ColorSpace.CS_CIEXYZ)
            .fromRGB(floatArrayOf(this.red/255f,this.green/255f,this.blue/255f))
}