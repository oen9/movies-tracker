package oen.mtrack.components

import oen.mtrack.Credential
import oen.mtrack.ajax.AjaxHelper
import oen.mtrack.materialize.JQueryHelper
import org.scalajs.dom.raw.KeyboardEvent

class ComponentsLogic(staticComponents: StaticComponents,
                      cacheData: CacheData,
                      jQueryHelper: JQueryHelper,
                      ajaxHelper: AjaxHelper) {

  def init(): Unit = {
    initSignIn()

    jQueryHelper.initMaterialize()
  }

  protected def initSignIn() = {
    val signInComp = staticComponents.signIn
    signInComp.signInButton.onclick = _ => signIn()
    signInComp.name.onkeydown = (e: KeyboardEvent) => if ("Enter" == e.key) signIn()
    signInComp.passwd.onkeydown = (e: KeyboardEvent) => if ("Enter" == e.key) signIn()
  }

  protected def signIn() = {
    val signInComp = staticComponents.signIn
    val name = Some(signInComp.name.value).filter(!_.isEmpty)
    val passwd = Some(signInComp.passwd.value).filter(!_.isEmpty)

    for { n <- name; p <- passwd } {
      jQueryHelper.hideElement(signInComp.notification)

      val credential = Credential(n, p)
      ajaxHelper.signIn(credential, { t =>
        cacheData.username = Some(n)
        cacheData.token = t

        println(s"Signed in as $n with token $t")
        signInComp.passwd.value = ""
      }, {
        jQueryHelper.showElement(signInComp.notification)
      })
    }

  }
}
