package oen.mtrack.components

import oen.mtrack.{Credential, Register}
import oen.mtrack.ajax.AjaxHelper
import oen.mtrack.materialize.JQueryHelper
import org.scalajs.dom
import org.scalajs.dom.raw.KeyboardEvent

class ComponentsLogic(staticComponents: StaticComponents,
                      cacheData: CacheData,
                      jQueryHelper: JQueryHelper,
                      ajaxHelper: AjaxHelper,
                      localStorageService: LocalStorageService) {

  def init(): Unit = {
    initSignIn()
    initSignUp()
    initLogout()

    refreshHeader()

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
        cacheData.data = cacheData.data.copy(username = Some(n), token = t)
        localStorageService.save()

        signInComp.passwd.value = ""
        refreshHeader()
        dom.window.location.hash = "#dashboard"
      }, jQueryHelper.showElement(signInComp.notification))
    }

  }

  protected def initSignUp() = {
    val signUpComp = staticComponents.signUp
    signUpComp.signUpButton.onclick = _ => signUp()
    signUpComp.name.onkeydown = (e: KeyboardEvent) => if ("Enter" == e.key) signUp()
    signUpComp.passwd.onkeydown = (e: KeyboardEvent) => if ("Enter" == e.key) signUp()
    signUpComp.passwd2.onkeydown = (e: KeyboardEvent) => if ("Enter" == e.key) signUp()
  }

  protected def signUp() = {
    val signUpComp = staticComponents.signUp
    val name = Some(signUpComp.name.value).filter(!_.isEmpty)
    val passwd = Some(signUpComp.passwd.value).filter(!_.isEmpty)
    val passwd2 = Some(signUpComp.passwd2.value).filter(!_.isEmpty)

    for { n <- name
          p <- passwd
          p2 <- passwd2 } {

      if (p == p2) {
        jQueryHelper.hideElement(signUpComp.notification)
        val reg = Register(Credential(n, p))

        ajaxHelper.signUp(reg, {
          signUpComp.passwd.value = ""
          signUpComp.passwd2.value = ""
          staticComponents.signIn.name.value = n
          dom.window.location.hash = "#signin"
        }, jQueryHelper.showElement(signUpComp.notification))

      } else {
        jQueryHelper.showElement(signUpComp.notification)
      }
    }
  }

  protected def refreshHeader() = {
    cacheData.data.username match {
      case Some(_) =>
        jQueryHelper.hideElement(staticComponents.header.signin)
        jQueryHelper.hideElement(staticComponents.header.signUp)
        jQueryHelper.showElement(staticComponents.header.dashboard)
        jQueryHelper.showElement(staticComponents.header.logout)
      case None =>
        jQueryHelper.showElement(staticComponents.header.signin)
        jQueryHelper.showElement(staticComponents.header.signUp)
        jQueryHelper.hideElement(staticComponents.header.dashboard)
        jQueryHelper.hideElement(staticComponents.header.logout)
    }
  }

  protected def initLogout() = {
    val headerComp = staticComponents.header
    headerComp.logout.onclick = _ => logout()
  }

  protected def logout() = ajaxHelper.logout(cacheData.data.token, {
    cacheData.data = ImmutableCacheData()
    localStorageService.clearSaved()

    refreshHeader()
    dom.window.location.hash = "#signin"
  })

}
