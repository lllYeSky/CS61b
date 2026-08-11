package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 * does at a high level.
 *
 * @author lllYeSky
 */
public class Repository {

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /**
     * obj目录，放 commit 和 blog
     */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /**
     * commit目录
     */
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");
    /**
     * blog目录
     */
    public static final File BLOGS_DIR = join(OBJECTS_DIR, "blogs");

    /**
     * branch目录
     */
    public static final File BRANCHS_DIR = join(GITLET_DIR, "branchs");

    /**
     * HEAD
     */
    public static final File HEAD = join(GITLET_DIR, "head");

    /**
     * stage目录
     */
    public static final File STAGE = join(GITLET_DIR, "stage");

    public static void init() {
        if (GITLET_DIR.exists()) {
            error("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdirs();
        OBJECTS_DIR.mkdirs();
        COMMITS_DIR.mkdirs();
        BLOGS_DIR.mkdirs();
        BRANCHS_DIR.mkdirs();
        Commit start = new Commit();
        start.save();
        File master = join(BRANCHS_DIR, "master");
        writeContents(master, start.getid());
        writeContents(HEAD, "master");
    }

    public static void add(String path) {
        File f = join(CWD, path);
        if (!f.exists()) {
            error("File does not exist.");
        }
        String hash = sha1(readContents(f));
        writeContents(join(BLOGS_DIR, hash), readContents(f));
        TreeMap<String, String> m = new TreeMap<>();
        if (STAGE.exists()) {
            m = readObject(STAGE, TreeMap.class);
        }
        Commit c = getcurrentcommit();
        Map<String, String> ma = c.getmap();
        boolean over = false;
        for (Map.Entry<String, String> entry : ma.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals(path) && value.equals(hash)) {
                over = true;
            }
        }
        if (over) {
            if (m.containsKey(path)) {
                m.remove(path);
            }
        } else {
            m.put(path, hash);
        }
        writeObject(STAGE, m);
    }

    public static void commit(String info) {
        TreeMap<String, String> stageMap =
                STAGE.exists() ? readObject(STAGE, TreeMap.class) : new TreeMap<>();
        if (stageMap.isEmpty()) {
            error("No changes added to the commit.");
        }
        if (info.trim().isEmpty()) {
            error("Please enter a commit message.");
        }
        Commit parent = getcurrentcommit();
        Commit newmap = new Commit(info, parent.getid(), parent.getmap());
        for (Map.Entry<String, String> entry : stageMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.equals("")) {
                newmap.getmap().remove(key);
            } else {
                newmap.getmap().put(key, value);
            }
        }
        newmap.save();
        String branch = readContentsAsString(HEAD).trim();
        File branchFile = join(BRANCHS_DIR, branch);
        writeContents(branchFile, newmap.getid());
        writeObject(STAGE, new TreeMap<>());
    }

    public static void rm(String path) {
        TreeMap<String, String> stage = new TreeMap<>();
        Commit com = getcurrentcommit();
        Map<String, String> track = com.getmap();
        if (STAGE.exists()) {
            stage = readObject(STAGE, TreeMap.class);
        }
        if (stage.containsKey(path)) {
            stage.remove(path);
            writeObject(STAGE, stage);
            System.exit(0);
        }
        if (track.containsKey(path)) {
            stage.put(path, "");
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            writeObject(STAGE, stage);
            System.exit(0);
        }
        error("No reason to remove the file.");
    }

    public static void log() {
        Commit cur = getcurrentcommit();
        while (!cur.getparenthash().isEmpty()) {
            cur.printlog();
            cur = readObject(join(COMMITS_DIR, cur.getparenthash()), Commit.class);
        }
        cur.printlog();
    }

    public static void globallog() {
        List<String> all = plainFilenamesIn(COMMITS_DIR);
        for (String id : all) {
            Commit com = readObject(join(COMMITS_DIR, id), Commit.class);
            com.printlog();
        }
    }

    public static void find(String mes) {
        List<String> all = plainFilenamesIn(COMMITS_DIR);
        boolean found = false;
        for (String id : all) {
            Commit com = readObject(join(COMMITS_DIR, id), Commit.class);
            if (com.getmessage().equals(mes)) {
                System.out.println(com.getid());
                found = true;
            }
        }
        if (!found) {
            error("Found no commit with that message.");
        }
    }

    public static void status() {
        System.out.println("=== Branches ===");
        List<String> bran = plainFilenamesIn(BRANCHS_DIR);
        Collections.sort(bran);
        for (String file : bran) {
            if (file.equals(readContentsAsString(HEAD).trim())) {
                System.out.print("*");
            }
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        TreeMap<String, String> stage =
                STAGE.exists() ? readObject(STAGE, TreeMap.class) : new TreeMap<>();
        for (Map.Entry<String, String> entry : stage.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!value.equals("")) {
                System.out.println(key);
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (Map.Entry<String, String> entry : stage.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.equals("")) {
                System.out.println(key);
            }
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void checkout3(String file) {
        Commit com = getcurrentcommit();
        Map<String, String> map = com.getmap();
        if (map.containsKey(file)) {
            String hash = map.get(file);
            File target = join(CWD, file);
            File parent = target.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            writeContents(target, readContents(join(BLOGS_DIR, hash)));
        } else {
            error("File does not exist in that commit.");
        }
    }

    public static void checkout4(String id, String file) {
        Commit com = idtocommit(id);
        if (com == null) {
            error("No commit with that id exists.");
        }
        Map<String, String> map = com.getmap();
        if (map.containsKey(file)) {
            String hash = map.get(file);
            File target = join(CWD, file);
            File parent = target.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            writeContents(target, readContents(join(BLOGS_DIR, hash)));
        } else {
            error("File does not exist in that commit.");
        }
    }

    public static void checkout2(String bran) {
        if (!checkbranch(bran)) {
            error("No such branch exists.");
        }
        if (bran.equals(readContentsAsString(HEAD).trim())) {
            error("No need to checkout the current branch.");
        }
        Commit curcom = getcurrentcommit();
        Map<String, String> curmap = curcom.getmap();
        String tarid = readContentsAsString(join(BRANCHS_DIR, bran));
        Commit tarcom = readObject(join(COMMITS_DIR, tarid), Commit.class);
        Map<String, String> tarmap = tarcom.getmap();
        TreeMap<String, String> stage =
                STAGE.exists() ? readObject(STAGE, TreeMap.class) : new TreeMap<>();
        List<String> all = plainFilenamesIn(CWD);
        for (String file : all) {
            if (!curmap.containsKey(file) &&
                    (!stage.containsKey(file) || stage.get(file).isEmpty()) &&
                    tarmap.containsKey(file)) {
                error("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        writeObject(STAGE, new TreeMap<>());
        for (Map.Entry<String, String> entry : tarmap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (join(CWD, key).getParentFile() != null) {
                join(CWD, key).getParentFile().mkdirs();
            }
            writeContents(join(CWD, key), readContents(join(BLOGS_DIR, value)));
        }
        for (Map.Entry<String, String> entry : curmap.entrySet()) {
            String key = entry.getKey();
            if (!tarmap.containsKey(key)) {
                join(CWD, key).delete();
            }
        }
        writeContents(HEAD, bran);
    }

    public static void branch(String bran) {
        List<String> allbran = plainFilenamesIn(BRANCHS_DIR);
        if (allbran != null && allbran.contains(bran)) {
            error("A branch with that name already exists.");
        }
        String id = readContentsAsString(join(BRANCHS_DIR, readContentsAsString(HEAD).trim())).trim();
        writeContents(join(BRANCHS_DIR, bran), id);
    }

    public static void rmbranch(String bran) {
        List<String> allbran = plainFilenamesIn(BRANCHS_DIR);
        if (allbran != null && !allbran.contains(bran)) {
            error("A branch with that name does not exist.");
        }
        String head = readContentsAsString(HEAD).trim();
        if (head.equals(bran)) {
            error("Cannot remove the current branch.");
        }
        join(BRANCHS_DIR, bran).delete();
    }

    public static void reset(String id) {
        Commit tarcom = idtocommit(id);
        Commit curcom = getcurrentcommit();
        if (tarcom == null) {
            error("No commit with that id exists.");
        }
        Map<String, String> tarmap = tarcom.getmap();
        Map<String, String> curmap = curcom.getmap();
        TreeMap<String, String> stage =
                STAGE.exists() ? readObject(STAGE, TreeMap.class) : new TreeMap<>();
        List<String> work = plainFilenamesIn(CWD);
        for (String file : work) {
            if (!curmap.containsKey(file) &&
                    (!stage.containsKey(file) || stage.get(file).isEmpty()) &&
                    tarmap.containsKey(file)) {
                error("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        writeObject(STAGE, new TreeMap<>());
        for (Map.Entry<String, String> entry : tarmap.entrySet()) {
            String path = entry.getKey();
            String blobHash = entry.getValue();
            File target = join(CWD, path);
            File parent = target.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            writeContents(target, readContents(join(BLOGS_DIR, blobHash)));
        }
        for (String path : curmap.keySet()) {
            if (!tarmap.containsKey(path)) {
                File file = join(CWD, path);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        writeContents(join(BRANCHS_DIR, readContentsAsString(HEAD).trim()), tarcom.getid());
    }

    public static void merge(String bran) {
        TreeMap<String, String> stage =
                STAGE.exists() ? readObject(STAGE, TreeMap.class) : new TreeMap<>();
        precheck(stage, bran);
        String anceid = conance(bran);
        Commit ancecom = idtocommit(anceid);
        Commit curcom = getcurrentcommit();
        Commit tarcom = idtocommit(readContentsAsString(join(BRANCHS_DIR, bran)).trim());
        Set<String> allpaths = new HashSet<>();
        allpaths.addAll(ancecom.getmap().keySet());
        allpaths.addAll(curcom.getmap().keySet());
        allpaths.addAll(tarcom.getmap().keySet());
        String ance = "";
        String cur = "";
        String tar = "";
        boolean havecon = false;
        checkuntrackedfile(ancecom, curcom, tarcom, stage, bran);
        Map<String, String> commap = new TreeMap<>();
        for (String f : allpaths) {
            ance = existid(ancecom, f);
            cur = existid(curcom, f);
            tar = existid(tarcom, f);
            if (cur.equals(tar)) {
                if (!cur.equals("0")) {
                    checkoutFileFromCommit(tarcom, f);
                    add(f);
                } else {
                    File file = join(CWD, f);
                    if (file.exists()) {
                        file.delete();
                    }
                    stage.remove(f);
                }
                commap.put(f, cur);
            } else if (ance.equals(cur)) {
                if (!tar.equals("0")) {
                    checkoutFileFromCommit(tarcom, f);
                    add(f);
                } else {
                    File file = join(CWD, f);
                    if (file.exists()) {
                        file.delete();
                    }
                    stage.remove(f);
                }
                commap.put(f, tar);
            } else if (ance.equals(tar)) {
                if (!cur.equals("0")) {
                    checkoutFileFromCommit(curcom, f);
                    add(f);
                } else {
                    File file = join(CWD, f);
                    if (file.exists()) {
                        file.delete();
                    }
                    stage.remove(f);
                }
                commap.put(f, cur);
            } else {
                conflict(f, cur, tar);
                commap.put(f, sha1(readContents(join(CWD, f))));
                havecon = true;
            }
        }

        String mess = "Merged " + bran + " into " + readContentsAsString(HEAD).trim() + ".";
        Commit com = new Commit(mess, curcom.getid(), tarcom.getid(), commap);
        com.save();
        writeContents(join(BRANCHS_DIR, readContentsAsString(HEAD).trim()), com.getid());
        writeObject(STAGE, new TreeMap<>());
        if (havecon) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    public static Commit getcurrentcommit() {
        String branch = readContentsAsString(HEAD).trim();
        String hash = readContentsAsString(join(BRANCHS_DIR, branch)).trim();
        return readObject(join(COMMITS_DIR, hash), Commit.class);
    }

    public static Commit idtocommit(String id) {
        List<String> comdir = plainFilenamesIn(COMMITS_DIR);
        int sum = 0;
        String matchedid = null;
        for (String i : comdir) {
            if (i.startsWith(id)) {
                sum++;
                matchedid = i;
            }
        }
        if (sum == 1) {
            return readObject(join(COMMITS_DIR, matchedid), Commit.class);
        }
        return null;
    }

    public static boolean checkbranch(String bran) {
        List<String> brandir = plainFilenamesIn(BRANCHS_DIR);
        for (String b : brandir) {
            if (b.equals(bran)) {
                return true;
            }
        }
        return false;
    }

    public static void error(String info) {
        System.out.println(info);
        System.exit(0);
    }

    public static String existid(Commit com, String file) {
        Map<String, String> map = com.getmap();
        if (map.containsKey(file)) {
            return map.get(file);
        }
        return "0";
    }

    public static void conflict(String path, String cur, String tar) {
        File con = join(CWD, path);
        StringBuilder content = new StringBuilder();
        content.append("<<<<<<< HEAD\n");
        if (cur != null && !cur.equals("0")) {
            content.append(readContentsAsString(join(BLOGS_DIR, cur)));
        }
        content.append("\n=======\n");
        if (tar != null && !tar.equals("0")) {
            content.append(readContentsAsString(join(BLOGS_DIR, tar)));
        }
        content.append("\n>>>>>>>\n");
        String conflictContent = content.toString();

        String blobHash = sha1(conflictContent);
        writeContents(join(BLOGS_DIR, blobHash), conflictContent);

        File workFile = join(CWD, path);
        writeContents(workFile, conflictContent);

        add(path);
    }

    public static String conance(String bran) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        String headid = getcurrentcommit().getid();
        queue.add(headid);
        visited.add(headid);
        while (!queue.isEmpty()) {
            String id = queue.poll();
            Commit commit = readObject(join(COMMITS_DIR, id), Commit.class);
            List<String> parents = commit.getparenthashes();
            for (String parentId : parents) {
                if (!visited.contains(parentId)) {
                    visited.add(parentId);
                    queue.add(parentId);
                }
            }
        }

        Queue<String> tarqueue = new LinkedList<>();
        Set<String> tarvisited = new HashSet<>();
        Commit tarcom = idtocommit(readContentsAsString(join(BRANCHS_DIR, bran)).trim());
        String tarid = tarcom.getid();
        tarqueue.add(tarid);
        while (!tarqueue.isEmpty()) {
            String id = tarqueue.poll();
            if (visited.contains(id)) {
                return id;
            }
            Commit commit = readObject(join(COMMITS_DIR, id), Commit.class);
            for (String parentId : commit.getparenthashes()) {
                if (!tarvisited.contains(parentId)) {
                    tarvisited.add(parentId);
                    tarqueue.add(parentId);
                }
            }
        }
        return null;
    }

    public static void checkoutFileFromCommit(Commit com, String f) {
        String id = com.getmap().get(f);
        File target = join(CWD, f);
        File parent = target.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        writeContents(target, readContents(join(BLOGS_DIR, id)));
    }

    public static void checkuntrackedfile(Commit ancecom, Commit curcom, Commit tarcom,
                                          TreeMap<String, String> stage, String bran) {
        String ance = "";
        String cur = "";
        String tar = "";
        List<String> workFiles = plainFilenamesIn(CWD);
        if (workFiles != null) {
            for (String fileName : workFiles) {
                File workFile = join(CWD, fileName);
                String filePath = workFile.getPath();
                if (!curcom.getmap().containsKey(filePath)
                        && (!stage.containsKey(filePath) || stage.get(filePath).isEmpty())) {
                    ance = existid(ancecom, filePath);
                    cur = existid(curcom, filePath);
                    tar = existid(tarcom, filePath);
                    String result;
                    if (cur.equals(tar)) {
                        result = cur;
                    } else if (ance.equals(cur)) {
                        result = tar;
                    } else if (ance.equals(tar)) {
                        result = cur;
                    } else {
                        result = "conflict";
                    }
                    if (!result.equals("0") || result.equals("conflict")) {
                        error("There is an untracked file in the way; delete it, or add and commit it first.");
                    }
                }
            }
        }
    }

    public static void precheck(TreeMap<String, String> stage, String bran) {
        if (!stage.isEmpty()) {
            error("You have uncommitted changes.");
        }
        if (!checkbranch(bran)) {
            error("A branch with that name does not exist.");
        }
        if (readContentsAsString(HEAD).trim().equals(bran)) {
            error("Cannot merge a branch with itself.");
        }

        String anceid = conance(bran);

        if (readContentsAsString(join(BRANCHS_DIR, bran)).trim().equals(anceid)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (getcurrentcommit().getid().trim().equals(anceid)) {
            checkout2(bran);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
    }
}
