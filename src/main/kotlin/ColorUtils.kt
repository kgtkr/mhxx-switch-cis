package com.github.kgtkr.mhxxSwitchCIS

import java.awt.*

object ColorUtils{
    fun ssd(a:Color,b:Color):Int{
        val r=a.red-b.red
        val g=a.green-b.green
        val b=a.blue-b.blue

        return r*r+g*g+b*b
    }
}