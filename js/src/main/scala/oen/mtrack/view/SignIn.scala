package oen.mtrack.view

import org.scalajs.dom.html

import scalatags.JsDom.all._

class SignIn extends HtmlView {

  override def get(): html.Div = {
    div(cls := "container center",
      h2(cls := "header", "Sign in!"),
      div(cls := "row",
        form(cls := "col s12",
          div(cls := "row",
            div(cls := "input-field col s12",
              i(cls := "material-icons prefix", "account_circle"),
              input(placeholder := "test", id := "login", tpe := "text", cls := "validate"),
              label(`for` := "login", cls := "active", "login")
            ),
          ),
          div(cls := "row",
            div(cls := "input-field col s12",
              i(cls := "material-icons prefix", "security"),
              input(placeholder := "test", id := "password", tpe := "password", cls := "validate"),
              label(`for` := "password", cls := "active", "password")
            )
          ),
          div(cls := "row",
            button(cls := "btn waves-effect waves-light", tpe := "submit",
              "submit", i(cls := "material-icons right", "send")
            )
          )
        )
      )
    ).render
  }
}
