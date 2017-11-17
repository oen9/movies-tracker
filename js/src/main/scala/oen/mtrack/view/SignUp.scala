package oen.mtrack.view

import org.scalajs.dom.html

import scalatags.JsDom.all._

class SignUp extends HtmlView {

  override def get(): html.Div = {
    div(cls := "container center",
      h2(cls := "header", "Sign in!"),
      div(cls := "row",
        form(cls := "col s12", method := "post",
          div(cls := "row",
            div(cls := "input-field col s12",
              i(cls := "material-icons prefix", "account_circle"),
              input(placeholder := "your new login", id := "login", tpe := "text", cls := "validate"),
              label(`for` := "login", cls := "active", "login")
            ),
          ),
           div(cls := "row",
            div(cls := "input-field col s12",
              i(cls := "material-icons prefix", "security"),
              input(placeholder := "password", id := "password", tpe := "password", cls := "validate"),
              label(`for` := "password", cls := "active", "password")
            )
          ),
          div(cls := "row",
            div(cls := "input-field col s12",
              i(cls := "material-icons prefix", "security"),
              input(placeholder := "again password", id := "password2", tpe := "password", cls := "validate"),
              label(`for` := "password2", cls := "active", "password")
            )
          ),
          div(cls := "row",
            button(cls := "btn waves-effect waves-light", tpe := "submit", "sign up!", i(cls := "material-icons right", "send"))
          )
        )
      )
    ).render
  }
}
