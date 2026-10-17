# Prime: Reality Glitch — Forge 1.12.2

**Target:** Minecraft 1.12.2 · Forge 14.23.5.2860 · Java 8

## Current implemented prototype mechanics
- Villagers retaliate after a player attacks them.
- Hostile mobs are pushed away from nearby players.
- Creepers attempt to fill nearby air gaps using adjacent full blocks.
- Breaking a block has a chance to hurt the player and restore the block.
- Periodically, the player's horizontal momentum is reversed.
- GitHub Actions workflow attempts to build and uploads the JAR artifact.

## Build
This legacy ForgeGradle 2.3 project requires Java 8 and an appropriate Gradle installation. Run:

```sh
gradle --no-daemon build
```

The resulting JAR should be in `build/libs/`.

## Important scope statement
This is **not yet a fully reversed Minecraft game**. The client GUI class is only a placeholder and does not implement a mirrored logo or fully mirrored, clickable menus. Input inversion, complete gravity/fluid/recipe/trading/audio reversals, and a comprehensive block-by-block behavior system remain to be implemented and tested. Do not present this build as a finished all-systems reversal.
