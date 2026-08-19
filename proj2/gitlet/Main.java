package gitlet;

import static gitlet.Repository.*;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author lllYeSky
 */
public class Main {

    public static void main(String[] args) {
        int num = args.length;
        if (num == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                checkoperands(num, 1);
                init();
                break;

            case "add":
                checkoperands(num, 2);
                havegit();
                add(args[1]);
                break;

            case "commit":
                checkoperands(num, 2);
                havegit();
                commit(args[1]);
                break;

            case "rm":
                checkoperands(num, 2);
                havegit();
                rm(args[1]);
                break;

            case "log":
                checkoperands(num, 1);
                havegit();
                log();
                break;

            case "global-log":
                checkoperands(num, 1);
                havegit();
                globallog();
                break;

            case "find":
                checkoperands(num, 2);
                havegit();
                find(args[1]);
                break;

            case "status":
                checkoperands(num, 1);
                havegit();
                status();
                break;

            case "checkout":
                havegit();
                if (num == 2) {
                    checkout2(args[1]);
                } else if (num == 3) {
                    if (!args[1].equals("--")) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    checkout3(args[2]);
                } else if (num == 4) {
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    checkout4(args[1], args[3]);
                } else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;

            case "branch":
                checkoperands(num, 2);
                havegit();
                branch(args[1]);
                break;

            case "rm-branch":
                checkoperands(num, 2);
                havegit();
                rmbranch(args[1]);
                break;

            case "reset":
                checkoperands(num, 2);
                havegit();
                reset(args[1]);
                break;

            case "merge":
                checkoperands(num, 2);
                havegit();
                merge(args[1]);
                break;

            case "add-remote":
                checkoperands(num, 3);
                havegit();
                addremote(args[1], args[2]);

            case "rm-remote":
                checkoperands(num, 2);
                havegit();
                rmremote(args[1]);

            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    public static void havegit() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static void checkoperands(int num, int n) {
        if (num != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
