
import edu.holycross.shot.cite._
import scala.io.Source
import java.io.PrintWriter
import java.io.File

import scala.util.Try

// Settings
/** Size of image in paleography displays.*/
val imageSize = 50
/** Binary image service*/
val imgService = "http://www.homermultitext.org/hmt-digital/images?request=GetBinaryImage"

val repoDirectory: String = "./"

val imgUrlBase = "http://www.homermultitext.org/iipsrv?OBJ=IIP,1.0&FIF=/project/homer/pyramidal/VenA/"

object Paleography {

/*
  def view = {
    scholia
    iliad
  }

  def scholia = {
    val urlBase = s"${imgService}&w=${imageSize}&urn="
    val report =  StringBuilder.newBuilder
    report.append("# Paleographic verification: *scholia*\n\n")
    report .append("| Record | Reading     | Image     |\n| :------------- | :------------- |\n")

    val reader = CSVReader.open(new File(scholiaDataFile))
    val entries = reader.allWithHeaders()

    for (entry <- entries) {
      val img = entry("Image")
      try {
        val txt = CtsUrn(entry("TextUrn"))
        val reading = txt.passageNodeSubrefText

        report.append(s"| ${reading} | ![${txt}](${urlBase}${img}) |\n")
      } catch {
        case t: Throwable => report.append(s"| ${t.getMessage()} | Text reference: " + entry("TextUrn") + " |\n")
      }
    }
    report.append("\n\n")
    new PrintWriter(scholiaReportFile) { write(report.toString); close }
  }
  */

  def iliad = {
    val urlBase = s"${imgService}&w=${imageSize}&urn="
    val report =  StringBuilder.newBuilder
    report.append("# Paleographic verification: *Iliad* text\n\n")
    report .append("| Record | Reading     | Image     |\n| :------------- | :------------- |\n")


    val iliadDataFile = repoDirectory + "paleography/paleography-iliad.cex"
    // read file, dropping header
    val iliadLines = Source.fromFile(iliadDataFile).getLines.toVector.drop(1)
    //urn, text, image

///VA012RN-0013.tif&RGN=0.172,0.0998,0.058,0.0218&WID=9000&CVT=JPEG
    for (entry <- iliadLines) {
      val columns = entry.split("#")
      val urn : Try[Cite2Urn] = Try(Cite2Urn(columns(0)))
      val txt : Try[CtsUrn] = Try(CtsUrn(columns(1)))
      val img : Try[Cite2Urn] = Try(Cite2Urn(columns(2)))


      if (img.isSuccess && txt.isSuccess) {

      } else {

        println(img)
      }

      val idDisplay = if (urn.isSuccess) {urn.get} else {urn}
      val reading = if (txt.isSuccess) { txt.get.passageNodeSubrefText} else { txt}
      val imgLink = if (img.isSuccess && txt.isSuccess) {
        val splits = img.get.objectComponent.split("@")
        s"![reading](${imgUrlBase}${splits(0)}.tif&RGN=${img.get.objectExtension}&WID=${imageSize}&CVT=JPEG])"
       } else {img}

      report.append (s"| ${idDisplay} | ${reading} | ${imgLink} | \n")
    }
    report.append("\n\n")
    val iliadReportFile = new File(repoDirectory + "reports/paleography-iliad.md")
    new PrintWriter(iliadReportFile) { write(report.toString); close }
    println("Iliad report is in reports/paleography-iliad.md")

  }


}
