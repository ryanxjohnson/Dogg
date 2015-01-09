package edu.umkc.rjcmf.dogg.dao;

import java.util.ArrayList;

import android.database.Cursor;

public class PottySeries extends ArrayList<Potty> {

	private static final long serialVersionUID = 5304523564485608182L;
	
    public PottySeries(Potty... potties) {
        final int length = potties.length;
        Potty previous = null;
        Potty current = null;
        for (int i = 0; i < length; i++) {
            current = potties[i];
            if (previous != null && previous.hasNext() == false) {
                previous.setNext(current);
            }
            if (current.hasPrevious() == false) {
                current.setPrevious(previous);
            }
            super.add(current);
            previous = current;

        }
    }
	
    @Override
    public boolean add(Potty potty) {
        if (size() > 0) {
            last().setNext(potty);
            potty.setPrevious(last());
        }
        super.add(potty);

        return true;
    }
    
    public long getTimeRange() {
        return Math.abs(last().getTimestamp() - first().getTimestamp());
    }
    
    public Potty first() {
        return get(0);
    }

    public Potty last() {
        return get(size() - 1);
    }

    /**
     * In order for this to work, it's expected that the potties are sorted in
     * ascending order by date.
     * 
     * @param cursor
     * @return
     */
    public static ArrayList<PottySeries> load(Cursor cursor) {
        ArrayList<PottySeries> output = new ArrayList<PottySeries>();

        PottySeries series = new PottySeries();
        while (cursor.moveToNext()) {
        	Potty potty = new Potty(cursor);
            if (potty.isRestart()) {
                output.add(series);
                series = new PottySeries();
            }
            series.add(potty);
        }
        output.add(series);
        return output;
    }
   
} 
