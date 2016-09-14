package com.darylweir.depservice

import org.saddle._
import org.saddle.io._
import scala.math.log

class DataModel {
  
  val UNIQUE_CLASSES = List(-1,0,1)
  
  val PATH_TO_DATA = "src/main/webapp/WEB-INF/data/old_projects_dataset.csv"
  
  val file = CsvFile(PATH_TO_DATA);
  val frame = CsvParser.parse()(file).withColIndex(0).withRowIndex(0)
  val data = frame.mapValues(CsvParser.parseInt)
  
  mutualInformation("result_customer_was_happy","project_tries_new_ways_or_technologies")
  mutualInformation("result_customer_was_happy","customer_is_big")

  
  def get: String = data.toString()
  
  def getlist(param:String) : String = {
    return data.col(param).toString
  }
  
  def hasKey(variable: String) : Boolean = data.colIx.contains(variable)
  
  
  private def mutualInformation(col1: String, col2:String) : Double = {
    val c1 = data.col(col1)
    val c2 = data.col(col2)
    val c3 = data.col(col1,col2)
    c3.print(36,2)
    var result = 0.0
    for (x <- UNIQUE_CLASSES) {
      val px = probability(x,c1)
      for (y <- UNIQUE_CLASSES) {
        val py = probability(y,c2)
        val pxy = jointProbability(x, y, c3)
        println("p("+x+","+y+") = "+pxy)
        if (px != 0.0 && py != 0.0 && pxy != 0.0) {
          result += pxy*log2(pxy / (px*py))
        }
        
      }
    }
    println(result)
    return result
  }
  
  private def probability(x: Int, ser: Frame[String, String, Int]) : Double = {
    var vec = ser.toMat.toVec
    return 1.0 * vec.countif(_ == x) / vec.length
  }
  
  private def jointProbability(x: Int, y: Int, f: Frame[String, String, Int]) : Double = {
    var mat = f.toMat
    var bothTrue = (mat.col(0) =? x) && (mat.col(1) =? y)
    bothTrue = bothTrue.filter(_ == true)
    return 1.0* bothTrue.length / mat.numRows;
  }
  
  /*
   * Gets the logarithm to the base 2 of parameter x.
   * Simple change of basis formula, log_a(x) = log_b(x) / log_b(a)
   */
  private def log2(x: Double) : Double = log(x) / log(2)
  
  
}