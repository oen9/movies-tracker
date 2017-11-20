package oen.mtrack.view

import oen.mtrack.components.SignUpComp
import org.scalajs.dom.html

import scalatags.JsDom.all._

class SignUp(signUpComp: SignUpComp) extends HtmlView {

  override def get(): html.Div = {
    div(cls := "container center",
      h2(cls := "header", "Sign up!"),
      div(cls := "row",
        form(cls := "col s12", method := "post",
          div(cls := "row",
            div(cls := "input-field col s12",
              i(cls := "material-icons prefix", "account_circle"),
              signUpComp.name,
              label(`for` := "login", cls := "active", "login")
            ),
          ),
           div(cls := "row",
            div(cls := "input-field col s12",
              i(cls := "material-icons prefix", "security"),
              signUpComp.passwd,
              label(`for` := "password", cls := "active", "password")
            )
          ),
          div(cls := "row",
            div(cls := "input-field col s12",
              i(cls := "material-icons prefix", "security"),
              signUpComp.passwd2,
              label(`for` := "password2", cls := "active", "password")
            )
          ),
          div(cls := "row",
            signUpComp.notification
          ),
          div(cls := "row",
            signUpComp.signUpButton
          )
        )
      )
    ).render
  }
}
