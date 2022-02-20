package online.meinkraft.highscores.table;

import java.util.Comparator;

public class EntryComparator implements Comparator<Entry> {

    @Override
    public int compare(Entry a, Entry b) {
        return (int) Math.ceil(a.getScore() - b.getScore());
    }
    
}
