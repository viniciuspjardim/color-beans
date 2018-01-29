/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.tests;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.vpjardim.colorbeans.ai.AiMap;
import com.vpjardim.colorbeans.ai.Moves;
import com.vpjardim.colorbeans.ai.ai3.Tree3;
import com.vpjardim.colorbeans.ai.ai3.Tree3Node;
import com.vpjardim.colorbeans.ai.ai4.Ai4;
import com.vpjardim.colorbeans.ai.ai4.Uct;
import com.vpjardim.colorbeans.ai.ai4.UctNode;
import com.vpjardim.colorbeans.defaults.Db;
import com.vpjardim.colorbeans.tests.treeview.RunnableTV;
import com.vpjardim.colorbeans.tests.treeview.TVNode;
import com.vpjardim.colorbeans.tests.treeview.TreeView;

/**
 * @author Vinícius Jardim
 */

public class Test {

    public static void main(String[] args){
        Test t = new Test();
        t.test9();
    }

    public void test12() {
        System.out.println("1110 = " + 1110/100 % 10);
        System.out.println("10   = " + 10  /100 % 10);
        System.out.println("100  = " + 100 /100 % 10);
        System.out.println("1    = " + 1   /100 % 10);
        System.out.println("1010 = " + 1010/100 % 10);
        System.out.println("1000 = " + 1000/100 % 10);
        System.out.println("1111 = " + 1111/100 % 10);
        System.out.println("1100 = " + 1100/100 % 10);
    }

    public void test11() {
        System.out.println(" 0 % 4 = " +  0 % 4);
        System.out.println(" 1 % 4 = " +  1 % 4);
        System.out.println(" 2 % 4 = " +  2 % 4);
        System.out.println(" 4 % 4 = " +  4 % 4);
        System.out.println("-1 % 4 = " + -1 % 4);
        System.out.println("-2 % 4 = " + -2 % 4);
    }

    public void test10() {

        float a = 1304f;
        float b = 1304f;
        float ar = 7.5750586E-5f;
        float br = 6.614644E-5f;

        a = a + ar;
        b = b + br;

        System.out.println(a == b);
        System.out.printf("%.9f   %.9f", a, b);
    }

    public void test9() {

        // Creating a Uct object
        Uct uct = new Uct(7);
        uct.initProcess(Utils.map, 4, 5, Ai4.formula1, 1, 2);

        TVNode<UctNode> root = new TVNode<>(0, 0, null);
        TreeView.dbgToTV(uct.root, root);

        // Create a new runnable to run the TreeView app/window
        RunnableTV run = new RunnableTV();
        run.tv = new TreeView(root);

        // The app will run in this new thread. This way we can debug (with break points) the main
        // thread while watch the TreeView app (it won't stop because it's in another thread)
        new Thread(run).start();

        // Wait until the TreeView app is created in the new thread
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Iterating throw the UCT algorithm: it adds new nodes search tree
        while(uct.totalIter <= 10000) {

            // If not debugging, make tree build process slower so we can watch. Comment if
            // debugging
            // try {
            //     Thread.sleep(25);
            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            // }

            uct.root.iterate();
            uct.totalIter++;
            run.tv.update();

            //UctNode bc = uct.bestRootChild();

            //System.out.println("Best child: " + bc.color1 + "/" + bc.color2 + "/"
            //        + bc.position + "/" + bc.rotation);
            //
            //System.out.println("Uct iterations: " + uct.totalIter);
            //System.out.println("UctNodes obj: " + UctNode.objCount);
            //System.out.println("AiMap obj: " + AiMap.objCount);

            // Put a brake point in the print below to render the TreeView while debugging the tree
            // built process. Each time the program reach this break point a new node were added and
            // you can watch this live in the TreeView window
            //System.out.println("======================");
        }
    }

    public void test8() {

        // Best child: c1 = 1; c2 = 2; pos = 4; rot = 1
        // Uct iterations: 37215
        // UctNodes obj: 25351
        // AiMap obj: 25350
        // ##### Heap utilization statistics [MB] #####
        // Used Memory:116
        // Free Memory:107
        // Total Memory:224
        // Max Memory:1801
        // ======================

        Uct uct = new Uct(7);
        uct.initProcess(Utils.map, 4, 5, Ai4.formula1, 1, 2);
        uct.limitTime = 8;
        uct.framesAi = 10;

        long timeIni = System.currentTimeMillis();

        while(uct.totalIter <= 100000) {
            uct.root.iterate();
            uct.totalIter++;
        }

        long timeEnd = System.currentTimeMillis();

        // Todo find out why best child is always 1/2/3/1 since 1/2/4/0 leads to the same state
        // Guess it was bad luck. On new tests 1/2/4/0 wore the best too. Redo to confirm
        UctNode bc = uct.bestRootChild();

        System.out.println("Time = " + (timeEnd - timeIni) + "ms");
        System.out.println("Best child: " + bc.color1 + "/" + bc.color2 + "/" + bc.position + "/" +
                bc.rotation);

        System.out.println("Uct iterations: " + uct.totalIter);
        System.out.println("UctNodes obj: " + UctNode.objCount);
        System.out.println("AiMap obj: " + AiMap.objCount);
        System.out.println();
        Utils.printMemory();
        System.out.println("======================");

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setWindowedMode(1080, 860);
        config.setTitle("Tree View");

        // Rendering tree

        //TVNode<UctNode> root = new TVNode<>(0, 0, null);
        //TreeView.dbgToTV(uct.root, root);

        //new Lwjgl3Application(new TreeView<>(root), config);
    }

    public void test7() {

        // The following tests are based on the video:
        // https://www.youtube.com/watch?v=a-1b3IA2ujA
        System.out.println("Base video: https://www.youtube.com/watch?v=a-1b3IA2ujA\n");

        Db db = new Db();
        int score;
        int block;
        int a, b;

        System.out.println("========= Test 1 video at 1:42 =========");
        score = 127;
        System.out.println("Start score: " + score);

        // Chain 1
        block = 4;
        a = block * 10;
        b = db.getChainPower(1) + db.getColorBonus(1) + db.getGroupBonus(block);
        if(b < 1) b = 1;
        score += a * b;
        System.out.println(a + " x " + b + " = " + (a * b) + " => score = " + score);

        // Chain 2
        block = 4;
        a = block * 10;
        b = db.getChainPower(2) + db.getColorBonus(1) + db.getGroupBonus(block);
        if(b < 1) b = 1;
        score += a * b;
        System.out.println(a + " x " + b + " = " + (a * b) + " => score = " + score);


        System.out.println("\n========= Test 2 video at 2:31 =========");
        score = 655;
        System.out.println("Start score: " + score);

        // Chain 1
        block = 8;
        a = block * 10;
        // Group bonus 0 because no group has more then 4 blocks
        b = db.getChainPower(1) + db.getColorBonus(2) + db.getGroupBonus(0);
        if(b < 1) b = 1;
        score += a * b;
        System.out.println(a + " x " + b + " = " + (a * b) + " => score = " + score);

        // Chain 2
        block = 5;
        a = block * 10;
        b = db.getChainPower(2) + db.getColorBonus(1) + db.getGroupBonus(block);
        if(b < 1) b = 1;
        score += a * b;
        System.out.println(a + " x " + b + " = " + (a * b) + " => score = " + score);


        System.out.println("\n========= Test 3 video at 2:58 =========");
        score = 1806;
        System.out.println("Start score: " + score);

        // Chain 1
        block = 7;
        a = block * 10;
        b = db.getChainPower(1) + db.getColorBonus(1) + db.getGroupBonus(block);
        if(b < 1) b = 1;
        score += a * b;
        System.out.println(a + " x " + b + " = " + (a * b) + " => score = " + score);

        // Chain 2
        block = 9;
        a = block * 10;
        // Only has one group with more then 4 blocks (5 blocks in this case)
        b = db.getChainPower(2) + db.getColorBonus(2) + db.getGroupBonus(5);
        if(b < 1) b = 1;
        score += a * b;
        System.out.println(a + " x " + b + " = " + (a * b) + " => score = " + score);
    }

    public void test6() {

        Tree3Node n = Tree3Node.pool.obtain();
        n.aiMap = AiMap.pool.obtain();
        n.aiMap.init(Utils.map, 4, 2);

        Tree3Node.pool.free(n);
        Tree3Node.pool.free(n);
        Tree3Node.pool.free(n);
        Tree3Node.pool.free(n);

        System.out.println(Tree3Node.pool.getFree()); // 4

        Tree3Node n1 = Tree3Node.pool.obtain();
        Tree3Node n2 = Tree3Node.pool.obtain();

        System.out.println(Tree3Node.pool.getFree()); // 2

        System.out.println(n == n1); // true
        System.out.println(n == n2); // true
    }

    public void test5() {

        Tree3 t = new Tree3(7);

        int i = 0;

        // ##### Heap utilization statistics [MB] #####
        // Used Memory:41
        // Free Memory:81
        // Total Memory:123
        // Max Memory:1801
        // i:69, 65, 85, 63, 63, 73, 72, 72

        // ##### Heap utilization statistics [MB] #####
        // Used Memory:37
        // Free Memory:85
        // Total Memory:123
        // Max Memory:1801
        // i:68, 76, 74, 78, 67, 68, 73, 74
        for(int j = 0; j < 10; j++) {

            t.processFinished = false;
            t.root = Tree3Node.pool.obtain();
            t.root.aiMap = AiMap.pool.obtain();
            t.root.aiMap.init(Utils.map, 4, 2);
            t.cacheA.add(t.root);
            t.bestNode = t.root;

            t.color1 = 1;
            t.color2 = 2;

            if(t.color1 != -1) {
                t.addChild(t.root, t.color1, t.color2);
                t.cacheSwap();
            }

            while (!t.processFinished) {
                i++;
                t.process();
            }
            t.reset();
        }
        Utils.printMemory();
        System.out.println("i:" + i);
        System.out.println();
    }

    public void test4() {
        long now = TimeUtils.nanoTime();
        System.out.println(now);

        try {
            Thread.sleep(1800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(TimeUtils.timeSinceNanos(now));
    }

    public void test3() {
        Moves moves = new Moves();

        moves.setMove(102043);
        System.out.println(moves);

        System.out.println("getMove: " + moves.getMove());

        moves.init(7);
        //moves.setColor(2, 2);

        int cont = 0;
        /*for(Moves m : moves) {
            System.out.println("[" + cont + "] " + m.color1 + ";" + m.color2 + ";" + m.position + ";" + m.rotation);
            cont++;
        }*/
    }
    
    public void test2() {

        AiMap m = new AiMap();
        m.init(Utils.map, 4, 5);
        AiMap c;

        for(int i = 0; i < 10; i++) {

            long time = System.currentTimeMillis();

            for(int j = 0; j < 300000; j++) {
                c = m.copy();
                c.process(
                        MathUtils.random(1, 5),
                        MathUtils.random(1, 5),
                        MathUtils.random(0, 5),
                        MathUtils.random(0, 3));
            }

            time = System.currentTimeMillis() - time;
            System.out.println("time: " + time);
        }
    }
    
    public void test1() {
        float time;
        float shift = 0.0f;

        for(int i = 0; i < 10; i++) {

            time = i;
            shift = 0.5f * 7 * time * time;
        }

        System.out.println(shift);
    }
}