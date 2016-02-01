/*
 * Copyright (C) 2015
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sf.jclal.util.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *  Class to control the execution time.
 *
 * @author Eduardo Perez Perdomo
 * @author Oscar Gabriel Reyes Pupo
 */
public class TimeControl implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
     * Times marked
     */
    private ArrayList<Long> marks;

    /**
     * Names and values of intervals
     */
    HashMap<String, Long> timeNames;

    /**
     * Constructor
     *
     * @param num Number of marks in the experiment
     */
    public TimeControl(int num) {
        marks = new ArrayList<Long>(num);
        timeNames = new HashMap<String, Long>(num);
    }

    /**
     * Clean the marks
     */
    public void reset() {
        marks.clear();
        timeNames.clear();
    }

    /**
     * Destroy the objects
     */
    public void destroy() {
        reset();
        marks = null;
        timeNames = null;
    }

    /**
     * Set the current time. It adds a mark
     *
     * @return The time
     */
    public long mark() {
        long ne = System.currentTimeMillis();
        marks.add(ne);
        return ne;
    }

    /**
     * Return the time that exists between two marks
     *
     * @param mark1 The first mark
     * @param mark2 The second mark
     * @return The difference
     */
    public long time(int mark1, int mark2) {
        return time(marks.get(mark1), marks.get(mark2));
    }

    /**
     * Return the time that exists between two marks by discounting time
     *
     * @param mark1 The first mark
     * @param mark2 The second mark
     * @param discount Time to discount
     * @return The difference
     */
    public long time(int mark1, int mark2, long discount) {
        return time(mark1, mark2) - discount;
    }

    /**
     * Return the time that exists between the first mark and the last one
     *
     * @return The difference
     */
    public long time() {
        return time(0, marks.size() - 1);
    }

    /**
     * Return the time that exists between the last two marks
     *
     * @return The difference
     */
    public long timeLastOnes() {
        int size = marks.size();
        return time(size - 1, size - 2);
    }

    /**
     * The absolute value between two times
     *
     * @param time1 first time
     * @param time2 second time
     * @return difference of time
     */
    public long time(long time1, long time2) {
        return Math.abs(time1 - time2);
    }

    /**
     * Add a name associated to the time
     *
     * @param name The name
     * @param time The value
     */
    public void timeName(String name, long time) {
        timeNames.put(name, time);
    }

    /**
     * Return the names and the times
     *
     * @return An array of times
     */
    public Object[][] namesAndTimes() {
        Object[][] dev = new Object[timeNames.size()][2];
        int index = 0;
        for (Map.Entry<String, Long> entry : timeNames.entrySet()) {
            dev[index][0] = entry.getKey();
            dev[index++][1] = entry.getValue();
        }
        return dev;
    }

    /**
     * Get time by name
     *
     * @param name The name of the time
     * @return The time
     */
    public long time(String name) {
        return timeNames.get(name);
    }
}
