package oen.mtrack.components

import org.scalajs.dom.html._

import scalatags.JsDom.all._

case class StaticComponents (
  progressbar: Div = div(cls := "progress", div(cls := "indeterminate")).render,
  header: HeaderComp = HeaderComp(),
  signIn: SignInComp = SignInComp(),
  signUp: SignUpComp = SignUpComp(),
  dashboard: DashboardComp = DashboardComp()
)

case class SignInComp(
  name: Input = input(placeholder := "test", id := "name", tpe := "text", cls := "validate", required).render,
  passwd: Input = input(placeholder := "test0", id := "password", tpe := "password", cls := "validate", required).render,
  signInButton: Button = button(cls := "btn waves-effect waves-light", "Sign in!", tpe := "button", i(cls := "material-icons right", "send")).render,
  notification: Div = div(cls := "card-panel red darken-3 white-text", hidden, "Wrong name and/or password!").render
)

case class SignUpComp(
  name: Input = input(placeholder := "your new login", id := "name", tpe := "text", cls := "validate", required).render,
  passwd: Input = input(placeholder := "password", id := "password", tpe := "password", cls := "validate", required).render,
  passwd2: Input = input(placeholder := "again password", id := "password2", tpe := "password", cls := "validate", required).render,
  signUpButton: Button = button(cls := "btn waves-effect waves-light", "Sign up!", tpe := "button", i(cls := "material-icons right", "send")).render,
  notification: Div = div(cls := "card-panel red darken-3 white-text", hidden, "User exists and/or inconsistent passwords!").render
)

case class HeaderComp(
  signin: LI = li(a(href := "#signin", "sign in")).render,
  signUp: LI = li(a(href := "#signup", "sign up")).render,
  dashboard: LI = li(hidden, a(href := "#dashboard", "dashboard")).render,
  logout: LI = li(hidden, a(href := "#", "logout")).render
)

case class DashboardComp(
  moviesList: UList = ul(cls := "collapsible popout", attr("data-collapsible") := "expandable").render,
  searcher: Input = input(tpe := "text", id := "search-movie-input").render,
  searchResults: UList = ul(cls := "collection scrollable-mini").render
)
