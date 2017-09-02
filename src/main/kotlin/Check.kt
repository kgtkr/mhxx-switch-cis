package com.github.kgtkr.mhxxSwitchCIS

import org.apache.commons.io.FileUtils
import java.io.File

fun checkCmd(){
    fun check(name:String){
        println("【${name}】")
        //必要なファイル
        val files= FileUtils.readFileToString(File("./images-${name}.md"),"utf8")
                .split("\n")
                .filter{s->s.indexOf("* [x] ")==0}
                .map{s->s.substring(6)}
                .map{s->if(s=="(なし)") "" else s}
                .map{s-> File("./images/${name}/${s}.jpg") }

        //不足
        files.filter { file-> !file.exists()}
                .forEach{file->println("不足:"+file.name)}
        //余計
        File("./images/${name}/")
                .listFiles()
                .filter{f->files.indexOf(f)==-1}
                .forEach{file->println("余計:"+file.name)}

        println()
    }

    check("goseki")
    check("skill")
    check("value0")
    check("value1")
    check("value2")
    check("slot")
}
