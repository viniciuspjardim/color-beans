/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;
import com.google.gson.Gson;

import java.util.Comparator;

/**
 * @author Vinícius Jardim
 * 03/09/2016
 */
public class ScoreTable {

    public static final int GMODE_CAMPAIGN = 1;
    public static final int GMODE_TRAINING = 2;

    private final static Comparator compare = new Comparator<ScoreTable.Row>() {
        @Override
        public int compare(ScoreTable.Row r1, ScoreTable.Row r2) {
            return r2.score - r1.score;
        }
    };

    private final Array<ScoreTable.Row> rows = new Array<>(true, 20);
    private boolean sorted = true;
    private boolean saved = true;

    public static class Row {

        public int gameMode;

        public String nick;
        public int score;
        public int scoreSum;
        public int stars;
        public float time;

        /** True if has an opponent */
        public boolean hasOpp;
        public String oppNick;
        public int oppScore;
        public int oppDificulty;
    }

    public void addRow(String nick, int score, int scoreSum, float time) {

        ScoreTable.Row row = new ScoreTable.Row();
        row.nick = nick;
        row.score = score;
        row.scoreSum = scoreSum;
        row.time = time;

        rows.add(row);
        sorted = false;
        saved = false;
    }

    public Array<ScoreTable.Row> getRows() {
        sort();
        return rows;
    }

    private void sort() {

        if(sorted) return;

        Sort.instance().sort(rows, compare);
        sorted = true;
    }

    public static ScoreTable load(String path) {

        // The object loaded is already sorted and obviously saved

        FileHandle file = Gdx.files.local(path);
        ScoreTable score = new ScoreTable();

        if(file.exists()) {
            String jsonTxt = file.readString();
            Gson gson = new Gson();
            score.rows.addAll(gson.fromJson(jsonTxt, Row[].class));
        }

        return score;
    }

    public static void save(ScoreTable table) {

        if(table.saved) return;

        // Sort before save
        table.sort();

        Gson gson = new Gson();

        String jsonTxt = gson.toJson(table.rows.toArray());
        FileHandle file = Gdx.files.local("state/scores.json");
        file.writeString(jsonTxt, false);

        table.saved = true;
    }
}
