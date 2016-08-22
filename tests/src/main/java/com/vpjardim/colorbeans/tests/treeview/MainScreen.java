package com.vpjardim.colorbeans.tests.treeview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.ai.AiMap;
import com.vpjardim.colorbeans.ai.DebugNode;
import com.vpjardim.colorbeans.ai.Moves;

/**
 * @author Vinícius Jardim
 * 03/08/2016
 */
public class MainScreen<T extends DebugNode> implements Screen {

    public static final Color COLOR_1 = new Color(0x550000ff);
    public static final Color COLOR_2 = new Color(0x005500ff);
    public static final Color COLOR_3 = new Color(0x000055ff);

    public ShapeRenderer sr;
    public SpriteBatch batch;
    public InputProc input;

    public TVNode<T> selectedNode;
    public boolean isParentBlocks;
    public Moves moves;

    public IntArray levelSizes;
    public int maxLevelSizes;
    public Array<TVNode<T>> cacheA;
    public Array<TVNode<T>> cacheB;

    public BitmapFont font16;
    public boolean isColor1;

    @Override
    public void show() {

        sr = new ShapeRenderer();
        batch = new SpriteBatch();

        input = new InputProc(this);
        InputMultiplexer multiInput = new InputMultiplexer();
        multiInput.addProcessor(input);
        multiInput.addProcessor(new GestureDetector(input));
        Gdx.input.setInputProcessor(multiInput);

        selectedNode = null;
        isParentBlocks = true;
        moves = new Moves();

        levelSizes = new IntArray();
        maxLevelSizes = 0;
        cacheA = new Array<>();
        cacheB = new Array<>();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        font16 = generator.generateFont(parameter); // font size 12 pixels
        font16.setColor(Color.WHITE);
        generator.dispose();

        isColor1 = true;

        updateLevelSizes();
        updatePositions();
    }

    public void select(TVNode<T> newSelected) {

        if(selectedNode != null) selectedNode.selected = false;
        if(newSelected != null) newSelected.selected = true;

        selectedNode = newSelected;
    }

    public void moveTo(Vector2 pos) {

        Camera cam = TreeView.get().cam;
        cam.p.x = pos.x;
        cam.p.y = pos.y;
    }

    public void tap(float screenX, float screenY) {

        TreeView<T> tv = TreeView.get();

        screenX -= Gdx.graphics.getWidth() / 2f;
        screenY = +Gdx.graphics.getHeight() / 2f - screenY;

        TVNode<T> found = tv.root.find(screenX, screenY);

        select(found);
    }

    public void keyPressed(int keycode) {

        if(selectedNode == null) return;

        if(keycode == Input.Keys.SPACE) {
            isParentBlocks = !isParentBlocks;
        }
        if(keycode == Input.Keys.UP) {
            if(selectedNode.parent != null) {
                selectedNode.parent.child = selectedNode;
                moveTo(selectedNode.parent.pos);
                select(selectedNode.parent);
            }
        }
        else if(keycode == Input.Keys.LEFT) {
            if(selectedNode.left != null) {
                moveTo(selectedNode.left.pos);
                select(selectedNode.left);
            }
        }
        else if(keycode == Input.Keys.RIGHT) {
            if(selectedNode.right != null) {
                moveTo(selectedNode.right.pos);
                select(selectedNode.right);
            }
        }
        else if(keycode == Input.Keys.DOWN) {
            if(selectedNode.child != null) {
                moveTo(selectedNode.child.pos);
                select(selectedNode.child);
            }
        }
    }

    public void updateLevelSizes() {

        TVNode root = TreeView.get().root;
        levelSizes.clear();

        root.countLevel(0, levelSizes);

        maxLevelSizes = -1;

        for(int i = 0; i < levelSizes.size; i++) {

            int currLevelSize = levelSizes.get(i);
            if(currLevelSize > maxLevelSizes) maxLevelSizes = currLevelSize;
        }
    }

    public void updatePositions() {

        TVNode root = TreeView.get().root;
        cacheA.add(root);
        int levelPos = 0;

        while(cacheA.size > 0) {

            TVNode<T> lastParent = null;

            for(int cachePos = 0; cachePos < cacheA.size; cachePos++) {

                TVNode<T> curr = cacheA.get(cachePos);

                // Setting child, left and right
                if(curr.children.size > 0)
                    curr.child = curr.children.first();

                if(cachePos > 0) curr.left = cacheA.get(cachePos -1);

                if(cachePos < cacheA.size -1) curr.right = cacheA.get(cachePos +1);

                // If is root
                if(curr.parent == null) {
                    curr.pos.x = 0f;
                    curr.pos.y = 0f;
                    curr.color = COLOR_1;
                }
                else {
                    float levelWidth = maxLevelSizes * 100f;
                    float space = levelWidth / cacheA.size;

                    curr.pos.x = (space * cachePos) - levelWidth / 2f;
                    curr.pos.y = levelPos * -maxLevelSizes * 100;

                    if(curr.parent != lastParent) isColor1 = !isColor1;

                    curr.color = isColor1 ? COLOR_1 : COLOR_2;

                    lastParent = curr.parent;
                }

                for(int i = 0; i < curr.children.size; i++) {
                    cacheB.add(curr.children.get(i));
                }
            }
            levelPos++;
            Array<TVNode<T>> aux = cacheA;
            cacheA = cacheB;
            cacheB = aux;
            cacheB.clear();
        }

        cacheA.clear();
        cacheB.clear();
    }

    @Override
    public void render(float delta) {

        synchronized(TreeView.get().lock) {

            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            TVNode root = TreeView.get().root;

            sr.setProjectionMatrix(TreeView.get().cam.cam.combined);
            sr.begin(ShapeRenderer.ShapeType.Filled);

            sr.setColor(Color.RED);
            root.drawLine(sr);

            sr.setColor(Color.DARK_GRAY);
            root.drawShape(sr);

            renderMap();

            sr.end();

            batch.setProjectionMatrix(TreeView.get().cam.cam.combined);
            batch.begin();

            root.drawText(batch);

            batch.end();
        }
    }

    public void renderMap() {

        if(selectedNode == null || selectedNode.obj == null) return;

        AiMap aiMap = selectedNode.obj.getAiMap();
        moves.setMove(selectedNode.obj.getMove());
        int color1 = moves.color1;
        int color2 = moves.color2;
        int position = moves.position;
        int rotation = moves.rotation;

        byte[][] b = aiMap.b;

        float side = 20;
        int outRow = aiMap.outRow;
        float px = + Gdx.graphics.getWidth()/2f - (side * b.length) - 20f;
        float py = + Gdx.graphics.getHeight()/2 - 40f - side;

        if(isParentBlocks && selectedNode.parent != null) {

            b = selectedNode.parent.obj.getAiMap().b;

            if (rotation == 0) {
                b[position][outRow - 2] = (byte) color1; // upper
                b[position][outRow - 1] = (byte) color2; // center
            } else if (rotation == 1) {
                b[position][outRow - 1] = (byte) color2; // center stay
                b[position + 1][outRow - 1] = (byte) color1; // upper goes down/left
            } else if (rotation == 2) {
                b[position][outRow - 2] = (byte) color2; // center goes up
                b[position][outRow - 1] = (byte) color1; // upper goes down
            } else if (rotation == 3) {
                b[position][outRow - 1] = (byte) color1; // upper goes down
                b[position + 1][outRow - 1] = (byte) color2; // center goes right
            }
        }

        // Draw map blocks
        for(int i = 0; i < b.length; i++) {

            for(int j = outRow - 2; j < b[0].length; j++) {

                if(b[i][j] == Block.EMPTY) {

                    sr.setColor(Color.DARK_GRAY);

                    if(j < outRow) sr.setColor(COLOR_2);

                    sr.circle(
                            px + (side * i),
                            py - (side * (j -outRow)),
                            side/8f
                    );
                    continue;
                }

                sr.setColor(Block.intToColor(b[i][j]));
                sr.circle(
                        px + (side * i),
                        py - (side * (j -outRow)),
                        side / 2f
                );

                if(j < outRow) b[i][j] = 0;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        TreeView.get().cam.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        sr.dispose();
        font16.dispose();
        batch.dispose();
    }
}