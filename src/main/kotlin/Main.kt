package com.github.kgtkr.mhxxSwitchCIS

import javax.imageio.*
import java.io.*
import org.apache.commons.io.*
import java.awt.Color
import java.awt.image.BufferedImage

fun main(args: Array<String>) {
    if(args.size==0){
        analysisCmd()
    }else if(args[0]=="t"||args[0]=="trimming"){
        trimmingCmd()
    }else if(args[0]=="c"||args[0]=="check"){
        checkCmd()
    }else if(args[0]=="p"||args[0]=="param"){
        paramCmd()
    }else if(args[0]=="d"||args[0]=="data"){
        dataCmd()
    }

}


fun readImages(dir:String):List<Rows>{
    return File(dir)
            .listFiles()
            .sorted()
            .map{file->ImageIO.read(file)}
            .map{image->ImageTrimming.trimmingImage(image)}
            .map{table->ImageTrimming.trimmingTable(table)}
            .flatten()
            .map{row->ImageTrimming.trimmingRow(row)}
}


data class Goseki(val goseki:String,
                  val skill1:Pair<String,Int>,
                  val skill2:Pair<String,Int>?,
                  val slot:Int)