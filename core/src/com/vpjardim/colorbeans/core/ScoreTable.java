/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;

import java.util.Comparator;

/**
 * @author Vinícius Jardim
 * 03/09/2016
 */
public class ScoreTable {

    private boolean ordered = false;
    private Array<ScoreTable.Row> rows = new Array<>();

    public static class Row {
        public String nick;
        public int score;
    }

    private final static Comparator compare = new Comparator<ScoreTable.Row>() {
        @Override
        public int compare(ScoreTable.Row r1, ScoreTable.Row r2) {
            return r2.score - r1.score;
        }
    };

    private void order() {
        Sort.instance().sort(rows, compare);
        ordered = true;
    }

    public void addRow(String nick, int score) {
        ScoreTable.Row row = new ScoreTable.Row();
        row.nick = nick;
        row.score = score;
        rows.add(row);
        ordered = false;
    }

    public Array<ScoreTable.Row> getRows() {
        if(!ordered) order();
        return rows;
    }
}
