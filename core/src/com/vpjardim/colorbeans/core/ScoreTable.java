/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Sort;

import java.util.Comparator;

/**
 * @author Vinícius Jardim
 * 2016/09/03
 */
public class ScoreTable {

    // Todo create a match id and to link multiple players to one match
    // This way it is possible to see multiple opponents score in one match

    public static final int GMODE_CAMPAIGN  = 1;
    public static final int GMODE_TRAINING  = 2;

    private static final Comparator compare = new Comparator<ScoreTable.Row>() {
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
        public boolean win;
        public int score;
        public int scoreSum;
        public float time;
    }

    public void addRow(int gameMode, String nick, boolean win, int score, int scoreSum,
            float time) {

        ScoreTable.Row row = new ScoreTable.Row();

        row.gameMode = gameMode;
        row.nick = nick;
        row.win = win;
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

    public static void save(ScoreTable table) {

        if(table.saved) return;

        // Sort before save
        table.sort();

        Json json = new Json();

        String jsonTxt = json.prettyPrint(table.rows.toArray());
        FileHandle file = Gdx.files.local("state/scores.json");
        file.writeString(jsonTxt, false);

        table.saved = true;
    }

    public static ScoreTable load() {

        // The object loaded is already sorted and obviously saved

        FileHandle file = Gdx.files.local("state/scores.json");
        ScoreTable score = new ScoreTable();

        if(file.exists()) {
            String jsonTxt = file.readString();
            Json json = new Json();
            score.rows.addAll(json.fromJson(Row[].class, jsonTxt));
        }

        return score;
    }
}
