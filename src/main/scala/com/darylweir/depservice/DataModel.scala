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
  
  ProbabilityEstimator.mutualInformation(data, UNIQUE_CLASSES, UNIQUE_CLASSES, "result_customer_was_happy","project_tries_new_ways_or_technologies")

  
  def get: String = data.toString()
  
  def getlist(param:String) : String = {
    return data.col(param).toString
  }
  
  def hasKey(variable: String) : Boolean = data.colIx.contains(variable)
  
  

  
  
}

/**Object for estimating probabilities and related quantities for a data sample.
 * 
 * Given a Saddle Frame object, offers methods to compute probability, joint
 * probability and mutual information.
 * 
 */
object ProbabilityEstimator {
  
  /** Computes the mutual information between samples of two random variables.
   *  
   *  @param data, the Frame object containing the observations
   *  @param classes1, a list of the possible values of the first variable
   *  @param classes2, a list of the possible values of the second variable
   *  @param col1 the String index for the first variable in data
   *  @param col2 the String index for the second variable in data
   *  @throws IllegalArgumentException if classes1 or classes2 is empty, data has now rows, or data does not contain columns named col1 and col2
   */
  def mutualInformation(data: Frame[String, String, Int], classes1: List[Int], classes2: List[Int], col1: String, col2:String) : Double = {
    
    //Error handling
    if (classes1.isEmpty || classes2.isEmpty) throw new IllegalArgumentException("Classes list cannot be empty")
    if (data.numRows == 0) throw new IllegalArgumentException("Data frame is empty")
    if (!data.colIx.contains(col1)) throw new IllegalArgumentException("Data frame has no key "+col1)
    if (!data.colIx.contains(col2)) throw new IllegalArgumentException("Data frame has no key "+col2)
    
    //Extract the relevant columns
    val c1 = data.col(col1)
    val c2 = data.col(col2)
    val c3 = data.col(col1,col2)
    
    //I(X,Y) = sum_x sum_y p(x,y)*log[ p(x,y) / (p(x)p(y)) ]
    var result = 0.0
    for (x <- classes1) {
      val px = probability(x,c1)
//      println("p(x="+x+") = "+px)
      for (y <- classes2) {
        val py = probability(y,c2)
//        println("p(y="+y+") = "+py)
        val pxy = jointProbability(x, y, c3)
//        println("p("+x+","+y+") = "+pxy)
        //0*log0 and 0*log(0/0) both equal 0 by convention 
        if (px != 0.0 && py != 0.0 && pxy != 0.0) {
          result += pxy*log2(pxy / (px*py))
        }
        
      }
    }
    return result
  }
  
  /** Get the probability P(X=x) that a random variable equals a specific value x.
   *  
   *  @param x, the value to estimate the probability of.
   *  @param ser, a 1-column Frame containing a sample of the random variable.
   *  @throws IllegalArgumentException if f is empty or has more than one column
   */
  def probability(x: Int, ser: Frame[String, String, Int]) : Double = {
    if (ser.numCols != 1) throw new IllegalArgumentException("Frame must have one column")
    if (ser.numRows == 0) throw new IllegalArgumentException("Frame is empty")
    var vec = ser.toMat.toVec
    return 1.0 * vec.countif(_ == x) / vec.length
  }
  
  /** Get the joint probability P(X=x,Y=y) of an event (x,y) when sampling from two random variables.
   *  
   *  @param x, the value for the first random variable
   *  @param y, the value for the second random variable
   *  @param f, a 2-column Frame with observations of X in the first column and observations of Y in the second
   *  @throws IllegalArgumentException if f has no rows or more/less than 2 columns
   */
  def jointProbability(x: Int, y: Int, f: Frame[String, String, Int]) : Double = {
    //Error handling
    if (f.numCols != 2) throw new IllegalArgumentException("Frame must have two columns")
    if (f.numRows == 0) throw new IllegalArgumentException("Frame is empty")
    
    //Get the data as a matrix
    var mat = f.toMat
    //Find where X=x and Y=y
    var bothTrue = (mat.col(0) =? x) && (mat.col(1) =? y)
    bothTrue = bothTrue.filter(_ == true)
    //Normalise to get probability
    return 1.0* bothTrue.length / f.numRows;
  }
  
  /*
   * Gets the logarithm to the base 2 of parameter x.
   * Simple change of basis formula, log_a(x) = log_b(x) / log_b(a)
   */
  private def log2(x: Double) : Double = log(x) / log(2)
  
}