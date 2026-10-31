# AGENTS.md

Color Beans: a libGDX falling-block game where you match beans of the same color. Every module wraps `core`. Desktop, HTML (GWT) and Android are thin launchers around it.

## Commands

- Run on desktop: `./gradlew desktop:run` (the task already adds `-XstartOnFirstThread` on macOS)
- Run in the browser: `./gradlew html:superDev`, then open http://localhost:8080. It also opens GWT's dev mode window; refreshing the page recompiles changed sources
- Release builds: `./gradlew desktop:distDesktop`, `./gradlew html:distGWT` (output in `html/build/dist`), `./gradlew android:assembleRelease`

The `AndroidKey` signing config in `android/build.gradle` is only defined when the `AndroidKeys` env var points at the folder with `AndroidKey.properties`. It is exported in `~/.zshrc`; other builds don't need it.

Use JDK 17 for Gradle. Gradle 9 needs at least 17, and 17 is the tested version.

There is no test suite. Check a change by building it and running the game.

## Core must compile under GWT

The browser build compiles `core/src` (`G.gwt.xml`) to JavaScript, so `core` can only use the JRE subset GWT emulates. That rules out reflection, `String.format`, `java.io` files, threads and most of `java.util.concurrent`. Use libGDX equivalents instead (`Gdx.files`, `Json`, `Array`, `ObjectMap`). Platform-specific code goes in the launcher modules. After touching `core`, run `./gradlew html:gwtCompile`; desktop builds won't catch these errors.

FreeType on the web comes from the third-party `gdx-freetype-gwt`, whose version must match libGDX's. `html/src/.../FreetypeFix.gwt.xml` patches a bug in it; see that file before upgrading either.

## Assets

- Sprites: edit sources in `to_pack/`, then run `tools`' `PackTextures` from the repo root. It writes to `packed/`; copy `pack.atlas` and `pack*.png` from there into `assets/img/`.
- `to_pack/`, `packed/` and `state/` are gitignored. `state/` holds runtime saves (`cfg.json`, `scores.json`), relative to the working directory: `assets/state/` under `desktop:run`, the repo root's `state/` under the IDE run configs.

## Code orientation

- `G` is the `Game` and a global singleton (`G.game`). Screens, input, assets, audio and config hang off it.
- Touch input has y pointing down and rendering has y pointing up. See the diagram in `G.java`.
