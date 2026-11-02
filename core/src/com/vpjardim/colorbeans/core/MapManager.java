package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;

/**
 * Runs the matches of a game mode. A match goes through 3 phases: the maps play
 * until it ends, the winners celebrate, then the results show until
 * {@link #nextMatch()}. Without {@link Cfg.Game#LOST_SHOW_RESULTS} the next
 * match starts right after the end
 */
public abstract class MapManager {
    public static final int GAME_CONTINUE = 1;
    public static final int GAME_ZEROED = 2;

    public static final int PHASE_PLAYING = 1;
    /** The winners' beans pop. See {@link com.vpjardim.colorbeans.animation.Animations#celebrate()} */
    public static final int PHASE_CELEBRATING = 2;
    /** Each map shows its results, waiting for {@link #nextMatch()} */
    public static final int PHASE_RESULTS = 3;

    /** Fraction of the music's volume while the match end shows */
    private static final float DUCKED_MUSIC = 0.3f;

    public Cfg.Game gameCfg;
    public Array<Map> maps;
    public Array<Map> opps;
    public Array<MapRender> render;
    public int gameStatus = GAME_CONTINUE;
    public int phase = PHASE_PLAYING;
    /** Seconds since the phase started */
    public float phaseTime;
    /** Maps that won the match that ended */
    public final Array<Map> winners = new Array<>();
    private boolean paused = false;

    public abstract void init();

    public Map getOpponent(int excludeIndex) {
        if (maps.size <= 1)
            return null;

        opps.clear();
        opps.addAll(maps);
        opps.removeIndex(excludeIndex);

        for (int i = 0; i < opps.size; i++) {
            Map opp = opps.get(i);

            // TODO: fix cause the map go to GRAVITY_FALL state in winLost method if is
            // autoRestart
            if (opp.isInState(Map.MState.OVER) || opp.isInState(Map.MState.DONE))
                opps.removeIndex(i);
        }

        if (opps.size > 0) {
            return opps.get(MathUtils.random(0, opps.size - 1));
        }

        return null;
    }

    /** Called once when a match ends with a winner, before the celebration */
    public abstract void mapWin(int mapIndex);

    /**
     * Sets {@link Map#gameWin} on the maps that won, once the others lost. Here the
     * last map standing wins
     */
    protected void findWinners() {
        if (maps.size <= 1)
            return;

        Map playing = null;
        int playingCount = 0;

        for (Map m : maps) {
            if (!Map.lost(m)) {
                playing = m;
                playingCount++;
            }
        }

        if (playingCount == 1)
            playing.gameWin = true;
    }

    /** Called before the maps restart for the next match */
    protected void prepareNextMatch() {
    }

    /** The text of the button that starts the next match */
    public String nextMatchLabel() {
        return "Play again";
    }

    /** Whether a map is a player's, not the AI's */
    public boolean playersPlayed() {
        for (Map m : maps) {
            if (m.ai == null)
                return true;
        }

        return false;
    }

    /** Whether a player won the match, or else, with no players, the AI */
    public boolean playersWon() {
        boolean playersPlayed = playersPlayed();

        for (Map m : winners) {
            if (m.ai == null || !playersPlayed)
                return true;
        }

        return false;
    }

    /** Moves the match through its phases. Called each frame before the maps update */
    public void winLost() {
        // The match end waits while the game is paused, like when the app goes to the background
        if (paused && phase != PHASE_PLAYING)
            return;

        phaseTime += G.delta;

        if (phase == PHASE_PLAYING) {
            findWinners();

            // The match ends when every map is in DONE state: the winners go to it at once and
            // the others after their lost animation
            for (Map m : maps) {
                if (!m.isInState(Map.MState.DONE))
                    return;
            }

            endMatch();
        } else if (phase == PHASE_CELEBRATING) {
            boolean animating = false;

            for (Map m : winners) {
                animating = animating | m.anim.celebrate();
            }

            if (!animating) {
                setPhase(PHASE_RESULTS);

                if (playersWon())
                    G.game.audio.playEffect("audio/win.ogg");
            }
        }
    }

    private void endMatch() {
        winners.clear();

        for (Map m : maps) {
            if (m.gameWin)
                winners.add(m);
        }

        if (winners.size > 0)
            mapWin(winners.first().index);

        if (gameCfg.lostAct != Cfg.Game.LOST_SHOW_RESULTS) {
            restart();
            return;
        }

        setPhase(PHASE_CELEBRATING);
        G.game.audio.setMusicVolume(DUCKED_MUSIC);

        for (Map m : winners) {
            m.anim.startCelebration();
        }

        // A win plays its jingle after the celebration, a loss right away
        if (!playersWon())
            G.game.audio.playEffect("audio/lose.ogg");
    }

    /** Starts the next match, after the results */
    public void nextMatch() {
        if (phase == PHASE_RESULTS)
            restart();
    }

    private void restart() {
        prepareNextMatch();
        setPhase(PHASE_PLAYING);
        winners.clear();

        for (Map m : maps) {
            m.recycle(true); // TODO: remove and test. It's done in MState
            m.state.changeState(Map.MState.GRAVITY_FALL);
            m.anim.sparks.clear();
        }

        G.game.audio.setMusicVolume(1f);
        setPaused(gameCfg.lostAct == Cfg.Game.LOST_RESTART_PAUSED);

        if (G.game.dbg.mapShapes != null) {
            for (int i = 0; i < maps.size; i++) {
                Map m = maps.get(i);
                m.debugShape(G.game.dbg.mapShapes[i]);
            }
        }
    }

    private void setPhase(int phase) {
        this.phase = phase;
        phaseTime = 0f;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        if (paused) {
            G.game.audio.pauseMusic();
        } else {
            G.game.audio.playMusic();
        }

        this.paused = paused;
    }

    /** Pauses or resumes, only while the maps play */
    public void togglePaused() {
        if (phase == PHASE_PLAYING)
            setPaused(!paused);
    }
}
