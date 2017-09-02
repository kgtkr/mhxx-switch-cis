package com.github.kgtkr.mhxxSwitchCIS

import java.awt.*
import java.awt.image.*

//二色に簡略化した画像
typealias BitImage=Array<Array<Boolean>>

/**
 * 画像を簡略化
 */
fun BufferedImage.toBitImage(color:Color=ImageConfig.COLOR,th:Int=ImageConfig.THRESHOLD):BitImage{
    val bi=Array<Array<Boolean>>(this.width,{Array(this.height,{false})})
    for(x in 0..this.width-1){
        for(y in 0..this.height-1){
            bi[x][y]=color.deff(Color(this.getRGB(x,y)))<=th
        }
    }

    return bi
}

/**
 * 最も差の小さい画像の名前
 */
fun BitImage.deffMinIndex(images:List<Pair<String,BitImage>>):String{
    var min=-1
    var name=""
    for((k,v) in images){
        val deff=this.deff(v)
        if(deff<min||min==-1){
            min=deff
            name=k
        }
    }
    return name
}

fun BufferedImage.deffMinIndex(x:Int,y:Int,images:List<Pair<String,Color>>):String{
    var min=-1
    var name=""
    for((k,v) in images){
        val deff=v.deff(Color(this.getRGB(x,y)))
        if(deff<min||min==-1){
            min=deff
            name=k
        }
    }
    return name
}

/**
 * 画像の差を求める
 * 値が大きいほど差が大きい
 */
fun BitImage.deff(target:BitImage):Int{
    return this.flatten().zip(target.flatten())
            .filter{p->p.first!=p.second}
            .size
}