package com.github.kgtkr.mhxxSwitchCIS

import javax.imageio.*
import java.io.*
import org.apache.commons.io.*
import java.awt.Color
import java.awt.image.BufferedImage

fun main(args: Array<String>) {
    if(args.size==0){
        defaltCmd()
    }else if(args[0]=="t"||args[0]=="trimming"){
        trimmingCmd()
    }else if(args[0]=="c"||args[0]=="check"){
        checkCmd()
    }else if(args[0]=="p"||args[0]=="param"){
        paramCmd()
    }
}

fun paramCmd(){
    ParamFrame()
}

fun defaltCmd(){
    fun <T> imageMap(name:String,func:(image: BufferedImage)->T):List<Pair<String,T>>{
        return File("./images/${name}")
                .listFiles()
                .map { f->Pair(FilenameUtils.getBaseName(f.name),ImageIO.read(f)) }
                .map{i->Pair(i.first,func(i.second))}
    }

    fun getBit(name:String):List<Pair<String,BitImage>>{
        return imageMap(name,{i->ImageSearch.toBitImage(i)})
    }

    val skills=getBit("skill")
    val values0=getBit("value0")
    val values1=getBit("value1")
    val values2=getBit("value2")
    val gosekis=imageMap("goseki",{i->Color(i.getRGB(IC.GOSEKI_COLOR_X,IC.GOSEKI_COLOR_Y))})

    fun getVal(value:Triple<BufferedImage,BufferedImage,BufferedImage>):Int{
        val val0=ImageSearch.deffMinIndex(ImageSearch.toBitImage(value.first),values0)
        val val1=ImageSearch.deffMinIndex(ImageSearch.toBitImage(value.second),values1)
        val val2=ImageSearch.deffMinIndex(ImageSearch.toBitImage(value.third),values2)

        return (val0+val1+val2).toInt()
    }



    val csv=readImages("./input")
            .map{row->
                val goseki=ImageSearch.deffMinIndex(row.goseki,IC.GOSEKI_COLOR_X,IC.GOSEKI_COLOR_Y,gosekis)
                val skill1=Pair(ImageSearch.deffMinIndex(ImageSearch.toBitImage(row.oneSkillName),skills),
                        getVal(row.oneSkillValue))
                val skill2Name=ImageSearch.deffMinIndex(ImageSearch.toBitImage(row.twoSkillName),skills)
                val skill2=when(skill2Name){
                    ""->null
                    else->Pair(skill2Name,getVal(row.twoSkillValue))
                }
                val slot1Color=Color(row.slot.getRGB(IC.SLOT1_COLOR_X,IC.SLOT_COLOR_Y))
                val slot2Color=Color(row.slot.getRGB(IC.SLOT2_COLOR_X,IC.SLOT_COLOR_Y))
                val slot3Color=Color(row.slot.getRGB(IC.SLOT3_COLOR_X,IC.SLOT_COLOR_Y))

                val isSlot1=ColorUtils.ssd(slot1Color,IC.SLOT_COLOR)>IC.SLOT_THRESHOLD
                val isSlot2=ColorUtils.ssd(slot2Color,IC.SLOT_COLOR)>IC.SLOT_THRESHOLD
                val isSlot3=ColorUtils.ssd(slot3Color,IC.SLOT_COLOR)>IC.SLOT_THRESHOLD

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

fun trimmingCmd(){
    FileUtils.deleteQuietly(File("./trimming"))
    File("./trimming").mkdir()
    File("./trimming/goseki").mkdir()
    File("./trimming/oneSkillName").mkdir()
    File("./trimming/oneSkillValue0").mkdir()
    File("./trimming/oneSkillValue1").mkdir()
    File("./trimming/oneSkillValue2").mkdir()
    File("./trimming/twoSkillName").mkdir()
    File("./trimming/twoSkillValue0").mkdir()
    File("./trimming/twoSkillValue1").mkdir()
    File("./trimming/twoSkillValue2").mkdir()
    File("./trimming/slot").mkdir()

    readImages("./raw-images")
            .withIndex()
            .forEach{row->
                ImageIO.write(row.value.goseki,
                        "jpeg",
                        File("./trimming/goseki/${row.index}.jpg"))
                ImageIO.write(row.value.oneSkillName,
                        "jpeg",
                        File("./trimming/oneSkillName/${row.index}.jpg"))
                row.value.oneSkillValue.toList().withIndex()
                        .forEach{(i,image)->ImageIO.write(image,
                                "jpeg",
                                File("./trimming/oneSkillValue${i}/${row.index}.jpg"))}
                ImageIO.write(row.value.twoSkillName,
                        "jpeg",
                        File("./trimming/twoSkillName/${row.index}.jpg"))
                row.value.twoSkillValue.toList().withIndex()
                        .forEach{(i,image)->ImageIO.write(image,
                                "jpeg",
                                File("./trimming/twoSkillValue${i}/${row.index}.jpg"))}
                ImageIO.write(row.value.slot,
                        "jpeg",
                        File("./trimming/slot/${row.index}.jpg"))
            }
}

fun readImages(dir:String):List<Rows>{
    return File(dir)
            .listFiles()
            .map{file->ImageIO.read(file)}
            .map{image->ImageTrimming.trimmingImage(image)}
            .map{table->ImageTrimming.trimmingTable(table)}
            .flatten()
            .map{row->ImageTrimming.trimmingRow(row)}
}

fun checkCmd(){
    fun check(name:String){
        println("【${name}】")
        FileUtils.readFileToString(File("./images-${name}.md"),"utf8")
                .split("\n")
                .filter{s->s.indexOf("* [x] ")==0}
                .map{s->s.substring(6)}
                .map{s->File("./images/${name}/${s}.jpg")}
                .filter { file-> !file.exists()}
                .forEach{file->println(file.name)}
        println()
    }

    check("goseki")
    check("skill")
    check("value")
    check("slot")
}

data class Goseki(val goseki:String,
                  val skill1:Pair<String,Int>,
                  val skill2:Pair<String,Int>?,
                  val slot:Int)