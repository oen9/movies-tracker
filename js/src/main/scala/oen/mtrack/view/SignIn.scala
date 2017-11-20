package oen.mtrack.view

import oen.mtrack.components.SignInComp
import org.scalajs.dom.html

import scalatags.JsDom.all._

class SignIn(signInComp: SignInComp) extends HtmlView {

  override def get(): html.Div = {
    div(cls := "container center",
      h2(cls := "header", "Sign in!"),
      div(cls := "row",
        form(cls := "col s12", method := "post",
          div(cls := "row",
            div(cls := "input-field col s12",
              i(cls := "material-icons prefix", "account_circle"),
              signInComp.name,
              label(`for` := "name", cls := "active", "name")
            ),
          ),
          div(cls := "row",
            div(cls := "input-field col s12",
              i(cls := "material-icons prefix", "security"),
              signInComp.passwd,
              label(`for` := "password", cls := "active", "password")
            )
          ),
          div(cls := "row",
            signInComp.notification
          ),
          div(cls := "row",
            signInComp.signInButton
          )
        )
      )
    ).render
  }
}
