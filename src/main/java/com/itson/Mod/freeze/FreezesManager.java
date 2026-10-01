package com.itson.Mod.freeze;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class FreezesManager {

  private final Set<UUID> frozen = new HashSet<>();

  public boolean isFrozen(UUID uuid) {
    return frozen.contains(uuid);
  }

  public void freeze(UUID uuid) {
    frozen.add(uuid);
  }

  public void unfreeze(UUID uuid) {
    frozen.remove(uuid);
  }

  public void clear() {
    frozen.clear();
  }
}
