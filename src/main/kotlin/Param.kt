package com.github.kgtkr.mhxxSwitchCIS

import javax.swing.*
import java.awt.*
import java.awt.image.*
import javax.imageio.ImageIO

fun paramCmd(){
    ParamFrame()
}

class ParamFrame:JFrame("パラメーター調整"){
    var image:BufferedImage?=null

    val imageLabel=JLabel()

    val color=JColorChooser(ImageConfig.COLOR)

    val thSlider=JSlider(0,30000,ImageConfig.THRESHOLD)
    val thLabel=JLabel()

    init{
        this.size=Dimension(500,500)
        this.defaultCloseOperation=JFrame.EXIT_ON_CLOSE

        val menuBar=JMenuBar()
        val fileMenu=JMenu("File")
        val openMenuItem=JMenuItem("Open")
        openMenuItem.addActionListener {
            val filechooser = JFileChooser(".")
            if(filechooser.showOpenDialog(this)==0){
                this.image=ImageIO.read(filechooser.selectedFile)
                this.updateImage()
            }
        }

        fileMenu.add(openMenuItem)
        menuBar.add(fileMenu)
        this.jMenuBar=menuBar

        val imagePanel =JPanel()
        imagePanel.add(this.imageLabel)


        this.color.selectionModel.addChangeListener {
            this.updateImage()
        }
        this.thSlider.addChangeListener {
            this.updateImage()
        }

        val colorPanel =JPanel()
        colorPanel.add(this.color)

        val toolPanel =JPanel()
        toolPanel.add(this.thSlider)
        toolPanel.add(this.thLabel)


        this.contentPane.add(imagePanel, BorderLayout.CENTER)
        this.contentPane.add(colorPanel, BorderLayout.SOUTH)
        this.contentPane.add(toolPanel, BorderLayout.NORTH)
        this.isVisible=true
    }

    fun updateImage(){
        val image=this.image
        if(image==null)return

        this.thLabel.text=this.thSlider.value.toString()

        val bitImage=image.toBitImage(this.color.color,this.thSlider.value)
        val bufImage=BufferedImage(image.width,image.height,image.type)
        for(x in 0..image.width-1){
            for(y in 0..image.height-1){
                bufImage.setRGB(x,y,if(bitImage[x][y])Color.BLACK.rgb else Color.WHITE.rgb)
            }
        }

        this.imageLabel.icon=ImageIcon(bufImage)
    }
}