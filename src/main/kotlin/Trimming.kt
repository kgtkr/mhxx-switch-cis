package com.github.kgtkr.mhxxSwitchCIS

import org.apache.commons.io.FileUtils
import java.io.File
import javax.imageio.ImageIO

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
                        .forEach{(i,image)->
                            ImageIO.write(image,
                                    "jpeg",
                                    File("./trimming/oneSkillValue${i}/${row.index}.jpg"))}
                ImageIO.write(row.value.twoSkillName,
                        "jpeg",
                        File("./trimming/twoSkillName/${row.index}.jpg"))
                row.value.twoSkillValue.toList().withIndex()
                        .forEach{(i,image)->
                            ImageIO.write(image,
                                    "jpeg",
                                    File("./trimming/twoSkillValue${i}/${row.index}.jpg"))}
                ImageIO.write(row.value.slot,
                        "jpeg",
                        File("./trimming/slot/${row.index}.jpg"))
            }
}
