/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.vpjardim.colorbeans.ai.AiBase;
import com.vpjardim.colorbeans.animation.Animations;
import com.vpjardim.colorbeans.core.Cfg;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.core.MapManager;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.TargetBase;

/**
 * Represents a field/map where one player can do his actions. The goal is to group / chain
 * {@link Block blocks} of the same color until they reach at least 4. This number can be set in
 * {@link #deleteSize}. Once this group is formed, the color beans/blocks will be deleted to make
 * room to the falling ones ({@link PlayBlocks}). If there isn't enough room (the play blocks are
 * obstructed) it's game over.
 * <pre>
 *
 * Examples of color group (uses the 4 neighborhood):
 * Note: blue = 1, red = 2; deleteSize = 4
 *
 * Ex 1       Ex 2       Ex 3       Ex 4       Ex 5       Ex 6
 * . 1 . .    . . . .    . . . .    . . . .    . . . .    . . 1 .
 * . 1 . .    . 1 . .    . . . 1    . . . 1    . . 1 .    . . 1 .
 * . 1 . .    . 1 . .    . . 1 1    . 1 1 1    . . 1 .    2 2 1 .
 * . 1 . .    2 2 2 2    . 1 2 2    . 1 2 2    . 1 1 .    2 2 1 .
 *
 * Ex 1: all blues are deleted;
 * Ex 2: all reds are deleted;
 * Ex 3: none are deleted, because blue has to groups:
 *       one with 3 blocks, the other with only one;
 * Ex 4: all 5 blues are deleted, cause now they are
 *       in the same group;
 * Ex 5: all blues are deleted;
 * Ex 6: all blues and all reds are deleted.
 * </pre>
 *
 * Blocks in the same color group have the same {@link Block#label}
 *
 * The game can have one or more maps depending on the number of players. One player can disturb
 * other player's map by doing combos that will send trash blocks to the other map.
 * <pre>
 *
 * ==== Standard matrix: row x col ====
 *
 *   Row Col
 * b[ 0][ 0 + OUT_ROW].setColor(1); // Blue
 * b[ 0][14 + OUT_ROW].setColor(2); // Red
 * b[ 6][ 0 + OUT_ROW].setColor(3); // Green
 * b[ 6][14 + OUT_ROW].setColor(4); // Yellow
 *
 *     OUT_ROW    <- Top                floor ->
 * X|x x x x x x |1 0 0 0 0 0 0 0 0 0 0 0 0 0 2
 *  |x x x x x x |0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 *  |x x x x x x |0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 *  |x x x x x x |0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 *  |x x x x x x |0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 *  |x x x x x x |0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 * Y|x x x x x x |3 0 0 0 0 0 0 0 0 0 0 0 0 0 4
 * </pre>
 *
 * At the Game, the matrix has a kind of rotation to render and logics: make the first column a row
 * (top becomes left, bottom becomes right), then make the next column as the next row and so on.
 * <pre>
 *
 * ==== Game matrix: col x row ====
 *
 * b.length    = N_COL (7 default)
 * b[0].length = N_ROW (15 default + OUT_ROW)
 *
 *   Col Row
 * b[ 0][ 0 + OUT_ROW].setColor(1); // Blue
 * b[ 0][14 + OUT_ROW].setColor(2); // Red
 * b[ 6][ 0 + OUT_ROW].setColor(3); // Green
 * b[ 6][14 + OUT_ROW].setColor(4); // Yellow
 *
 * X            Y
 * _ _ 7 cols _ _
 * x x   ..   x x
 * x            x
 * .            . OUT_ROW [out of screen rows]
 * x            x
 * x x   ..   x x
 * --------------
 * 1 0   ..   0 3
 * 0            0
 * .            .
 * .            . 15 rows
 * .            .
 * 0            0
 * 2 0   ..   0 4
 * </pre>
 *
 * @author Vinícius Jardim
 * 21/03/2015
 */
public class Map implements TargetBase {

    // ====== Static members =====>

    /**
     * A finite state machine to track the main states of the map and perform actions required by
     * each state
     */
    public enum MState implements State<Map> {

        FREE_FALL() {

            @Override
            public void enter(Map map) {
                // #debugCode
                Dbg.dbg(Dbg.tag(map), "state = FREE_FALL");

                map.shuffleColAcceleration(0.3f);
                map.afterFreeFallTimer = map.afterFreeFallWait;
                map.freeFallCalc();
                map.anim.freeFall();
            }

            @Override
            public void update(Map map) {

                // Case the player of this map won the game
                if(map.gameWin) {
                    map.state.changeState(MState.DONE);
                    return;
                }

                // Waiting til next frame if the game is paused
                if(map.pause) return;

                // Waiting animation to end
                boolean freeFallAnim = map.anim.freeFall();
                map.anim.deform();

                if(freeFallAnim) return;

                map.afterFreeFallTimer -= G.delta;

                // Wait some time before change state
                if(map.afterFreeFallTimer > 0f) return;

                // If a block change it's position the labels
                // needs to be recalculated
                if(map.blockChanged) {
                    map.state.changeState(MState.LABEL_CALC);
                    map.blockChanged = false;
                }
                // If not, see if is time to add trash blocks
                else if(map.trashBlocksToAdd > 0 && map.trashBlocksTurn) {
                    map.state.changeState(MState.TRASH_ADD);
                }
                // If not, change to play fall state
                else {
                    map.state.changeState(MState.PLAY_FALL);
                }
            }

            @Override
            public void exit(Map map) {}
        },

        LABEL_CALC() {

            @Override
            public void enter(Map map) {
                // #debugCode
                Dbg.dbg(Dbg.tag(map), "state = LABEL_CALC");

                map.mapLinks();

                map.labelCalc();
                map.groupBonusCalc();
                // Chain power: incrementing chain power (combo level)
                map.chainPowerCount++;
                map.scoreCalc();
                map.anim.labelDelete();
            }

            @Override
            public void update(Map map) {

                // Case the player of this map won the game
                if(map.gameWin) {
                    map.state.changeState(MState.DONE);
                    return;
                }

                // Waiting til next frame if the game is paused
                if(map.pause) return;

                // Waiting animation to end
                if(map.anim.labelDelete()) return;

                map.state.changeState(MState.FREE_FALL);
            }

            @Override
            public void exit(Map map) {
                map.cleanScoreCalc();
                map.scoreStr = Integer.toString(map.score);
            }
        },

        PLAY_FALL() {

            @Override
            public void enter(Map map) {
                // #debugCode
                Dbg.dbg(Dbg.tag(map), "state = PLAY_FALL");

                map.chainPowerCount = 0;
                map.scoredBlocks = 0;

                map.gameOver();

                if(map.gameOver) {
                    map.state.changeState(MState.OVER);
                    return;
                }

                map.vPlayMoveTimer = map.beforePlayFallWait;
                map.hPlayMoveTimer = map.beforePlayFallWait;
                map.rPlayMoveTimer = map.beforePlayFallWait;

                map.trashBlocksTurn = true;
                map.throwTrashBlocks();

                // processing input
                map.inputUpdate();
            }

            @Override
            public void update(Map map) {

                // Case the player of this map won the game
                if(map.gameWin) {
                    map.state.changeState(MState.DONE);
                    return;
                }

                // Waiting til next frame if the game is paused
                if(map.pause) return;

                // processing input and input animation
                map.inputUpdate();
                map.pb.playFallCalc();
                map.anim.playFall();
                map.anim.playHorizontal();
                map.anim.playRotation();

                if(map.blockChanged) {
                    map.state.changeState(MState.FREE_FALL);
                }
            }

            @Override
            public void exit(Map map) {
                // #debugCode
                //AiMap aiMap = new AiMap();
                //aiMap.init(map.b, map.deleteSize, map.OUT_ROW);
                //aiMap.iterate(0, 0, 0, 0);
            }
        },

        TRASH_ADD() {

            @Override
            public void enter(Map map) {
                // #debugCode
                Dbg.dbg(Dbg.tag(map), "state = TRASH_ADD");

                map.addTrashBlocks();
                map.trashBlocksTurn = false;
                map.state.changeState(MState.FREE_FALL);
            }

            @Override
            public void update(Map map) {}

            @Override
            public void exit(Map map) {}
        },

        OVER() {

            @Override
            public void enter(Map map) {
                // #debugCode
                Dbg.dbg(Dbg.tag(map), "state = OVER");
                Dbg.inf(Dbg.tag(map), "game over");

                map.shuffleColAcceleration(0.8f);
                map.colAcceleration[map.b.length/2] = map.freeFallAcceleration * 0.8f;
                map.anim.gameOver();
            }

            @Override
            public void update(Map map) {

                // Waiting animation to end
                if(map.anim.gameOver()) return;

                map.state.changeState(MState.DONE);
            }

            @Override
            public void exit(Map map) {}
        },

        DONE() {

            @Override
            public void enter(Map map) {
                // #debugCode
                Dbg.dbg(Dbg.tag(map), "state = DONE");
                if(map.gameWin) {
                    Dbg.inf(Dbg.tag(map), "game win\n");
                }
            }

            @Override
            public void update(Map map) {}

            @Override
            public void exit(Map map) {}
        }
        ;

        @Override
        public boolean onMessage(Map map, Telegram telegram) {
            return false;
        }
    }

    /** Map width (number of block columns): 7 default */
    public static final int N_COL = 7;

    /** Map height (number of block rows): 15 default */
    public static final int N_ROW = 15;

    /**
     * Out of screen rows: space used to place trash blocks and avoid collisions at the top. It`s a
     * kind of back stage that is not shown to the player. 10 default
     */
    public static final int OUT_ROW = 10;

    /** Chain power from 1 to 24+. Ref: https://puyonexus.com/wiki/Chain_Power_Table */
    public static final int[] chainPowerTable = {
            0, 8, 16, 32, 64, 128, 256, 512, 999 // Puyo Puyo
            // 0, 8, 16, 32, 64, 96, 128, 160,   // Puyo Puyo Tsu
            // 192, 224, 256, 288, 320, 352,
            // 384, 416, 448, 480, 512, 544,
            // 576, 608, 640, 672
    };

    /** Color bonus from 1 to 8. Ref: https://puyonexus.com/wiki/Scoring */
    public static final int [] colorBonusTable = {
            0, 3, 6, 12, 24               // classic
            // 0, 2, 4, 8, 16, 20, 24, 28 // fever
    };

    /** Group bonus from 4 to 11+. Ref: https://puyonexus.com/wiki/Scoring */
    public static final int[] groupBonusTable = {
            0, 2, 3, 4, 5, 6, 7, 10   // classic
            // 0, 1, 2, 3, 4, 5, 6, 8 // fever
    };

    public static int getChainPower(int val) {
        val--;
        if(val < 0) return 0;
        if(val >= chainPowerTable.length) return chainPowerTable[chainPowerTable.length -1];
        return chainPowerTable[val];
    }

    public static int getColorBonus(int val) {
        val--;
        if(val < 0) return 0;
        if(val >= colorBonusTable.length) return colorBonusTable[colorBonusTable.length -1];
        return colorBonusTable[val];
    }

    public static int getGroupBonus(int val) {
        val -= 4;
        if(val < 0) return 0;
        if(val >= groupBonusTable.length) return groupBonusTable[groupBonusTable.length -1];
        return groupBonusTable[val];
    }

    // <===== End of static members ======

    /** Reference holding the MapManager object */
    public transient MapManager manager;

    /** Index of this object at {@link MapManager} maps list */
    public int index;

    /** Name of this map object */
    public String name = "";

    /** Current map state */
    public transient StateMachine<Map, MState> state;

    /** Animations logics */
    public Animations anim;

    /** Blocks in the map */
    public Block[][] b;

    /** Blocks falling (controlled by the player) */
    public PlayBlocks pb;

    /**
     * Abstract input method: a controller, touch screen, keyboard etc from which this map will be
     * controlled
     */
    public transient InputBase input;

    /** Reference to AI object. Null if is a real player's map */
    public transient AiBase ai;

    // ====== Block's logic =====>

    /**
     * Labels equivalence
     * @see #labelCalc()
     */
    private transient Array<IntSet> le;

    /**
     * Labels count: the number of blocks per label
     * @see #mergeEquivalentLabels()
     */
    private transient IntMap<Integer> lc;

    /**
     * If there is at least this number of blocks with the same {@link Block#label} (color group),
     * the blocks of this label will be deleted. 4 default.
     */
    public int deleteSize = 4;

    /** Number of blocks deleted */
    private int blocksDeleted = 0;

    /**
     * Max trash blocks that can be put in the map in one turn. The others will wait the next turns
     */
    public int maxTrashOnce = N_COL * 5;

    // <===== End of block's logic ======

    // ====== Score control variables =====>

    /** Score number from the current match */
    public int score;

    /** Score for all matches  played in sequence */
    public int scoreSum;

    /** Score string to show in the GUI */
    public String scoreStr;

    /** Blocks already scored in the chain **/
    public int scoredBlocks;

    public int chainPowerCount;

    /** Each index represents a color, colors set as true will be counted to color bonus */
    public boolean[] colorBonusArr;

    public int groupBonus;

    // <===== End of score control variables ======

    // ====== State control variables =====>

    /** True if game is paused */
    public boolean pause = false;

    /** True if the game is over */
    public boolean gameOver = false;

    /** True if the game was won */
    public boolean gameWin = false;

    /** If true the blocks reach the top and this game is lost */
    public boolean lost = false;

    /** True if any block has changed it's position then label calc needs to be redone */
    public boolean blockChanged = false;

    /** Number of trash blocks to add when it's {@link #trashBlocksTurn} */
    private int trashBlocksToAdd = 0;

    /**
     * {@link #trashBlocksToAdd} has a limit in per turn: {@link #maxTrashOnce}. When this limit is
     * reached the player will have one turn then more trash blocks can be added
     */
    public boolean trashBlocksTurn = true;

    // <===== End of state control variables ======

    // ====== Default times, timers and other speed control variables =====>

    /** Match time. Only non paused time is computed */
    public float matchTimer = 0f;

    /** Time at the current speed */
    public float speedTimer = 0f;

    /** Pairs of change time and the new vertical moveTime (less is faster then more speed) */
    public float[] speedArr;

    /** Current index of the {@link #speedArr} */
    public int speedIndex = 0;

    /**
     * Multiplier (times faster) for {@link PlayBlocks PlayBlock's} speed when the player press
     * the down key of the controller/input
     */
    public float vPlayMoveMultip = 8f;

    /** Acceleration of the free fall blocks in rows per second squared: 80 default */
    public float freeFallAcceleration = 80f;

    /** Used to make each column of blocks fall at random speed (for better visual) */
    public float colAcceleration[];

    /**
     * Default time to wait before insert the play blocks. The player can use this time to do his
     * last moves
     */
    public float beforeInsertWait = 0.2f;

    /**
     * Default time to wait before changing from the {@link Map.MState#FREE_FALL FREE_FALL} state to
     * the next state
     */
    public float afterFreeFallWait = 0.23f;

    /**
     * Remaining time to wait before changing from the {@link Map.MState#FREE_FALL FREE_FALL} state
     * to the next state
     */
    public float afterFreeFallTimer = afterFreeFallWait;

    /** Default time to wait before current {@link PlayBlocks} starts to fall */
    public float beforePlayFallWait = 0.025f;

    /** Default time to wait before next {@link PlayBlocks} rotation */
    public float rPlayMoveWait = 0.1f;

    /** Remaining time to wait before next {@link PlayBlocks} rotation */
    public float rPlayMoveTimer = 0f;

    /**
     * Default time to wait before next {@link PlayBlocks} horizontal move (when player press the
     * right/left arrows in the controller/input)
     */
    public float hPlayMoveWait = 0.1f;

    /** Remaining time to wait before next {@link PlayBlocks} horizontal move */
    public float hPlayMoveTimer = 0f;

    /**
     * Default time to {@link PlayBlocks} fall 1 row. In seconds (less is fester). This field sets
     * the PlayBlocks default vertical speed. It remains unchanged when the player press the down
     * key of the controller/input.
     * Slow 0.5f; Normal 0.34; fast 0.16
     */
    public float vPlayMoveWait = 0.5f;

    /**
     * Default time to {@link PlayBlocks} fall 1 row. In seconds (less is fester). This field sets
     * the PlayBlocks default vertical speed. It changes when the player press/release the down key
     * of the controller/input to the block fall faster or go back to the default speed
     */
    public float vPlayMoveWait2 = vPlayMoveWait;

    /** Remaining time to the {@link PlayBlocks} falls to the next row */
    public float vPlayMoveTimer = vPlayMoveWait;

    /** Default time to wait before delete the block */
    public float deleteWait = 0.5f;

    /** A third of {@link #deleteWait} */
    public float delWait3 = deleteWait / 3f;

    // <===== End of default times, timers and other speed control variables ======

    public Map(MapManager manager) {

        this.manager = manager;

        state = new DefaultStateMachine<>(this, MState.FREE_FALL);
        anim = new Animations(this);
        b = new Block[N_COL][OUT_ROW + N_ROW];
        pb = new PlayBlocks(this);
        le = new Array<>();
        lc = new IntMap<>();

        colorBonusArr = new boolean[Block.CLR_N];
        scoreStr = "0";

        colAcceleration = new float[N_COL];

        // Fill map with empty blocks
        for(int i = 0; i < b.length; i++) {
            for(int j = 0; j < b[i].length; j++) {
                b[i][j] = new Block(this);
            }
        }
    }

    public void recycle() {

        for(int i = 0; i < b.length; i++) {
            for(int j = 0; j < b[i].length; j++) {
                b[i][j].recycle();
            }
        }

        // Todo fix trash blocks still been added on a new match
        // Todo fix blocks starts falling first in a map then in the other
        trashBlocksToAdd = 0;
        gameOver = false;
        gameWin = false;
        score = 0;
        scoreStr = "0";
        speedIndex = 0;
        matchTimer = 0f;
        speedTimer = 0f;
        vPlayMoveWait = speedArr[1];
        vPlayMoveWait2 = speedArr[1];
        vPlayMoveTimer = speedArr[1];
        pb.recycle();
        pb.init();
    }

    @Override
    public void setInput(InputBase input) {
        this.input = input;
    }

    public void setCfg(Cfg.Map cfg) {
        speedArr = cfg.moveTime;

        vPlayMoveWait = speedArr[1];
        vPlayMoveWait2 = speedArr[1];
        vPlayMoveTimer = speedArr[1];
    }

    public boolean isEmpty(int col, int row) {
        return col >= 0 && col < N_COL && row < N_ROW + OUT_ROW && b[col][row].isEmpty();
    }

    /**
     * Perform the calculation of each block moving down the blocks that need to fall i.e. the ones
     * that have empty space below it
     */
    private void freeFallCalc() {

        // Loop through the columns 0(left) to 6(right)
        for(int col = 0; col < b.length; col++) {

            // Number of empty rows blocks in this column
            int nEmpty = 0;

            // Loop through the rows 14 + OUT_ROW (floor) to 0 (top) of the actual
            // column. Keep looking from floor to top and counting empty blocks
            // then move down non empty the number that has been counted
            for(int row = b[col].length -1; row >= 0; row--) {
                // If it`s not empty then swap: the block goes down
                // and the empty goes up
                if(!b[col][row].isEmpty()) {

                    if(nEmpty > 0) {

                        b[col][row].setFreeFallTrajectory(row, row + nEmpty);

                        Block swap = b[col][row + nEmpty];
                        b[col][row + nEmpty] = b[col][row];
                        b[col][row] = swap;

                        b[col][row + nEmpty].tile = 0;
                        fixSideLinks(col, row);
                        blockChanged = true;
                    }
                }
                // If it`s empty increment
                else nEmpty++;
            }
        }
    }

    /**
     * Label blocks based on it's color and neighborhood. Each group of blocks with the same color
     * will have a unique label number. It's is based on a image processing algorithm to detect
     * objects
     */
    private void labelCalc() {

        recycleLabelEquivalence();

        int label = 1;

        // cols 0 -> 6
        for(int col = 0; col < b.length; col++) {

            // row 0 -> 14 + OUT_ROW
            for(int row = 0; row < b[col].length; row++) {

                // Empty blocks and trash blocks don`t group at first:
                // put a zero label
                if(!b[col][row].isColor()) {

                    b[col][row].label = 0;
                    continue;
                }

                boolean sameColorLeft = false;
                boolean sameColorUpper = false;
                int left = col -1;
                int upper = row -1;

                // If it has the same color of the left block
                // then mark with the same label
                if(left >= 0 && b[left][row].intColor == b[col][row].intColor) {
                    b[col][row].label = b[left][row].label;
                    sameColorLeft = true;
                }

                // If it has the same color of the upper block
                if(upper >= 0 && b[col][upper].intColor == b[col][row].intColor) {

                    sameColorUpper = true;

                    // If it has the same color of the left block add a label
                    // equivalence
                    if(sameColorLeft) {
                        addLabelEquivalence(b[col][row].label, b[col][upper].label);
                    }

                    // copy the label of the top block
                    b[col][row].label = b[col][upper].label;
                }

                // If it has not the same color of the left and de upper one,
                // then put a new label
                if(!sameColorLeft && !sameColorUpper) {
                    b[col][row].label = label;
                    label++;
                }
            }
        }
        mergeEquivalentLabels();
        labelDelete();
    }

    /** Adding to {@link #le} two labels that are equivalent */
    private void addLabelEquivalence(int labelA, int labelB) {

        // If they are equal they not need to be marked
        // as equivalent
        if(labelA == labelB) return;

        // try to find a set that contains A or B label
        for(IntSet s : le) {
            // If contains A or B, add A and B
            if(s.contains(labelA) || s.contains(labelB)) {
                s.addAll(labelA, labelB);
                return;
            }
        }

        // Try to find a empty set where A and B labels
        // can be added
        for(IntSet s : le) {
            if(s.size == 0) {
                s.addAll(labelA, labelB);
                return;
            }
        }

        // Add a new set with A and B labels
        IntSet s = new IntSet();
        s.addAll(labelA, labelB);
        le.add(s);
    }

    /**
     * Relabel using the label equivalence: uses the first label in the set to all equivalent labels
     * and increment {@link #lc}: the number of blocks per label
     */
    private void mergeEquivalentLabels() {

        for(int i = 0; i < b.length; i++) {

            for(int j = 0; j < b[i].length; j++) {

                if(b[i][j].label == 0) continue;

                for(IntSet s : le) {

                    if(s.contains(b[i][j].label)) {

                        b[i][j].label = s.first();
                    }
                }

                // Updates the number of blocks with the same
                // label (which are the same color and form a group)
                int v = lc.get(b[i][j].label, 0);
                lc.put(b[i][j].label, ++v);
            }
        }
    }

    /**
     * This method is called to search and delete blocks groups that big enough (labels that has the
     * number of blocks equals or grater then {@link #deleteSize}).
     */
    private void labelDelete() {

        for(int i = 0; i < b.length; i++) {

            for(int j = 0; j < b[i].length; j++) {

                if(b[i][j].label == 0) continue;

                if(lc.get(b[i][j].label) >= deleteSize) {

                    // Color bonus: Marking the colors that will be deleted.
                    colorBonusArr[b[i][j].intColor -1] = true;

                    b[i][j].toDelete = deleteWait;
                    blocksDeleted++;

                    // Check the 4 adjacent blocks: delete those that are trash blocks
                    if(i -1 >= 0 && b[i -1][j].isTrash())
                        b[i -1][j].toDelete = deleteWait;

                    if(i +1 < b.length && b[i +1][j].isTrash())
                        b[i +1][j].toDelete = deleteWait;

                    if(j -1 >= 0 && b[i][j -1].isTrash())
                        b[i][j -1].toDelete = deleteWait;

                    if(j +1 < b[i].length && b[i][j +1].isTrash())
                        b[i][j +1].toDelete = deleteWait;
                }
            }
        }
    }

    /** Clear the sets but not lose reference to reuse them and avoid garbage collection */
    private void recycleLabelEquivalence() {

        for(IntSet s : le) {
            s.clear();
        }
        lc.clear();
    }

    private void mapLinks() {

        for(int i = 0; i < b.length; i++) {
            for(int j = 0; j < b[i].length; j++) {
                blockLinks(i, j);
            }
        }
    }

    /** Updates the links with the block's 4 neighbours */
    private void blockLinks(int col, int row) {

        Block block = b[col][row];

        if(block.isEmpty() || block.isTrash()) return;

        int color = block.intColor;
        int tile = 0;

        // Check the 4 adjacent blocks if they are linked to center one
        // Up
        if(row - 1 >= 0 && b[col][row - 1].intColor == color)
            tile += 1000;
        // Right
        if(col + 1 < b.length && b[col + 1][row].intColor == color)
            tile += 100;
        // Down
        if(row + 1 < b[col].length && b[col][row + 1].intColor == color)
            tile += 10;
        // Left
        if(col - 1 >= 0 && b[col - 1][row].intColor == color)
            tile += 1;

        block.tile = tile;
    }

    /** Removes the side links with the current block because it will start to fall */
    private void fixSideLinks(int col, int row) {
        // Right block, left link
        if(col + 1 < b.length && b[col + 1][row].tile % 10 == 1)
            b[col + 1][row].tile -= 1;
        // Left block, right link
        if(col - 1 >= 0 && b[col - 1][row].tile / 100 % 10 == 1)
            b[col - 1][row].tile -= 100;
    }

    /**
     * Example in test6 from tests.Test class. Other resources:
     * https://puyonexus.com/wiki/Scoring
     * https://www.youtube.com/watch?v=a-1b3IA2ujA
     * http://www.cheatcc.com/xb/sg/dr_robotniks_mean_bean_machine.txt
     */
    private void scoreCalc() {

        // Todo score increment while blocks fall fast (down key is pressed)

        int block = blocksDeleted - scoredBlocks;
        scoredBlocks = blocksDeleted;

        int colorBonusCount = 0;
        for(int i = 0; i < colorBonusArr.length; i++) {
            if(colorBonusArr[i]) colorBonusCount++;
        }

        int a = block * 10;
        int b = + Map.getChainPower(chainPowerCount)
                + Map.getColorBonus(colorBonusCount)
                + groupBonus;

        if(b < 1) b = 1;

        int ab = a * b;

        // #debugCode
        if(ab > 0) Dbg.inf(Dbg.tag(this), "Score = " + score);

        score += ab;

        if(a > 0 && b > 1) scoreStr = "+ " + a + " x " + b;
        else if(ab > 0)    scoreStr = "+ " + a;
    }

    private void groupBonusCalc() {

        for(IntMap.Entry<Integer> entry : lc.entries()) {

            // Group bonus: add to groupBonus the value of group bonus table
            if (entry.value > 4) groupBonus += Map.getGroupBonus(entry.value);
        }
    }

    private void cleanScoreCalc() {

        groupBonus = 0;
        for(int i = 0; i < colorBonusArr.length; i++) {
            colorBonusArr[i] = false;
        }
    }

    /** Throw trash blocks in the opponent map */
    private void throwTrashBlocks() {

        if(blocksDeleted <= 0) return;

        Map opp = manager.getOpponent(index);

        if(opp != null) {
            float trashBlocks = ((blocksDeleted * 10f) * (blocksDeleted - 3f)) / 70f;
            opp.trashBlocksToAdd += Math.round(trashBlocks);
        }

        blocksDeleted = 0;
    }

    /** Add trash blocks (thrown by the opponent) in this map */
    private void addTrashBlocks() {

        if(trashBlocksToAdd <= 0) return;

        int toAdd = Math.min(trashBlocksToAdd, maxTrashOnce);
        trashBlocksToAdd -= toAdd;

        // row OUT_ROW -1 -> 0
        for(int row = OUT_ROW -1; row >= 0; row--) {

            toAdd = addTrashBlocksRow(row, toAdd);
            if(toAdd <= 0) return;
        }
    }

    private int addTrashBlocksRow(int rowIndex, int nTrashBlocks) {

        int randSeed = MathUtils.random(0, N_COL -1);

        for(int col = 0; col < N_COL; col++) {

            if(nTrashBlocks <= 0) break;

            int randIndex = (randSeed + col) % N_COL;

            if(!b[randIndex][rowIndex].isTrash()) {

                b[randIndex][rowIndex].setColor(Block.CLR_T);
                nTrashBlocks--;
            }
        }

        return nTrashBlocks;
    }

    /** Returns true if the {@link #state} is equals the argument state */
    public boolean isInState(Map.MState state) {
        return this.state.getCurrentState() == state;
    }

    /** Returns map current state */
    public Map.MState getState() {
        return this.state.getCurrentState();
    }

    private void gameOver() {

        if(!b[N_COL/2][OUT_ROW].isEmpty()) {
            gameOver = true;
        }
    }

    /** Control {@link PlayBlocks} speed that can change during tha game */
    private void timing() {

        // Updates vertical fall time (less is faster)
        if(speedIndex < speedArr.length && speedTimer >= speedArr[speedIndex]) {
            vPlayMoveWait = speedArr[speedIndex + 1];
            vPlayMoveWait2 = speedArr[speedIndex + 1];
            speedTimer = 0f;
            Dbg.dbg(Dbg.tag(this), "timing t = " + matchTimer + "; s = " + speedArr[speedIndex + 1]);
            speedIndex += 2;
        }
    }

    private void shuffleColAcceleration(float intensity) {

        float max = freeFallAcceleration * intensity;

        for(int i = 0; i < colAcceleration.length; i++) {
            colAcceleration[i] = MathUtils.random(max);
        }
    }

    public void update() {

        if(!pause) {
            matchTimer += G.delta;
            speedTimer += G.delta;
        }
        state.update();
        timing();
        if(ai != null) ai.update();
    }

    /**
     * Needs to be called before render when the map is loaded from a serialized source. This
     * because some references and objects are not serialized and it needs to be setup
     */
    public void deserialize(MapManager manager) {

        this.manager = manager;
        pb.deserialize(this);
        anim.deserialize(this);
        le = new Array<>();
        lc = new IntMap<>();

        if(pb.b1x != N_COL / 2 || pb.b1y != OUT_ROW -1 || pb.rotation != 0)
            state = new DefaultStateMachine<>(this, MState.PLAY_FALL);
        else
            state = new DefaultStateMachine<>(this, MState.FREE_FALL);

        for(int i = 0; i < b.length; i++) {
            for(int j = 0; j < b[i].length; j++) {
                b[i][j].deserialize(this);
            }
        }
    }

    /** Updates inputs and inputs and related animations */
    private void inputUpdate() {

        if(input != null) {

            input.update();

            int horizontal = input.getAxisX();
            int vertical   = input.getAxisY();
            int verticalOld = input.getAxisYOld();

            // ==== Rotation move timing control ====
            if(rPlayMoveTimer > 0f) {
                rPlayMoveTimer -= G.delta;
            }

            // ==== Horizontal move timing control ====
            if(hPlayMoveTimer > 0f) {
                hPlayMoveTimer -= G.delta;
            }
            if(horizontal != 0 && hPlayMoveTimer <= 0f) {
                pb.moveHorizontal(horizontal);
                hPlayMoveTimer += hPlayMoveWait;
            }

            // ==== Vertical move timing control ====
            if(vertical == 1) {
                vPlayMoveWait2 = vPlayMoveWait / vPlayMoveMultip;
            }
            else if(vertical == 0 && verticalOld == 1) {
                vPlayMoveWait2 = vPlayMoveWait;
                vPlayMoveTimer = vPlayMoveWait;
            }

            // Start pressing the down button right now
            if(verticalOld == 0 && vertical == 1) {
                // Do not wait to start falling faster
                vPlayMoveTimer = 0.0001f;
            }
        }
    }

    @Override
    public void button1(boolean isDown) {
        if(!pause && isInState(MState.PLAY_FALL)&& isDown && rPlayMoveTimer <= 0f)
            pb.rotateClockwise(true);
    }

    @Override
    public void button2(boolean isDown) {
        if(!pause && isInState(MState.PLAY_FALL) && isDown && rPlayMoveTimer <= 0f)
            pb.rotateClockwise(true);
    }

    @Override
    public void button3(boolean isDown) {
        if(!pause && isInState(MState.PLAY_FALL) && isDown && rPlayMoveTimer <= 0f)
            pb.rotateCounterclockwise(true);
    }

    @Override
    public void button4(boolean isDown) {
        if(!pause && isInState(MState.PLAY_FALL) && isDown && rPlayMoveTimer <= 0f)
            pb.rotateCounterclockwise(true);
    }

    @Override
    public void buttonStart(boolean isDown) {
        if(isDown && !isInState(MState.OVER) && !isInState(MState.DONE))
            manager.pause(index, !pause);
    }

    @Override
    public String toString() {
        return name;
    }

    public void debugShape(int shape) {

        // Nothing
        if(shape == 0) {}
        // Small combo
        if(shape == 1) {
            b[0][14 + OUT_ROW].setColor(1);
            b[0][13 + OUT_ROW].setColor(1);
            b[0][12 + OUT_ROW].setColor(1);
            b[0][11 + OUT_ROW].setColor(9);
            b[0][10 + OUT_ROW].setColor(9);
            b[0][9 + OUT_ROW].setColor(9);
            b[0][8 + OUT_ROW].setColor(9);

            b[1][14 + OUT_ROW].setColor(2);
            b[1][13 + OUT_ROW].setColor(2);
            b[1][12 + OUT_ROW].setColor(2);
            b[1][11 + OUT_ROW].setColor(1);
            b[1][10 + OUT_ROW].setColor(1);
            b[1][9 + OUT_ROW].setColor(1);
            b[1][8 + OUT_ROW].setColor(9);
        }
        // Combo
        else if(shape == 2) {
            b[0][14 + OUT_ROW].setColor(1);
            b[0][13 + OUT_ROW].setColor(1);
            b[0][12 + OUT_ROW].setColor(1);
            b[0][11 + OUT_ROW].setColor(9);
            b[0][10 + OUT_ROW].setColor(9);
            b[0][9 + OUT_ROW].setColor(9);
            b[0][8 + OUT_ROW].setColor(9);

            b[1][14 + OUT_ROW].setColor(2);
            b[1][13 + OUT_ROW].setColor(2);
            b[1][12 + OUT_ROW].setColor(2);
            b[1][11 + OUT_ROW].setColor(1);
            b[1][10 + OUT_ROW].setColor(1);
            b[1][9 + OUT_ROW].setColor(1);
            b[1][8 + OUT_ROW].setColor(9);

            b[2][14 + OUT_ROW].setColor(3);
            b[2][13 + OUT_ROW].setColor(3);
            b[2][12 + OUT_ROW].setColor(3);
            b[2][11 + OUT_ROW].setColor(2);
            b[2][10 + OUT_ROW].setColor(2);
            b[2][9 + OUT_ROW].setColor(2);
            b[2][8 + OUT_ROW].setColor(9);

            b[3][14 + OUT_ROW].setColor(4);
            b[3][13 + OUT_ROW].setColor(4);
            b[3][12 + OUT_ROW].setColor(4);
            b[3][11 + OUT_ROW].setColor(3);
            b[3][10 + OUT_ROW].setColor(3);
            b[3][9 + OUT_ROW].setColor(3);
            b[3][8 + OUT_ROW].setColor(9);
        }
        // Big combo
        else if(shape == 3) {
            b[0][14 + OUT_ROW].setColor(1);
            b[0][13 + OUT_ROW].setColor(1);
            b[0][12 + OUT_ROW].setColor(1);
            b[0][11 + OUT_ROW].setColor(9);
            b[0][10 + OUT_ROW].setColor(9);
            b[0][9 + OUT_ROW].setColor(9);
            b[0][8 + OUT_ROW].setColor(9);

            b[1][14 + OUT_ROW].setColor(2);
            b[1][13 + OUT_ROW].setColor(2);
            b[1][12 + OUT_ROW].setColor(2);
            b[1][11 + OUT_ROW].setColor(1);
            b[1][10 + OUT_ROW].setColor(1);
            b[1][9 + OUT_ROW].setColor(1);
            b[1][8 + OUT_ROW].setColor(9);

            b[2][14 + OUT_ROW].setColor(3);
            b[2][13 + OUT_ROW].setColor(3);
            b[2][12 + OUT_ROW].setColor(3);
            b[2][11 + OUT_ROW].setColor(2);
            b[2][10 + OUT_ROW].setColor(2);
            b[2][9 + OUT_ROW].setColor(2);
            b[2][8 + OUT_ROW].setColor(9);

            b[3][14 + OUT_ROW].setColor(4);
            b[3][13 + OUT_ROW].setColor(4);
            b[3][12 + OUT_ROW].setColor(4);
            b[3][11 + OUT_ROW].setColor(3);
            b[3][10 + OUT_ROW].setColor(3);
            b[3][9 + OUT_ROW].setColor(3);
            b[3][8 + OUT_ROW].setColor(9);

            b[4][14 + OUT_ROW].setColor(5);
            b[4][13 + OUT_ROW].setColor(5);
            b[4][12 + OUT_ROW].setColor(5);
            b[4][11 + OUT_ROW].setColor(4);
            b[4][10 + OUT_ROW].setColor(4);
            b[4][9 + OUT_ROW].setColor(4);
            b[4][8 + OUT_ROW].setColor(9);
        }
        // Stair: 2 block per step
        else if(shape == 4) {
            b[0][14 + OUT_ROW].setColor(1);
            b[1][14 + OUT_ROW].setColor(2);
            b[2][14 + OUT_ROW].setColor(3);
            b[3][14 + OUT_ROW].setColor(4);
            b[4][14 + OUT_ROW].setColor(5);

            b[0][13 + OUT_ROW].setColor(2);
            b[1][13 + OUT_ROW].setColor(3);
            b[2][13 + OUT_ROW].setColor(4);

            b[0][12 + OUT_ROW].setColor(3);
        }
        // Stair: 2 block per step
        else if(shape == 5) {
            b[0][14 + OUT_ROW].setColor(1);
            b[1][14 + OUT_ROW].setColor(2);
            b[2][14 + OUT_ROW].setColor(3);
            b[3][14 + OUT_ROW].setColor(4);
            b[4][14 + OUT_ROW].setColor(5);
            b[5][14 + OUT_ROW].setColor(1);

            b[0][13 + OUT_ROW].setColor(2);
            b[1][13 + OUT_ROW].setColor(3);
            b[2][13 + OUT_ROW].setColor(4);
            b[3][13 + OUT_ROW].setColor(5);
            b[4][13 + OUT_ROW].setColor(1);

            b[0][12 + OUT_ROW].setColor(3);
            b[1][12 + OUT_ROW].setColor(4);
            b[2][12 + OUT_ROW].setColor(5);
            b[3][12 + OUT_ROW].setColor(1);

            b[0][11 + OUT_ROW].setColor(4);
            b[1][11 + OUT_ROW].setColor(5);
            b[2][11 + OUT_ROW].setColor(1);
        }
        // Only 3 rows free at the top
        else if(shape == 6) {
            for(int col = 0; col < b.length; col++) {
                for(int row = OUT_ROW + 3; row < b[col].length; row++) {
                    b[col][row].setColor(Block.CLR_T);
                }
            }
        }
        // Wall (filled column) to block area in the map
        else if(shape == 7) {
            for(int row = 0; row < b[2].length; row++) {
                b[2][row].setColor(Block.CLR_T);
            }
            b[0][14 + OUT_ROW].setColor(1);
            b[0][13 + OUT_ROW].setColor(1);
            b[0][12 + OUT_ROW].setColor(1);

        }
        // 2 Walls (filled column) to block area in the map
        else if(shape == 8) {
            for(int row = 0; row < b[2].length; row++) {
                b[2][row].setColor(Block.CLR_T);
            }
            for(int row = 0; row < b[5].length; row++) {
                b[5][row].setColor(Block.CLR_T);
            }
            b[0][14 + OUT_ROW].setColor(1);
            b[0][13 + OUT_ROW].setColor(1);
            b[0][12 + OUT_ROW].setColor(1);
            b[1][14 + OUT_ROW].setColor(2);
            b[1][13 + OUT_ROW].setColor(2);
            b[1][12 + OUT_ROW].setColor(2);
            b[6][14 + OUT_ROW].setColor(3);
            b[6][13 + OUT_ROW].setColor(3);
            b[6][12 + OUT_ROW].setColor(3);
        }
    }

    public void print() {

        // row 0 -> 14 + OUT_ROW
        for(int row = 0; row < b[0].length; row++) {

            // col 0 -> 6
            for(int col = 0; col < b.length; col++) {

                System.out.print(b[col][row].intColor + " ");
            }
            Dbg.print("");
        }
    }

    public void printLabel() {

        Dbg.print("Label print ->");

        // row 0 -> 14 + OUT_ROW
        for(int row = 0; row < b[0].length; row++) {

            // col 0 -> 6
            for(int col = 0; col < b.length; col++) {

                System.out.printf(" %5d", b[col][row].label);
            }
            Dbg.print("");
        }

        // Prints labels count
        for(IntMap.Entry<Integer> entry : lc.entries())
        {
            System.out.print(entry.key + ":" + entry.value + "; ");
        }

        Dbg.print("");
    }
}