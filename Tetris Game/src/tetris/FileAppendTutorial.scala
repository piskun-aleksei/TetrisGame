package tetris
import java.io._

class FileAppendTutorial {

  def writeSaveFile(fileName: String, infoString: String) {

    val writer = new PrintWriter(new File(fileName))
    writer.write(infoString)
    writer.close()

  }
}