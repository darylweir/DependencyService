package com.darylweir.depservice

import org.scalatra.test.scalatest._

/**Lightweight test harness for the web service at /dependencies
 * 
 */
class CoreServletSpec extends ScalatraFlatSpec { 
  

  addServlet(classOf[CoreServlet], "/*")
  
  "The web service" should "return a success code for a valid request" in {
    get("/dependencies?variable=ux_kickstart") {
      status should equal (200)
    }
  }
  
  it should "return a 404 code when a bad variable is passed" in {
    get("/dependencies?variable=missingkey") {
      status should equal (404)
    }
  }
  
  it should "return a 400 code when no variable is passed" in {
    get("/dependencies") {
      status should equal (400)
    }
  }
  
  it should "return a 404 code for invalid urls" in {
    get("/dependencies42") {
      status should equal (404)
    }
  }
  
  it should "return a 405 code for invalid HTTP method" in {
    post("/dependencies?variable=ux_kickstart") {
      status should equal (405)
    }
  }

}
