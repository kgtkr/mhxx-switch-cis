package com.github.kgtkr.mhxxSwitchCIS

import javax.swing.*
import java.awt.*
import java.awt.image.*
import javax.imageio.ImageIO

class ParamFrame:JFrame("パラメーター調整"){
    var image:BufferedImage?=null

    val imageLabel=JLabel()

    val rSlider=JSlider(0,255,ImageConfig.COLOR.red)
    val rLabel=JLabel()

    val gSlider=JSlider(0,255,ImageConfig.COLOR.green)
    val gLabel=JLabel()

    val bSlider=JSlider(0,255,ImageConfig.COLOR.blue)
    val bLabel=JLabel()

    val thSlider=JSlider(0,300000,ImageConfig.THRESHOLD)
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
        val imageLabel=JLabel()
        imagePanel.add(this.imageLabel)

        val toolPanel =JPanel()

        this.rSlider.addChangeListener {
            this.updateImage()
        }
        this.gSlider.addChangeListener {
            this.updateImage()
        }
        this.bSlider.addChangeListener {
            this.updateImage()
        }
        this.thSlider.addChangeListener {
            this.updateImage()
        }

        toolPanel.add(this.rSlider)
        toolPanel.add(this.rLabel)
        toolPanel.add(this.gSlider)
        toolPanel.add(this.gLabel)
        toolPanel.add(this.bSlider)
        toolPanel.add(this.bLabel)
        toolPanel.add(this.thSlider)
        toolPanel.add(this.thLabel)


        this.contentPane.add(imagePanel, BorderLayout.CENTER)
        this.contentPane.add(toolPanel, BorderLayout.SOUTH)
        this.isVisible=true
    }

    fun updateImage(){
        val image=this.image
        if(image==null)return

        this.rLabel.text=this.rSlider.value.toString()
        this.gLabel.text=this.gSlider.value.toString()
        this.bLabel.text=this.bSlider.value.toString()
        this.thLabel.text=this.thSlider.value.toString()

        val bitImage=ImageSearch
                .toBitImage(image,Color(this.rSlider.value,this.gSlider.value,this.bSlider.value)
                ,this.thSlider.value)
        val bufImage=BufferedImage(image.width,image.height,image.type)
        for(x in 0..image.width-1){
            for(y in 0..image.height-1){
                bufImage.setRGB(x,y,if(bitImage[x][y])Color.BLACK.rgb else Color.WHITE.rgb)
            }
        }

        this.imageLabel.icon=ImageIcon(bufImage)
    }
}