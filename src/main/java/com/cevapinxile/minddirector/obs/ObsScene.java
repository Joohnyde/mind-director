package com.cevapinxile.minddirector.obs;

public enum ObsScene {
  All,
  Cards,
  Duel,
  Full,
  Single,
  Temp
}

/*Think about making this a class with static instances that own the scene.
* I could then use obsService.prepareScene(ObsScene.ALL).commit();*/