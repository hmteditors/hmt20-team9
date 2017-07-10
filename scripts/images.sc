
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

  def listFiles(dir: String): Vector[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      val realFiles =  d.listFiles.filter(_.isFile).toVector
      realFiles.filter(_.getName.matches(".+cex"))
    } else {
        Vector[File]()
    }
  }


  def scholia = {


    val urlBase = s"${imgService}&w=${imageSize}&urn="
    val report =  StringBuilder.newBuilder
    report.append("#Verification of indexing of scholia\n\n")
    val urlParams =  StringBuilder.newBuilder

    val files = listFiles(repoDirectory + "dse-models")

    for (f <- files) {
      val lines = Source.fromFile(f).getLines.toVector.drop(1)
      if (lines.size > 1) {
        val cols = lines(1).split("#")
        val txt : Try[String] = Try(CtsUrn(cols(0)).work)
        if (txt.isSuccess) {
          report.append(s"\n\n## Scholia group: ${txt.get} ")

          report.append("\n\n| Scholion     | Image     |\n| :------------- | :------------- |\n")


          for (entry <- lines) {
            val columns = entry.split("#")
            val txt : Try[String] = Try(CtsUrn(columns(0)).passageComponent)
            val img : Try[Cite2Urn] = Try(Cite2Urn(columns(1)))
            val folio : Try[Cite2Urn] = Try(Cite2Urn(columns(2)))

            val idDisplay = if (txt.isSuccess && folio.isSuccess) {
              txt.get + ", folio " + folio.get
            } else {txt + " " + folio}

            val imgLink = if (img.isSuccess ) {
              urlParams.append(s"urn=${img.get}&")
              val splits = img.get.objectComponent.split("@")
              val binaryImage =              s"${imgUrlBase}${splits(0)}.tif&RGN=${img.get.objectExtension}&WID=${imageSize}&CVT=JPEG"


              s"![${txt.get}](${binaryImage})"

             } else {img}

             report.append (s"| ${idDisplay} | ${imgLink} | \n")
          }
        } else {}
      } else {
        //skip
      }


    }
    report.append("\n\nUse the image citation tool to check completeness.\n")
    report.append(s"[Here](http://www.homermultitext.org/ict2/?${urlParams.toString.dropRight(1)})")


    val reportFile = new File(repoDirectory + "reports/indexing-scholia.md")
    new PrintWriter(reportFile) { write(report.toString); close }
    println("Scholia indexing report is in reports/indexing-iliad.md")
  }


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



      val idDisplay = if (txt.isSuccess) {txt.get} else {txt}
      val imgLink = if (img.isSuccess ) {
        val splits = img.get.objectComponent.split("@")
        val binaryImage =              s"${imgUrlBase}${splits(0)}.tif&RGN=${img.get.objectExtension}&WID=${imageSize}&CVT=JPEG"
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
