package com.github.kgtkr.mhxxSwitchCIS

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.util.*

fun dataCmd(){
    toData("./images/goseki","./data/goseki")
    toData("./images/skill","./data/skill")
    toData("./images/slot","./data/slot")
    toData("./images/value0","./data/value0")
    toData("./images/value1","./data/value1")
    toData("./images/value2","./data/value2")
}

private fun toData(input:String,output:String){
    val data=File(input)
            .listFiles()
            .map{f->Pair(FilenameUtils.getBaseName(f.name),FileUtils.readFileToByteArray(f))}
            .map{data->Pair(data.first,Base64.getEncoder().encodeToString(data.second))}
            .map{data->"${data.first},${data.second}"}
            .joinToString("\n")

    FileUtils.write(File(output),data,"utf8")
}