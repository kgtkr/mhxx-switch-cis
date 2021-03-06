package com.github.kgtkr.mhxxSwitchCIS

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO

typealias BitImages=List<Pair<String,BitImage>>

private fun readImage(name:String):List<Pair<String,BufferedImage>> {
    val data = FileUtils.readFileToString(File("./data/${name}"), "utf8")
    return data.split("\n")
            .map { s -> s.split(",") }
            .map { s -> Pair(s[0], s[1]) }
            .map { data -> Pair(data.first, Base64.getDecoder().decode(data.second)) }
            .map { data -> Pair(data.first, ImageIO.read(ByteArrayInputStream(data.second))) }
}

private fun readBitImage(name:String):BitImages{
    return readImage(name)
            .map{x-> Pair(x.first,x.second.toBitImage(IC.COLOR,IC.THRESHOLD)) }
}

private fun Triple<BufferedImage, BufferedImage, BufferedImage>.getVal(values:Triple<BitImages,BitImages,BitImages>):Int{
    val val0=this.first.toBitImage(IC.COLOR,IC.THRESHOLD).deffMinIndex(values.first)
    val val1=when(val0){
        ""->this.second.toBitImage(IC.COLOR,IC.THRESHOLD).deffMinIndex(values.second)
        else->"1"
    }
    val val2=when(val1){
        "1"->this.third.toBitImage(IC.COLOR,IC.THRESHOLD).deffMinIndex(values.third.filter { x->x.first=="0"||x.first=="1"||x.first=="2"||x.first=="3" })
        else->this.third.toBitImage(IC.COLOR,IC.THRESHOLD).deffMinIndex(values.third.filter { x->x.first!="0"})
    }

    return (val0+val1+val2).toInt()
}

private fun BufferedImage.getSlot():Int{
    val slot1Color= Color(this.getRGB(IC.SLOT1_COLOR_X,IC.SLOT_COLOR_Y))
    val slot2Color= Color(this.getRGB(IC.SLOT2_COLOR_X,IC.SLOT_COLOR_Y))
    val slot3Color= Color(this.getRGB(IC.SLOT3_COLOR_X,IC.SLOT_COLOR_Y))

    return if(slot3Color.deff(IC.SLOT_COLOR)>IC.SLOT_THRESHOLD){
        3
    }else if(slot2Color.deff(IC.SLOT_COLOR)>IC.SLOT_THRESHOLD){
        2
    }else if(slot1Color.deff(IC.SLOT_COLOR)>IC.SLOT_THRESHOLD){
        1
    }else{
        0
    }
}

private fun BufferedImage.getGoseki(gosekis:List<Pair<String,Color>>):String{
    return this.deffMinIndex(IC.GOSEKI_COLOR_X,IC.GOSEKI_COLOR_Y,gosekis)
}

private fun BufferedImage.getSkillName(skills:BitImages):String{
    return this.toBitImage(IC.COLOR,IC.THRESHOLD).deffMinIndex(skills)
}

fun analysisCmd(){
    //データ読み込み
    val skills=readBitImage("skill")
    val values=Triple(readBitImage("value0"),readBitImage("value1"),readBitImage("value2"))
    val gosekis=readImage("goseki")
            .map{x->Pair(x.first,Color(x.second.getRGB(IC.GOSEKI_COLOR_X,IC.GOSEKI_COLOR_Y)))}


    val csv=readImages("./input")
            .map{row->
                val skill1Name=row.oneSkillName.getSkillName(skills.filter { s->s.first.isNotEmpty() })

                if(skill1Name!="(無)") {
                    val goseki = row.goseki.getGoseki(gosekis)
                    val skill1 = Pair(skill1Name, row.oneSkillValue.getVal(values.copy(first = values.first.filter { s->s.first!="-" },second = values.second.filter { s->s.first!="-" })))
                    val skill2Name = row.twoSkillName.getSkillName(skills.filter { s -> s.first != "(無)" })
                    val skill2 = when (skill2Name) {
                        "" -> null
                        else -> Pair(skill2Name, row.twoSkillValue.getVal(values))
                    }
                    val slot = row.slot.getSlot()

                    Goseki(goseki, skill1, skill2, slot)
                }else{
                    null
                }
            }
            .filterNotNull()
            .map{g->
                if(g.skill2!=null){
                    "${g.goseki},${g.slot},${g.skill1.first},${g.skill1.second},${g.skill2.first},${g.skill2.second}"
                }else{
                    "${g.goseki},${g.slot},${g.skill1.first},${g.skill1.second}"
                }
            }
            .joinToString("\n")
    val header="#護石名,スロット,第一スキル名,第一スキルポイント,第ニスキル名,第ニスキルポイント\n"
    FileUtils.write(File("./CHARM.csv"),header+csv,"sjis")
}
