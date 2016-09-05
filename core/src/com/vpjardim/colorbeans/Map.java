/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans;

import com.badlogic.gdx.Gdx;
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
import com.vpjardim.colorbeans.core.MapManager;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.TargetBase;

/**
 * Represents a field/map where one player can do it`s actions.
 * The goal it's to group colors blocks ({@link Block}) until they
 * reach at least 4, for example. This number can be set in
 * {@link GameProperties#deleteSize}. Once this group is formed the
 * color beans/blocks will be deleted to make space for the falling
 * ones. If there isn't enough space for other blocks (the map is
 * full/the falling blocks are obstructed) it's game over.
 * <pre>
 *
 * Examples of color group (uses the 4 neighborhood):
 * Obs.: blue = 1, red = 2; deleteSize = 4
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
 * The game can have one or more maps depending on the number
 * of players. One player can interfere on others players map
 * by doing combos that will send trash blocks to the others map.
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
 * On the Game the matrix has kind of a rotation for render and
 * logics: make the column a row (top becomes left, bottom becomes
 * right), then make the next column as the next row and so on.
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
     * A finite state machine to track the
     * main states of the map and perform actions
     * required by each state
     */
    public enum MState implements State<Map> {

        FREE_FALL() {

            @Override
            public void enter(Map map) {
                // #debugCode
                Gdx.app.debug(map.getClass().getSimpleName() + "#" + map.toString(), "state = FREE_FALL");

                map.shuffleColAcceleration();
                map.prop.afterFreeFallTime = map.prop.afterFreeFallWait;
                map.freeFallCalc();
                map.anim.freeFall();
            }

            @Override
            public void update(Map map) {

                // Case the player of this map won the game
                if(map.prop.gameWin) {
                    map.state.changeState(MState.DONE);
                    return;
                }

                // Waiting til next frame if the game is paused
                if(map.prop.pause) return;

                // Waiting animation to end
                boolean freeFallAnim = map.anim.freeFall();
                map.anim.deform();

                if(freeFallAnim) return;

                map.prop.afterFreeFallTime -= G.delta;

                // Wait some time before change state
                if(map.prop.afterFreeFallTime > 0f) return;

                // If a block change it's position the labels
                // needs to be recalculated
                if(map.blockFall || map.blockInsert) {
                    map.state.changeState(MState.LABEL_CALC);
                    map.blockFall = false;
                    map.blockInsert = false;
                }
                // If not see if is time to add trash blocks
                else if(map.trashBlocksToAdd > 0 && map.trashBlocksTurn) {
                    map.state.changeState(MState.TRASH_ADD);
                }
                // If not change to play fall state
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
                Gdx.app.debug(map.getClass().getSimpleName() + "#" + map.toString(), "state = LABEL_CALC");

                map.labelCalc();
                map.groupBonusCalc();
                // Chain power: incrementing chain power (combo level).
                map.chainPowerCount++;
                map.scoreCalc();
                map.anim.labelDelete();
            }

            @Override
            public void update(Map map) {

                // Case the player of this map won the game
                if(map.prop.gameWin) {
                    map.state.changeState(MState.DONE);
                    return;
                }

                // Waiting til next frame if the game is paused
                if(map.prop.pause) return;

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
                Gdx.app.debug(map.getClass().getSimpleName() + "#" + map.toString(), "state = PLAY_FALL");

                map.chainPowerCount = 0;
                map.scoredBlocks = 0;

                map.gameOver();

                if(map.prop.gameOver) {
                    map.state.changeState(MState.OVER);
                    return;
                }

                map.prop.vPlayMoveWait = map.prop.beforePlayFallWait;
                map.prop.hPlayMoveWait = map.prop.beforePlayFallWait;
                map.prop.rPlayMoveWait = map.prop.beforePlayFallWait;

                map.trashBlocksTurn = true;
                map.throwTrashBlocks();

                // processing input and input animation
                map.inputUpdate();
            }

            @Override
            public void update(Map map) {

                // Case the player of this map won the game
                if(map.prop.gameWin) {
                    map.state.changeState(MState.DONE);
                    return;
                }

                // Waiting til next frame if the game is paused
                if(map.prop.pause) return;

                // processing input and input animation
                map.inputUpdate();

                if(map.blockInsert) {
                    map.state.changeState(MState.FREE_FALL);
                }
            }

            @Override
            public void exit(Map map) {
                // #debugCode
                //AiMap aiMap = new AiMap();
                //aiMap.init(map.b, map.prop.deleteSize, map.OUT_ROW);
                //aiMap.iterate(0, 0, 0, 0);
            }
        },

        TRASH_ADD() {

            @Override
            public void enter(Map map) {
                // #debugCode
                Gdx.app.debug(map.getClass().getSimpleName() + "#" + map.toString(), "state = TRASH_ADD");

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
                Gdx.app.debug(map.getClass().getSimpleName() + "#" + map.toString(), "state = OVER");
                System.out.println(map + " over");

                map.shuffleColAcceleration(0.8f);
                map.colAcceleration[map.b.length/2] = map.prop.freeFallAcceleration * 0.8f;
                map.anim.gameOver();
            }

            @Override
            public void update(Map map) {

                // Waiting animation to end
                if(map.anim.gameOver()) return;

                map.state.changeState(MState.DONE);
            }

            @Override
            public void exit(Map map) { map.recycle(); }
        },

        DONE() {

            @Override
            public void enter(Map map) {
                // #debugCode
                Gdx.app.debug(map.getClass().getSimpleName() + "#" + map.toString(), "state = DONE");
                if(map.prop.gameWin) {
                    System.out.println(map + " win\n");
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

    /**
     * Basic draw proprieties
     */
    public static class GameProperties {

        /** Match time */
        public float matchTime = 0f;

        /** Last update of timeMetrics */
        public float timeMetricsUpdate = matchTime;

        /** True if game is paused */
        public boolean pause = false;

        /** True if the game is over */
        public boolean gameOver = false;

        /** True if the game was won */
        public boolean gameWin = false;

        /** If true the blocks reach the top and this game is lost */
        public boolean lost = false;

        /**
         * Wait some time before insert the play blocks.
         * The player can use this time to do his last moves
         */
        public float beforeInsertWait = 0.2f;

        public float afterFreeFallWait = 0.23f;

        public float afterFreeFallTime = afterFreeFallWait;

        /** Wait some time before current play blocks starts to fall */
        public float beforePlayFallWait = 0.025f;

        /** Rotation default time to wait */
        public float rPlayMoveTime = 0.1f;

        /** Rotation remaining time to wait */
        public float rPlayMoveWait = 0f;

        /**
         * Horizontal move time (when player press the horizontal
         * arrows in the controller/input)
         */
        public float hPlayMoveTime = 0.1f;

        public float hPlayMoveWait = 0f;

        /**
         * Default fall time of a play block.
         * Slow 0.5f; Normal 0.34; fast 0.16
         */
        public float vPlayMoveTimeDef = 0.6f;

        /**
         * The multiplier (times faster) for a play block fall
         * used when a player press the down key of the controller/input
         */
        public float vPlayMoveMultip = 10f;

        /**
         * Fall time of a play block (one floor): less is faster.
         * It changes when a player press the down key of the
         * controller/input to the block fall faster
         */
        public float vPlayMoveTime = vPlayMoveTimeDef;

        /** Remaining time to a play block fall to the next floor */
        public float vPlayMoveWait = vPlayMoveTimeDef;

        /**
         * Acceleration of the free fall blocks in side[s] per
         * second squared: 80 default
         */
        public float freeFallAcceleration = 80f;

        /** Time to wait before delete de block */
        public float deleteTime = 0.5f;

        /** A third of deleteTime */
        public float delTime3 = deleteTime / 3f;

        /**
         * Max trash blocks that can be put in the map
         * in one turn. The others will wait the next turns
         */
        public int maxTrashOnce = N_COL * 5;

        /**
         * If there is at least {@link #deleteSize} blocks with the same
         * label (color group) the blocks of this label will be deleted.
         * 4 default.
         */
        public int deleteSize = 4;
    }

    /** Map width [number of block columns]: 7 default */
    public static final int N_COL = 7;

    /** Map height [number of block rows]: 15 default */
    public static final int N_ROW = 15;

    /**
     * Out of screen rows: space used to place trash
     * blocks and avoid collisions at the top. It`s
     * a kind of back stage that is not shown to
     * the player. 10 default
     */
    public static final int OUT_ROW = 10;

    /** Chain power from 1 to 24+. Ref: https://puyonexus.com/wiki/Chain_Power_Table */
    public static final int[] chainPowerTable = {

            0, 8, 16, 32, 64, 128, 256, 512, 999 // Puyo Puyo

            // 0, 8, 16, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, 480,
            // 512, 544, 576, 608, 640, 672      // Puyo Puyo Tsu
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


    /** Reference holding the screen object */
    public transient MapManager manager;

    public int index;

    /** Blocks in the map */
    public Block[][] b;

    /** Blocks falling (controlled by the player) */
    public PlayBlocks pb;

    /**
     * Abstract input method: a controller, touch screen,
     * keyboard etc from which this map will be controlled
     */
    public transient InputBase input;

    public transient AiBase ai;

    /** Current map state */
    public transient StateMachine<Map, MState> state;

    // ====== Score control variables =====>

    /** Score for all matches played in sequence */
    public int scoreSum;

    /** Score number from the current match */
    public int score;

    /** Score string to show in the GUI */
    public String scoreStr;

    /** Blocks already scored in the chain **/
    public int scoredBlocks;

    public int chainPowerCount;

    /** Each index represents a color, colors set as true will be counted to color bonus */
    public boolean[] colorBonusArr;

    public int groupBonus;

    // <===== End score control variables ======

    // ====== State control variables =====>
    public boolean blockFall = false;
    public boolean blockInsert = false;

    /** Number of trash blocks to add when it's {@link #trashBlocksTurn} */
    private int trashBlocksToAdd = 0;

    /**
     * {@link #trashBlocksToAdd} has a limit in one turn:
     * {@link GameProperties#maxTrashOnce}. When this limit is
     * reached the player will have one turn then more trash
     * blocks can be added
     */
    public boolean trashBlocksTurn = true;
    // <===== End state control variables ======


    /** Animations logics */
    public Animations anim;

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

    /** Number of blocks deleted */
    private int blocksDeleted = 0;

    /** Proprieties */
    public GameProperties prop;

    /** Name of this map instance */
    public String name = "";

    public float colAcceleration[];

    public Map(MapManager manager) {

        this.manager = manager;

        prop = new GameProperties();
        pb   = new PlayBlocks(this);
        anim = new Animations(this);
        le   = new Array<>();
        lc   = new IntMap<>();

        state = new DefaultStateMachine<>(this, MState.FREE_FALL);

        colorBonusArr = new boolean[Block.CLR_N];
        scoreStr = "0";

        // Creating space
        b = new Block[N_COL][OUT_ROW + N_ROW];
        colAcceleration = new float[N_COL];

        // With empty blocks
        for(int i = 0; i < b.length; i++) {

            for(int j = 0; j < b[i].length; j++) {

                b[i][j] = new Block(this);
            }
        }
    }

    @Override
    public void setInput(InputBase input) {
        this.input = input;
    }

    public void print() {

        // row 0 -> 14 + OUT_ROW
        for(int row = 0; row < b[0].length; row++) {

            // col 0 -> 6
            for(int col = 0; col < b.length; col++) {

                System.out.print(b[col][row].intColor + " ");
            }
            System.out.println();
        }
    }

    public void printLabel() {

        System.out.println("Label print ->");

        // row 0 -> 14 + OUT_ROW
        for(int row = 0; row < b[0].length; row++) {

            // col 0 -> 6
            for(int col = 0; col < b.length; col++) {

                System.out.printf(" %5d", b[col][row].label);
            }
            System.out.println();
        }

        // Prints labels count
        for(IntMap.Entry<Integer> entry : lc.entries())
        {
            System.out.print(entry.key + ":" + entry.value + "; ");
        }

        System.out.println();
    }

    public boolean isEmpty(int col, int row) {

        return col >= 0 && col < N_COL && row < N_ROW + OUT_ROW && b[col][row].isEmpty();
    }

    private void freeFallCalc() {

        // Loop through the columns 0(left) to 6(right)
        for(int col = 0; col < b.length; col++) {

            // Number of empty rows blocks in this column
            int nEmpty = 0;

            // Loop through the rows 14 + OUT_ROW (floor) to 0 (top) of the actual
            // column. Keep looking from floor to top and counting empty blocks
            // then move down non empty the number that has been counted
            for(int row = b[col].length -1; row >= 0; row--)
            {
                // If it`s not empty then swap: the block goes down
                // and the empty goes up
                if(!b[col][row].isEmpty()) {

                    if(nEmpty > 0) {

                        b[col][row].setFreeFallTrajectory(row, row + nEmpty);

                        Block swap = b[col][row + nEmpty];
                        b[col][row + nEmpty] = b[col][row];
                        b[col][row] = swap;

                        blockFall = true;
                    }
                }
                // If it`s empty increment
                else {
                    nEmpty++;
                }
            }
        }
    }

    /**
     * Label blocks based on it's color and neighborhood.
     * Each group of blocks with the same color will have
     * a unique label number. It's is based on a image
     * processing algorithm to detect objects
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

    /**
     * Adding to {@link #le} two labels that are equivalent
     */
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
     * Relabel using the label equivalence:
     * uses the first label in the set to all equivalent labels
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
     * This method is called to search and delete blocks groups
     * that big enough (labels that has the number of blocks equals
     * or grater then {@link GameProperties#deleteSize}).
     */
    private void labelDelete() {

        for(int i = 0; i < b.length; i++) {

            for(int j = 0; j < b[i].length; j++) {

                if(b[i][j].label == 0) continue;

                if(lc.get(b[i][j].label) >= prop.deleteSize) {

                    // Color bonus: Marking the colors that will be deleted.
                    colorBonusArr[b[i][j].intColor -1] = true;

                    b[i][j].toDelete = prop.deleteTime;
                    blocksDeleted++;

                    // Check the 4 adjacent blocks: delete those that are trash blocks
                    if(i -1 >= 0 && b[i -1][j].isTrash())
                        b[i -1][j].toDelete = prop.deleteTime;

                    if(i +1 < b.length && b[i +1][j].isTrash())
                        b[i +1][j].toDelete = prop.deleteTime;

                    if(j -1 >= 0 && b[i][j -1].isTrash())
                        b[i][j -1].toDelete = prop.deleteTime;

                    if(j +1 < b[i].length && b[i][j +1].isTrash())
                        b[i][j +1].toDelete = prop.deleteTime;
                }
            }
        }
    }

    /**
     * Clear the sets but not lose reference
     * to reuse them and avoid garbage collection
     */
    private void recycleLabelEquivalence() {

        for(IntSet s : le) {
            s.clear();
        }
        lc.clear();
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
        if(ab > 0) {
            Gdx.app.log(this.getClass().getSimpleName() + "#" + this.toString(),
                    "Score = " + score);
        }

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

    /**
     * Throw trash blocks in the opponent map
     */
    private void throwTrashBlocks() {

        if(blocksDeleted <= 0) return;

        Map opp = manager.getOpponent(index);

        if(opp != null) {
            float trashBlocks = ((blocksDeleted * 10f) * (blocksDeleted - 3f)) / 70f;
            opp.trashBlocksToAdd += Math.round(trashBlocks);
        }

        blocksDeleted = 0;
    }

    /**
     * Add trash blocks (thrown by the opponent) in this map
     */
    private void addTrashBlocks() {

        if(trashBlocksToAdd <= 0) return;

        int toAdd = Math.min(trashBlocksToAdd, prop.maxTrashOnce);
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

    /** Returns true if the map.state is equals the argument state */
    public boolean isState(Map.MState state) {
        return this.state.getCurrentState().equals(state);
    }

    /** Returns map current state */
    public Map.MState getState() {
        return this.state.getCurrentState();
    }

    private void gameOver() {

        if(!b[N_COL/2][OUT_ROW].isEmpty()) {
            prop.gameOver = true;
        }
    }

    private void timeMetrics() {

        prop.matchTime += G.delta;
        float delta = prop.matchTime - prop.timeMetricsUpdate;

        // Updates vertical fall velocity each 30s, limited to 10 times
        if(delta >= 30f && prop.matchTime < 301f) {
            prop.timeMetricsUpdate = prop.matchTime;
            prop.vPlayMoveTimeDef *= 0.9f;
            prop.vPlayMoveTime    *= 0.9f;
        }
    }

    public void shuffleColAcceleration() {
        shuffleColAcceleration(0.3f);
    }

    public void shuffleColAcceleration(float intensity) {

        float max = prop.freeFallAcceleration * intensity;

        for(int i = 0; i < colAcceleration.length; i++) {
            colAcceleration[i] = MathUtils.random(max);
        }
    }

    public void recycle() {

        for(int i = 0; i < b.length; i++) {

            for(int j =  0; j < b[i].length; j++) {

                b[i][j].recycle();
            }
        }
        // Todo fix trash blocks still been added on a new match
        // Todo fix blocks starts falling first in a map then in the other
        trashBlocksToAdd = 0;
        prop.gameOver = false;
        prop.gameWin = false;
        pb.recycle();
        pb.init();

        // Todo when recycle time metrics does not return to the start point
    }

    public void update() {
        state.update();
        if(ai != null) ai.update();
    }

    /**
     * Needs to be called before render when the map is loaded from
     * a serialized source. This because some references and objects
     * are not serialized and it needs to be setup
     */
    public void deserialize(MapManager manager) {

        this.manager = manager;
        pb.deserialize(this);
        anim.deserialize(this);
        le = new Array<>();
        lc = new IntMap<>();

        if(pb.mCol != N_COL / 2 || pb.mRow != OUT_ROW -1 || pb.rotation != 0)
            state = new DefaultStateMachine<>(this, MState.PLAY_FALL);
        else
            state = new DefaultStateMachine<>(this, MState.FREE_FALL);

        for(int i = 0; i < b.length; i++) {
            for(int j = 0; j < b[i].length; j++) {
                b[i][j].deserialize(this);
            }
        }
    }

    /**
     * Updates inputs and inputs animations
     */
    private void inputUpdate() {

        if(input != null) {

            input.update();

            int horizontal = input.getAxisX();
            int vertical   = input.getAxisY();
            int verticalOld = input.getAxisYOld();

            // ==== Rotation move timing control ====
            if(prop.rPlayMoveWait > 0f) {
                prop.rPlayMoveWait -= G.delta;
            }

            // ==== Horizontal move timing control ====
            if(prop.hPlayMoveWait > 0f) {
                prop.hPlayMoveWait -= G.delta;
            }
            if(horizontal != 0 && prop.hPlayMoveWait <= 0f) {
                pb.moveHorizontal(horizontal);
                prop.hPlayMoveWait += prop.hPlayMoveTime;
            }

            // ==== Vertical move timing control ====
            if(vertical == 1) {
                prop.vPlayMoveTime = prop.vPlayMoveTimeDef / prop.vPlayMoveMultip;
            }
            else if(vertical == 0 && verticalOld == 1) {
                prop.vPlayMoveTime = prop.vPlayMoveTimeDef;
                prop.vPlayMoveWait = prop.vPlayMoveTimeDef;
            }

            // Start pressing the down button right now
            if(verticalOld == 0 && vertical == 1) {
                // Do not wait to start falling faster
                prop.vPlayMoveWait = 0.0001f;
            }
        }

        pb.playFallCalc();
        anim.playFall();
        anim.playHorizontal();
    }

    @Override
    public void button1(boolean isDown) {
        if(!prop.pause && isDown && prop.rPlayMoveWait <= 0f)
            pb.rotateClockwise(true);
    }

    @Override
    public void button2(boolean isDown) {
        if(!prop.pause && isDown && prop.rPlayMoveWait <= 0f)
            pb.rotateClockwise(true);
    }

    @Override
    public void button3(boolean isDown) {
        if(!prop.pause && isDown && prop.rPlayMoveWait <= 0f)
            pb.rotateCounterclockwise(true);
    }

    @Override
    public void button4(boolean isDown) {
        if(!prop.pause && isDown && prop.rPlayMoveWait <= 0f)
            pb.rotateCounterclockwise(true);
    }

    @Override
    public void buttonStart(boolean isDown) {
        if(isDown) prop.pause = !prop.pause;
    }

    @Override
    public String toString() {
        return name;
    }

    public void debugShape(int shape) {

        // Small combo
        if(shape == 0) {
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
        // Stair: 2 block per step
        else if(shape == 2) {
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
        else if(shape == 3) {
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
        // Only 2 rows free at the top
        else if(shape == 4) {
            for(int col = 0; col < b.length; col++) {
                for(int row = OUT_ROW + 2; row < b[col].length; row++) {
                    b[col][row].setColor(Block.CLR_T);
                }
            }
        }
        // Wall (filled column) to block area in the map
        else if(shape == 5) {
            for(int row = 0; row < b[2].length; row++) {
                b[2][row].setColor(Block.CLR_T);
            }
            b[0][14 + OUT_ROW].setColor(1);
            b[0][13 + OUT_ROW].setColor(1);
            b[0][12 + OUT_ROW].setColor(1);

        }
        // 2 Walls (filled column) to block area in the map
        else if(shape == 6) {
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
}