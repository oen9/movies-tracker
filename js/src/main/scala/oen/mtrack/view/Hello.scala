package oen.mtrack.view

import org.scalajs.dom.html
import scalatags.JsDom.all._

class Hello extends HtmlView {

  override def get(): html.Div =
    div(cls := "container",
      h2(cls := "header", "Welcome!"),
      p(
        """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
          |Sed pellentesque accumsan blandit. Aenean quam nulla, malesuada non vulputate quis, ornare eu eros.
          |Ut metus sem, lobortis in efficitur nec, egestas semper turpis. Aenean iaculis sollicitudin purus,
          |sit amet scelerisque mi finibus in. Aliquam sit amet pretium velit, in vulputate sem. Nam dolor tellus,
          |egestas sit amet tellus non, congue iaculis nibh. Ut sagittis tortor non neque efficitur tristique.
          |Duis dictum lobortis malesuada. Integer sollicitudin neque a metus venenatis vulputate. Donec a dui tellus.
          |Curabitur aliquam vel metus in posuere. Donec volutpat placerat iaculis. Nullam in tincidunt nisi,
          |vel sagittis turpis. Nulla quis justo sit amet purus efficitur fringilla. Ut tempus condimentum arcu ut aliquam.
          |Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.""".stripMargin)
    ).render
}
