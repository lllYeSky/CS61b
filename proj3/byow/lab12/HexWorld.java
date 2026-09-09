package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;


public class HexWorld{
    private static final int WIDTH = 60;
    private static final int HEIGHT = 60;
    private static final long SEED = 74751;
    private static final Random RANDOM = new Random(SEED);

    public static void fillWithHex(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        CreatAllHex(tiles, 5, randomTile());
    }

    private static class Position{
        int x, y;

        Position(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    public static void CreatAllHex(TETile[][] teTiles, int n, TETile thing){
        Position p1 = new Position(0, HEIGHT / 2 + 3 * n);
        Position p2 = new Position(p1.x + 2 * n - 1, p1.y + n);
        Position p3 = new Position(p2.x + 2 * n - 1, p2.y + n);
        Position p4 = new Position(p3.x + 2 * n - 1, p3.y - n);
        Position p5 = new Position(p4.x + 2 * n - 1, p4.y - n);
        CreatLineHex(p1, teTiles, n, thing, 3);
        CreatLineHex(p2, teTiles, n, thing, 4);
        CreatLineHex(p3, teTiles, n, thing, 5);
        CreatLineHex(p4, teTiles, n, thing, 4);
        CreatLineHex(p5, teTiles, n ,thing, 3);
    }

    public static void CreatLineHex(Position p, TETile[][] tetiles, int n, TETile thing, int num){
        if(num < 1) return;
        int originaly = p.y;
        CreatHex(p, tetiles, n, randomTile());
        if(num > 1){
            p.y = originaly - 2 * n;
            CreatLineHex(p, tetiles, n, randomTile(), num-1);
        }
    }

    public static void CreatHex(Position p, TETile[][] tetiles, int n, TETile thing){
        int longest = 3 * n - 2;
        for(int i = 0; i < n; i++){
            int ThingNum = n + 2 * i;
            int NothingNum = (longest - ThingNum) / 2;
            CreatHexHelper(NothingNum, ThingNum, thing, tetiles, p);
            p.y--;
        }
        for(int i = 0; i < n; i++){
            int ThingNum = longest - 2 * i;
            int NothingNum = (longest - ThingNum) / 2;
            CreatHexHelper(NothingNum, ThingNum, thing, tetiles, p);
            p.y--;
        }
    }

    public static void CreatHexHelper(int NothingNum, int ThingNum, TETile thing, TETile[][] teTiles, Position p){
        int x = p.x;
        for(int i = 0; i < NothingNum; i++){
//            teTiles[x][p.y] = Tileset.NOTHING;
            x++;
        }
        for(int i = 0; i < ThingNum; i++){
            teTiles[x][p.y] = thing;
            x++;
        }
        for(int i = 0; i < NothingNum; i++){
//            teTiles[x][p.y] = Tileset.NOTHING;
            x++;
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(7);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.FLOOR;
            case 3: return Tileset.GRASS;
            case 4: return Tileset.MOUNTAIN;
            case 5: return Tileset.SAND;
            case 6: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }


    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] hexTiles = new TETile[WIDTH][HEIGHT];
        fillWithHex(hexTiles);

        ter.renderFrame(hexTiles);
    }


}
