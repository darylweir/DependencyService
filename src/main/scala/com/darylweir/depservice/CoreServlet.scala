package com.darylweir.depservice

import org.scalatra._

class FirstServlet extends MyfirstwebappStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world! It's me, Daryl.</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }
  
  get("/dependencies/?") {
    val variable = params.getOrElse("variable", halt(400,"No value specified"))
    <p>The requested variable is {variable}</p>
  }

}
