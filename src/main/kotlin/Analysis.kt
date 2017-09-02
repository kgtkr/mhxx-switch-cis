package com.github.kgtkr.mhxxSwitchCIS

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

typealias BitImages=List<Pair<String,BitImage>>

private fun readImage(name:String):List<Pair<String,BufferedImage>>{
    return File("./images/${name}")
            .listFiles()
            .map { f->Pair(FilenameUtils.getBaseName(f.name), ImageIO.read(f)) }
}

private fun readBitImage(name:String):BitImages{
    return readImage(name)
            .map{x-> Pair(x.first,x.second.toBitImage()) }
}

private fun Triple<BufferedImage, BufferedImage, BufferedImage>.getVal(values0:BitImages,values1:BitImages,values2:BitImages):Int{
    val val0=this.first.toBitImage().deffMinIndex(values0)
    val val1=this.second.toBitImage().deffMinIndex(values1)
    val val2=this.third.toBitImage().deffMinIndex(values2)

    return (val0+val1+val2).toInt()
}

fun analysisCmd(){
    //データ読み込み
    val skills=readBitImage("skill")
    val values0=readBitImage("value0")
    val values1=readBitImage("value1")
    val values2=readBitImage("value2")
    val gosekis=readImage("goseki")
            .map{x->Pair(x.first,Color(x.second.getRGB(IC.GOSEKI_COLOR_X,IC.GOSEKI_COLOR_Y)))}


    val csv=readImages("./input")
            .map{row->
                val goseki=row.goseki.deffMinIndex(IC.GOSEKI_COLOR_X,IC.GOSEKI_COLOR_Y,gosekis)
                val skill1=Pair(row.oneSkillName.toBitImage().deffMinIndex(skills),
                        row.oneSkillValue.getVal(values0,values1,values2))
                val skill2Name=row.twoSkillName.toBitImage().deffMinIndex(skills)
                val skill2=when(skill2Name){
                    ""->null
                    else->Pair(skill2Name,row.twoSkillValue.getVal(values0,values1,values2))
                }
                val slot1Color= Color(row.slot.getRGB(IC.SLOT1_COLOR_X,IC.SLOT_COLOR_Y))
                val slot2Color= Color(row.slot.getRGB(IC.SLOT2_COLOR_X,IC.SLOT_COLOR_Y))
                val slot3Color= Color(row.slot.getRGB(IC.SLOT3_COLOR_X,IC.SLOT_COLOR_Y))

                val isSlot1=slot1Color.deff(IC.SLOT_COLOR)>IC.SLOT_THRESHOLD
                val isSlot2=slot2Color.deff(IC.SLOT_COLOR)>IC.SLOT_THRESHOLD
                val isSlot3=slot3Color.deff(IC.SLOT_COLOR)>IC.SLOT_THRESHOLD

                val slot=if(isSlot1&&isSlot2&&isSlot3){
                    3
                }else if(isSlot1&&isSlot2){
                    2
                }else if(isSlot1){
                    1
                }else{
                    0
                }

                Goseki(goseki,skill1,skill2,slot)
            }
            .map{g->
                if(g.skill2!=null){
                    "${g.goseki},${g.slot},${g.skill1.first},${g.skill1.second},${g.skill2.first},${g.skill2.second}"
                }else{
                    "${g.goseki},${g.slot},${g.skill1.first},${g.skill1.second}"
                }
            }
            .joinToString("\n")

    FileUtils.write(File("./output.csv"),csv,"utf8")
}
