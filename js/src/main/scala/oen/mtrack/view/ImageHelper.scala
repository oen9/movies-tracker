package oen.mtrack.view

object ImageHelper {

  @deprecated
  def getSrcW150(src: Option[String]): String = {
    getSrcWihtDefault(src, 150)
  }

  def getSrcW300(src: Option[String]): String = {
    getSrcWihtDefault(src, 300)
  }

  protected def getSrcWihtDefault(src: Option[String], w: Int): String = {
    src
      .map(s => s"https://image.tmdb.org/t/p/w$w$s")
      .getOrElse("/front-res/image-unavailable1.png")
  }
}
