import edu.holycross.shot.scm._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._
import org.homermultitext.edmodel._
import java.io.PrintWriter
import java.io.File


object Markup {

  def texts = {
    val repoDirectory: String = "./"

    val propertiesFile = repoDirectory + "editions/xmlrepository.properties"
    val textRepository = LocalFileConverter.textRepoFromPropertiesFile(propertiesFile)

    val tokens = TeiReader.fromCorpus(textRepository.corpus)

    val failures = tokens.filter(_.analysis.errors.nonEmpty)
    val report =  StringBuilder.newBuilder
    report.append("#Verification of XML markup\n\n")
    if (failures.isEmpty) {
      println("\n\nNo errors in text edition.  εὖγε  !")
    } else {

      report.append(s"\n\nFound ${failures.size} errors.\n\n")

      for (f <- failures) {
        report.append("-   " + f.analysis.errorReport(", ") + "\n\n")
      }

      val markupReport = new File(repoDirectory + "reports/xml-editing.md")
      new PrintWriter(markupReport) { write(report.toString); close }
      println("Markup report is in reports/xml-editing.md")
    }
  }
}
