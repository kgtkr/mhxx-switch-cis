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
    }
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
    val values=getBit("value")
    val gosekis=imageMap("goseki",{i->Color(i.getRGB(IC.GOSEKI_COLOR_X,IC.GOSEKI_COLOR_Y))})

    val csv=readImages("./input")
            .map{row->
                val goseki=ImageSearch.deffMinIndex(row.goseki,IC.GOSEKI_COLOR_X,IC.GOSEKI_COLOR_Y,gosekis)
                val skill1=Pair(ImageSearch.deffMinIndex(ImageSearch.toBitImage(row.oneSkillName),skills),
                        ImageSearch.deffMinIndex(ImageSearch.toBitImage(row.oneSkillValue),values).toInt())
                val skill2Name=ImageSearch.deffMinIndex(ImageSearch.toBitImage(row.twoSkillName),skills)
                val skill2=when(skill2Name){
                    ""->null
                    else->Pair(skill2Name,ImageSearch.deffMinIndex(ImageSearch.toBitImage(row.twoSkillValue),values).toInt())
                }
                val slot1Color=Color(row.slot.getRGB(IC.SLOT1_COLOR_X,IC.SLOT_COLOR_Y))
                val slot2Color=Color(row.slot.getRGB(IC.SLOT2_COLOR_X,IC.SLOT_COLOR_Y))
                val slot3Color=Color(row.slot.getRGB(IC.SLOT3_COLOR_X,IC.SLOT_COLOR_Y))

                val isSlot1=ColorUtils.ssd(slot1Color,IC.COLOR)<=IC.THRESHOLD
                val isSlot2=ColorUtils.ssd(slot2Color,IC.COLOR)<=IC.THRESHOLD
                val isSlot3=ColorUtils.ssd(slot3Color,IC.COLOR)<=IC.THRESHOLD

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
    File("./trimming/oneSkillValue").mkdir()
    File("./trimming/twoSkillName").mkdir()
    File("./trimming/twoSkillValue").mkdir()
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
                ImageIO.write(row.value.oneSkillValue,
                        "jpeg",
                        File("./trimming/oneSkillValue/${row.index}.jpg"))
                ImageIO.write(row.value.twoSkillName,
                        "jpeg",
                        File("./trimming/twoSkillName/${row.index}.jpg"))
                ImageIO.write(row.value.twoSkillValue,
                        "jpeg",
                        File("./trimming/twoSkillValue/${row.index}.jpg"))
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