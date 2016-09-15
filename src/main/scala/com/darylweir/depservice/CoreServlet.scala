package com.darylweir.depservice

import org.scalatra._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class CoreServlet extends DepServiceStack with JacksonJsonSupport {
  
  protected implicit val jsonFormats: Formats = DefaultFormats
  
  val datastore = new DataModel

  /**Sends all responses in json format
   * 
   */
  before() {
    contentType = formats("json")
  }
  
  /**Implements the API for /dependencies
   * 
   * Looks for a query parameter named variable.
   * If this is present in the datastore, returns a JSON list of all dependencies, sorted
   * by mutual information. 
   * If variable is not present in the datastore, returns a 404 error.
   * If variable is not given as a query parameter, returns a 400 bad request error.
   */
  get("/dependencies/?") {
    val variable = params.getOrElse("variable", halt(400,Error("IncompleteRequest","Input parameter variable missing.")))
    datastore.get(variable) match {
      case Some(x) => x
      case None => NotFound(Error("KeyNotFound","Dependency list for key "+variable+" was not found."))
    }
  }
  
  /**Basic handler for all other routes
   * 
   * Simply gives a 404 and a brief JSON error summary.
   */
  notFound {
    NotFound(Error("ResourceNotFound","No resource found at this URI"))
  }

}

/**Simple case class to return when a bad request is made. 
 * 
 * @param errorType, a name for the class of error
 * @param description, a more detailed description of the error.
 */
case class Error(errorType: String, description: String);
