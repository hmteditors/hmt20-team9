
import edu.holycross.shot.cite._
import scala.io.Source
import java.io.PrintWriter
import java.io.File

import scala.util.Try

// Settings
/** Size of image in paleography displays.*/
val imageSize = 800
/** Binary image service*/
val imgService = "http://www.homermultitext.org/hmt-digital/images?request=GetBinaryImage"

val repoDirectory: String = "./"

val imgUrlBase = "http://www.homermultitext.org/iipsrv?OBJ=IIP,1.0&FIF=/project/homer/pyramidal/VenA/"

object Images {

  def iliad = {
    val urlBase = s"${imgService}&w=${imageSize}&urn="
    val report =  StringBuilder.newBuilder
    report.append("# Verification of index for *Iliad* text\n\n")
    report .append("| Text     | Image     |\n| :------------- | :------------- |\n")


    val iliadDataFile = repoDirectory + "relations/venA-textToImage-Iliad.cex"
    // read file, dropping header
    val iliadLines = Source.fromFile(iliadDataFile).getLines.toVector.drop(1)
    
    //urn, text, image

///VA012RN-0013.tif&RGN=0.172,0.0998,0.058,0.0218&WID=9000&CVT=JPEG
    for (entry <- iliadLines) {
      val columns = entry.split("#")
      val txt : Try[String] = Try(CtsUrn(columns(0)).passageComponent)
      val img : Try[Cite2Urn] = Try(Cite2Urn(columns(2)))


      if (img.isSuccess && txt.isSuccess) {

      } else {

        println(img)
      }

      val idDisplay = if (txt.isSuccess) {txt.get} else {txt}
      val imgLink = if (img.isSuccess ) {
        val splits = img.get.objectComponent.split("@")
        val binaryImage =              s"${imgUrlBase}${splits(0)}.tif&RGN=${img.get.objectExtension}&WID=${imageSize}&CVT=JPEG"


        println(binaryImage)
        s"![${txt.get}](${binaryImage})"

       } else {img}

      report.append (s"| ${idDisplay} | ${imgLink} | \n")
    }
    report.append("\n\n")
    val iliadReportFile = new File(repoDirectory + "reports/indexing-iliad.md")
    new PrintWriter(iliadReportFile) { write(report.toString); close }
    println("Iliad report is in reports/indexing-iliad.md")

  }


}
