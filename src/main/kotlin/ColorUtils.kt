package com.github.kgtkr.mhxxSwitchCIS

import java.awt.*
import javafx.geometry.Point3D

fun Color.deff(c:Color):Int{
    val r=this.red-c.red
    val g=this.green-c.green
    val b=this.blue-c.blue

    return r*r+g*g+b*b
}