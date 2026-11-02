# AGENTS.md

Color Beans: a libGDX falling-block game where you match beans of the same color. Every module wraps `core`. Desktop, HTML (GWT), Android and iOS (RoboVM) are thin launchers around it.

## Commands

- Run on desktop: `./gradlew desktop:run` (the task already adds `-XstartOnFirstThread` on macOS)
- Run in the browser: `./gradlew html:superDev`, then open http://localhost:8080
- Run on iOS: `./gradlew ios:launchIPhoneSimulator -Probovm.device.name="iPhone 17"` (a name from `xcrun simctl list devices`)
- Release builds: `./gradlew desktop:distDesktop`, `./gradlew html:distGWT`

Use JDK 17 for Gradle; it's the tested version.

There is no test suite. Check a change by building it and running the game.

## Core must compile under GWT

The browser build compiles `core/src` (`G.gwt.xml`) to JavaScript, so `core` can only use the JRE subset GWT emulates. That rules out reflection, `String.format`, `java.io` files, threads and most of `java.util.concurrent`. Use libGDX equivalents instead (`Gdx.files`, `Json`, `Array`, `ObjectMap`). Platform-specific code goes in the launcher modules. After touching `core`, run `./gradlew html:gwtCompile`; desktop builds won't catch these errors.

`Store` saves the config, scores, debug options and each game mode's match as JSON in libGDX `Preferences`: `~/.prefs/com.vpjardim.colorbeans` on desktop, `localStorage` in the browser. GWT's reflection needs each saved class listed under `gdx.reflect.include` in `html/src/com/vpjardim/colorbeans/GdxDefinition.gwt.xml`, and it can't set `final` fields.

Before upgrading libGDX, read `html/src/com/vpjardim/colorbeans/FreetypeFix.gwt.xml`: web FreeType comes from a third-party library that must match libGDX's version, and that file patches it.

## Assets

- Audio: iOS can't play `.ogg`. After changing `assets/audio/`, run `./gradlew tools:convertAudioForIOS` and commit the `.m4a` copies it writes to `ios/assets/audio/`.
- Sprites: edit sources in `to_pack/`, then run `tools`' `PackTextures` from the repo root. It writes to `packed/`; copy `pack.atlas` and `pack*.png` from there into `assets/img/`.
- `to_pack/` and `packed/` are gitignored.

## Code orientation

- `G` is the `Game` and a global singleton (`G.game`). Screens, input, assets, audio and config hang off it.
- Touch input has y pointing down and rendering has y pointing up. See the diagram in `G.java`.
