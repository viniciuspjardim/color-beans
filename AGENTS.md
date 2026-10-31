# AGENTS.md

Color Beans: a libGDX falling-block game where you match beans of the same color. Every module wraps `core`. Desktop, HTML (GWT) and Android are thin launchers around it.

## Commands

- Run on desktop: `./gradlew desktop:run` (the task already adds `-XstartOnFirstThread` on macOS)
- Run in the browser: `./gradlew html:superDev`, then open http://localhost:8080
- Release builds: `./gradlew desktop:distDesktop`, `./gradlew html:distGWT`

Every Gradle command, desktop included, needs the `AndroidKeys` env var: `android/build.gradle` loads `$AndroidKeys/AndroidKey.properties` at configuration time. It is exported in `~/.zshrc`, which non-interactive shells skip, so if it's unset, prefix commands with `AndroidKeys=<value from ~/.zshrc>`.

There is no test suite. Check a change by building it and running the game.

## Core must compile under GWT

The browser build compiles `core/src` (`G.gwt.xml`) to JavaScript, so `core` can only use the JRE subset GWT emulates. That rules out reflection, `String.format`, `java.io` files, threads and most of `java.util.concurrent`. Use libGDX equivalents instead (`Gdx.files`, `Json`, `Array`, `ObjectMap`). Platform-specific code goes in the launcher modules. After touching `core`, run `./gradlew html:compileGwt`; desktop builds won't catch these errors.

## Assets

- Sprites: edit sources in `to_pack/`, then run `tools`' `PackTextures` from the repo root. It writes to `packed/`; copy `pack.atlas` and `pack*.png` from there into `assets/img/`.
- `to_pack/`, `packed/` and `state/` are gitignored. `state/` holds runtime saves (`cfg.json`, `scores.json`), relative to the working directory: `assets/state/` under `desktop:run`, the repo root's `state/` under the IntelliJ run configs.

## Code orientation

- `G` is the `Game` and a global singleton (`G.game`). Screens, input, assets, audio and config hang off it.
- Touch input has y pointing down and rendering has y pointing up. See the diagram in `G.java`.
