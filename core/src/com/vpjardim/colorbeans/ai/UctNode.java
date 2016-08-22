package com.vpjardim.colorbeans.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool;

import java.text.NumberFormat;

/**
 * @author Vinícius Jardim
 * 29/07/2016
 */
public class UctNode implements Pool.Poolable, DebugNode {

    // #debugCode
    public static int objCount = 0;

    public static NumberFormat fmt1 = NumberFormat.getInstance();
    public static NumberFormat fmt2 = NumberFormat.getInstance();

    static {
        fmt1.setMaximumFractionDigits(0);
        fmt2.setMaximumFractionDigits(2);
    }

    public static Pool<UctNode> pool = new Pool<UctNode>(8450) {
        @Override
        protected UctNode newObject() {
            return new UctNode();
        }
    };

    public static final UctNode ILLEGAL_NODE = new UctNode();

    static {
        ILLEGAL_NODE.totalValue = AiMap.MOVE_ILLEGAL;
    }

    public Uct uct;
    public AiMap aiMap;

    public int visits;
    public float value;
    public float totalValue;

    // #debugCode exploit and explore are debug fields, don't need to be stored
    public float exploit;
    public float explore;

    public Array<UctNode> children;
    public IntArray childMovesLeft;

    public int color1;
    public int color2;
    public int position;
    public int rotation;

    public UctNode() {
        objCount++;
        children = new Array<UctNode>(false, 325);
        childMovesLeft = new IntArray(false, 325);
        clear();
    }

    /** Old */
    public void iterate() {

        UctNode n = this;

        uct.visited.add(n);
        n = treePolice(n);
        n.defaultPolice();

        // Back propagation on the visited nodes
        for(int i = 0; i < uct.visited.size; i++) {
            uct.visited.get(i).updateStats(n.value);
        }

        uct.visited.clear();
    }

    /** New */
    public static UctNode treePolice(UctNode n) {

        while(!n.isTerminal()) {

            if(!n.isFullExpanded()) {
                n = n.expand();
                break;
            }
            else
                n = n.bestChild();

            n.uct.visited.add(n);
        }

        return n;
    }

    /** Add a child node to this (new) */
    public UctNode expand() {

        int rand = MathUtils.random(childMovesLeft.size -1);

        uct.moves.setMove(childMovesLeft.get(rand));
        childMovesLeft.removeIndex(rand);

        UctNode child = UctNode.pool.obtain();
        child.init(uct, this.aiMap, uct.moves.color1, uct.moves.color2, uct.moves.position, uct.moves.rotation);
        children.add(child);

        // Level 1 is the root (current state) which children (next state) has known colors blocks
        if(uct.visited.size == 1) {
            child.color1 = uct.color1;
            child.color2 = uct.color2;
        }

        return  child;
    }

    /** New */
    public UctNode bestChild() {

        // #debugCode
        if(children.size < 1) throw new UnsupportedOperationException("Should have at least 1 child");

        UctNode bestChild = null;
        float bestVal = Float.NEGATIVE_INFINITY;

        for(int i = 0; i < children.size; i++) {

            UctNode n = children.get(i);
            // Sum Uct.EPSILON to avoid zero division as this number will be used as a divisor
            float childVisits = n.visits + Uct.EPSILON1;

            n.exploit = n.totalValue / childVisits;
            n.explore = Uct.EXPLORE_CONST * (float)Math.sqrt(Math.log(visits + 1) / (childVisits));

            float currVal = n.exploit + n.explore;

            // Add a small random number to avoid ties
            currVal += MathUtils.random(Uct.EPSILON2);

            if(currVal > bestVal) {
                bestVal = currVal;
                bestChild = n;
            }
        }

        return bestChild;
    }

    /** Old */
    public void defaultPolice() {

        float result = aiMap.process(color1, color2, position, rotation);

        // If less the zero is a illegal or lost move
        if(result < 0f)
            value = result;
        else {
            value = uct.formula.calc(aiMap);
        }

        value /= 10f;
    }

    /** New */
    public void updateStats(double value) {
        visits++;
        totalValue += value;
    }

    /** New */
    public boolean isFullExpanded() {

        if(childMovesLeft.size == 0) {

            // As all child of this node has been added, we don't need AiMap object anymore
            // Optimisation commented cause #debugCode
            //if(aiMap != null) {
            //    AiMap.pool.free(aiMap);
            //    aiMap = null;
            //}

            return true;
        }
        return false;
    }

    public boolean isTerminal() {

        // Todo fix the upper value
        // 1022 It's a score that throws 70 trash block (10 rows) to the adversary +-
        // based on a 7x15 map. But the adversary could lose with less (or need more) trash block.
        // So this need to be update while opponents change it's state
        // Obs: negative values are illegal moves or lost moves, so they are terminal states
        return value < 0 || value >= 1022;
    }

    /** Used to init the tree root */
    public void init(Uct uct, byte[][] map, int deleteSize, int outRow) {
        this.uct = uct;
        aiMap = AiMap.pool.obtain();
        aiMap.init(map, deleteSize, outRow);
        childMovesLeft.addAll(uct.moves.getArray(uct.color1, uct.color2));
    }

    /** Used to init non root tree nodes **/
    public void init(Uct uct, AiMap parentMap, int color1, int color2, int position, int rotation) {

        this.uct = uct;
        childMovesLeft.addAll(uct.moves.getArray());
        this.aiMap    = parentMap.copy();
        this.color1   = color1;
        this.color2   = color2;
        this.position = position;
        this.rotation = rotation;
    }

    private void clear() {
        uct        = null;
        visits     = 0;
        value      = 0f;
        totalValue = 0f;
        exploit    = 0f;
        explore    = 0f;
        color1     = -1;
        color2     = -1;
        position   = -1;
        rotation   = -1;
    }

    @Override
    public void reset() {

        for(int i = 0; i < children.size; i++) {
            UctNode.pool.free(children.get(i));
        }

        children.clear();
        childMovesLeft.clear();

        if(aiMap != null) {
            AiMap.pool.free(aiMap);
            aiMap = null;
        }

        clear();
    }

    // DebugNode implementation #debugCode

    @Override
    public int getMove() {

        return Moves.getMove(color1, color2, position, rotation);
    }

    @Override
    public String[] getText() {

        return new String[]{
                color1 + "/" + color2 + "/" + position + "/" + rotation,
                fmt1.format(visits) + "/" + fmt2.format(totalValue) + "/" + fmt2.format(value),
                fmt2.format(totalValue / visits),
                fmt2.format(exploit) + " + " + fmt2.format(explore) + " = " + fmt2.format(exploit + explore),
        };
    }

    @Override
    public Array<UctNode> getChildren() {
        return children;
    }

    @Override
    public AiMap getAiMap() { return aiMap; }
}
