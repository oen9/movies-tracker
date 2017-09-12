package oen.mtrack.components

import oen.mtrack.materialize.JQueryHelper

class ComponentsLogic(staticComponents: StaticComponents,
                      cacheData: CacheData,
                      jQueryHelper: JQueryHelper) {

  def init(): Unit = {
    jQueryHelper.initMaterialize()
  }
}
