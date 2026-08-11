package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Repository.COMMITS_DIR;
import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 * does at a high level.
 *
 * @author lllYeSky
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private final String message;
    private final Date date;
    private final String datestamp;
    private List<String> parentHash;
    private Map<String, String> pathtohash;
    private String id;

    public Commit() {
        message = "initial commit";
        date = new Date(0);
        parentHash = new ArrayList<>();
        pathtohash = new TreeMap<>();
        datestamp = datetotimestamp(date);
        id = sha1(message, datestamp, parentHash.toString(), maptostring(pathtohash));
    }

    public Commit(String me, String pa, Map<String, String> parentmap) {
        message = me;
        date = new Date();
        parentHash = new ArrayList<>();
        parentHash.add(pa);
        pathtohash = new TreeMap<>(parentmap);
        datestamp = datetotimestamp(date);
        id = sha1(message, datestamp, parentHash.toString(), maptostring(pathtohash));
    }

    public Commit(String me, String pa1, String pa2, Map<String, String> parentmap) {
        message = me;
        date = new Date();
        parentHash = new ArrayList<>();
        parentHash.add(pa1);
        parentHash.add(pa2);
        pathtohash = new TreeMap<>(parentmap);
        datestamp = datetotimestamp(date);
        id = sha1(message, datestamp, parentHash.toString(), maptostring(pathtohash));
    }

    private static String datetotimestamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z",
                Locale.US);
        return dateFormat.format(date);
    }

    private static String maptostring(Map<String, String> m) {
        String s = "";
        if (m == null || m.isEmpty()) {
            return s;
        }
        for (var entry : m.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            s += key + ":" + value + "\n";
        }
        return s;
    }

    public void printlog() {
        System.out.println("===");
        System.out.println("commit " + id);
        if (parentHash.size() > 1) {
            String p1 = parentHash.get(0).substring(0, 7);
            String p2 = parentHash.get(1).substring(0, 7);
            System.out.println("Merge: " + p1 + " " + p2);
        }
        System.out.println("Date: " + datestamp);
        System.out.println(message);
        System.out.println();
    }

    public void save() {
        id = sha1(message, datestamp, parentHash.toString(), maptostring(pathtohash));
        File c = join(COMMITS_DIR, id);
        writeObject(c, this);
    }

    public String getid() {
        return id;
    }

    public Map<String, String> getmap() {
        return pathtohash;
    }

    public String getparenthash() {
        return parentHash.isEmpty() ? "" : parentHash.get(0);
    }

    public String getmessage() {
        return message;
    }

    public List<String> getparenthashes() {
        return parentHash;
    }
}
