package com.github.kgtkr.mhxxSwitchCIS

import java.awt.*
import java.awt.image.*

//二色に簡略化した画像
typealias BitImage=Array<Array<Boolean>>

object ImageSearch{
    /**
     * 画像を簡略化
     */
    fun toBitImage(image:BufferedImage,color:Color=ImageConfig.COLOR,th:Int=ImageConfig.THRESHOLD):BitImage{
        val bi=Array<Array<Boolean>>(image.width,{Array(image.height,{false})})
        for(x in 0..image.width-1){
            for(y in 0..image.height-1){
                bi[x][y]=ColorUtils.ssd(Color(image.getRGB(x,y)),color)<=th
            }
        }

        return bi
    }

    /**
     * 画像の差を求める
     * 値が大きいほど差が大きい
     */
    fun deff(a:BitImage,b:BitImage):Int{
        return a.flatten().zip(b.flatten())
                .filter{p->p.first!=p.second}
                .size
    }

    /**
     * 最も差の小さい画像の名前
     */
    fun deffMinIndex(image:BitImage,images:List<Pair<String,BitImage>>):String{
        var min=-1
        var name=""
        for((k,v) in images){
            val deff=ImageSearch.deff(image,v)
            if(deff<min||min==-1){
                min=deff
                name=k
            }
        }
        return name
    }

    fun deffMinIndex(image:BufferedImage,x:Int,y:Int,images:List<Pair<String,Color>>):String{
        var min=-1
        var name=""
        for((k,v) in images){
            val deff=ColorUtils.ssd(Color(image.getRGB(x,y)),v)
            if(deff<min||min==-1){
                min=deff
                name=k
            }
        }
        return name
    }
}