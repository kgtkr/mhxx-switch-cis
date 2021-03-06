package com.github.kgtkr.mhxxSwitchCIS
import java.awt.image.BufferedImage

typealias IC=ImageConfig

object ImageTrimming{
    //生画像→いらない所を切り取る
    fun trimmingImage(image:BufferedImage):BufferedImage{
        if(image.width!=IC.IMAGE_W||image.height!=IC.IMAGE_H){
            throw IllegalArgumentException("画像サイズは${IC.IMAGE_W}*${IC.IMAGE_H}にして下さい。");
        }

        return image.getSubimage(IC.TABLE_X,IC.TABLE_Y,IC.TABLE_W,IC.TABLE_H)
    }

    fun trimmingTable(table:BufferedImage):List<BufferedImage>{
        return (0..IC.ROW_COUNT-1)
                .map{i -> table.getSubimage(0,i*IC.ROW_H,IC.TABLE_W,IC.ROW_H) }
    }

    fun trimmingRow(row:BufferedImage):Rows{
        fun valTri(image:BufferedImage):Triple<BufferedImage,BufferedImage,BufferedImage>{
            val list=(0..2)
                    .map{i->image.getSubimage(IC.SKILL_VALUE_NUMBER_W*i,0,IC.SKILL_VALUE_NUMBER_W,IC.ROW_H)}
            return Triple(list[0],list[1],list[2])

        }

        val oneVal=row.getSubimage(IC.ONE_SKILL_VALUE_X,0,IC.ONE_SKILL_VALUE_W,IC.ROW_H)
        val twoVal=row.getSubimage(IC.TWO_SKILL_VALUE_X,0,IC.TWO_SKILL_VALUE_W,IC.ROW_H)

        return Rows(row.getSubimage(IC.GOSEKI_X,0,IC.GOSEKI_W,IC.ROW_H),
                row.getSubimage(IC.ONE_SKILL_NAME_X,0,IC.ONE_SKILL_NAME_W,IC.ROW_H),
                valTri(oneVal),
                row.getSubimage(IC.TWO_SKILL_NAME_X,0,IC.TWO_SKILL_NAME_W,IC.ROW_H),
                valTri(twoVal),
                row.getSubimage(IC.SLOT_X,0,IC.SLOT_W,IC.ROW_H))
    }
}

data class Rows(val goseki:BufferedImage,
                val oneSkillName:BufferedImage,
                val oneSkillValue:Triple<BufferedImage,BufferedImage,BufferedImage>,
                val twoSkillName:BufferedImage,
                val twoSkillValue:Triple<BufferedImage,BufferedImage,BufferedImage>,
                val slot:BufferedImage)