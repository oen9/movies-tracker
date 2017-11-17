package oen.mtrack.components

import org.scalajs.dom.html._

import scalatags.JsDom.all._

case class StaticComponents (
  progressbar: Div = div(cls := "progress", div(cls := "indeterminate")).render,
  header: HeaderComp = HeaderComp(),
  signIn: SignInComp = SignInComp()
)

case class SignInComp(
  name: Input = input(placeholder := "test", id := "name", tpe := "text", cls := "validate", required).render,
  passwd: Input = input(placeholder := "test0", id := "password", tpe := "password", cls := "validate", required).render,
  signInButton: Button = button(cls := "btn waves-effect waves-light", "Sign in!", tpe := "button", i(cls := "material-icons right", "send")).render,
  notification: Div = div(cls := "card-panel red darken-3 white-text", hidden, "Wrong name and/or password!").render
)

case class HeaderComp (
  signin: LI = li(a(href := "#signin", "sign in")).render,
  signUp: LI = li(a(href := "#signup", "sign up")).render,
  dashboard: LI = li(hidden, a(href := "#dashboard", "dashboard")).render,
  logout: LI = li(hidden, a(href := "#", "logout")).render
)
