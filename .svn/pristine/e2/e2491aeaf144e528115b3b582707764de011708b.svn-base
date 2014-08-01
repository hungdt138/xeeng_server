/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.line.data;

import java.util.ArrayList;
import java.util.Stack;

import com.tv.xeeng.game.data.Couple;

/**
 *
 * @author tuanda
 */
public class LineMatrix {

    private Color[][] detail = new Color[9][9];
    public String detailInString;
    private ArrayList<Color> hideCurr = new ArrayList<Color>();
    private ArrayList<Couple<Couple<Integer, Integer>, Color>> fVisible =
            new ArrayList<Couple<Couple<Integer, Integer>, Color>>();

    public void setDetail(Color[][] detail) {
        this.detail = detail;
    }

    public String getDetailInString() {
        return detailInString;
    }

    public void setDetailInString(String detailInString) {
        this.detailInString = detailInString;
    }

    
    public String tableToString() {
        String res = "";
        for (int i = 0; i < sizeOfMatrix; i++) {
            for (int j = 0; j < sizeOfMatrix; i++) {
                res += detail[i][j].toInt();
            }
        }
        return res;
    }
    public void setDetail(String s){
        for(int i = 0; i<s.length(); i++){
            char c = s.charAt(i);
            Color color = Color.intToColor(Integer.parseInt(c+""));
            int y = i/9;
            int x = i%9;
            detail[y][x] = color;
        }
    }
    public Color[][] getDetail() {
        return detail;
    }

    public ArrayList<Couple<Couple<Integer, Integer>, Color>> getfVisible() {
        return fVisible;
    }

    public ArrayList<Color> getHideCurr() {
        return hideCurr;
    }

    public LineMatrix() {
        for (int i = 0; i < sizeOfMatrix; i++) {
            for (int j = 0; j < sizeOfMatrix; j++) {
                detail[i][j] = Color.Nil;
            }
        }

    }

    public void start() {
        fVisible = makeVisibleNode();
        for (Couple<Couple<Integer, Integer>, Color> c : fVisible) {
            int x = c.e1.e2;
            int y = c.e1.e1;
            detail[y][x] = c.e2;
        }
        hideCurr = makeHideNode(3);
    }

    private int makeRandomNum(int n) {
        return (int) Math.round(Math.random() * n);
    }

//    public Color intToColor(int n) {
//        switch (n) {
//            case 0:
//                return Color.Blue;
//            case 1:
//                return Color.Brown;
//            case 2:
//                return Color.Green;
//            case 3:
//                return Color.Orange;
//            case 4:
//                return Color.Red;
//            case 5:
//                return Color.Tohop;
//            case 6:
//                return Color.Viollet;
//            case 7:
//                return Color.Yellow;
//            default:
//                return Color.Nil;
//        }
//    }
    public int careauxInRest() {
        int index = 0;
        for (int i = 0; i < sizeOfMatrix; i++) {
            for (int j = 0; j < sizeOfMatrix; j++) {
                if (detail[i][j] == Color.Nil) {
                    index++;
                }
            }
        }
        return index;
    }
//    public void moveHideNode(int o_x, int o_y, int n_x, int n_y) {
//        int index = -1;
//        Color c1 = Color.Nil;
//        for (int i = 0; i < 3; i++) {
//            Couple<Couple<Integer, Integer>, Color> c = this.hideCurr.get(i);
//            if (c.e1.e1 == o_x && c.e1.e2 == o_y) {
//                index = i;
//                c1 = c.e2;
//            }
//        }
//        if ((index != -1) && (c1 != Color.Nil)) {
//            this.hideCurr.remove(index);
//            this.hideCurr.add(new Couple<Couple<Integer, Integer>, Color>(new Couple<Integer, Integer>(n_x, n_y), c1));
//        }
//    }
//    private boolean checkDupHideNode(ArrayList<Couple<Couple<Integer, Integer>, Color>> temp, Couple<Integer, Integer> node) {
//        for (Couple<Couple<Integer, Integer>, Color> t : temp) {
//            if ((t.e1.e1 == node.e1) && (t.e1.e2 == node.e2)) {
//                return true;
//            }
//        }
//        return false;
//    }
    private int sizeOfMatrixForMakeVisibleNode = 8; // 0-8
    private int sizeOfMatrix = 9; // 0-8
    private int numColor = 7;//0-7

    public ArrayList<Color> makeHideNode(int n) {
        ArrayList<Color> res = new ArrayList<Color>();
        for (int i = 0; i < n; i++) {
            int c = makeRandomNum(numColor) + 1;
            //int c = makeRandomNum(1) + 5;// Test
            Color temp = Color.intToColor(c);
            res.add(temp);
        }
        return res;
    }

    public ArrayList<Couple<Couple<Integer, Integer>, Color>> makeVisibleNode() {
        ArrayList<Couple<Couple<Integer, Integer>, Color>> res = new ArrayList<Couple<Couple<Integer, Integer>, Color>>();
        boolean[][] check = new boolean[sizeOfMatrix][sizeOfMatrix];
        for (int i = 0; i < sizeOfMatrix; i++) {
            for (int j = 0; j < sizeOfMatrix; j++) {
                check[j][i] = false;
            }
        }
        int i = 0;
        //for (int i = 0; i < 5; i++) {
        while (i < 5) {
            Couple<Couple<Integer, Integer>, Color> temp;
            int x = makeRandomNum(sizeOfMatrixForMakeVisibleNode);
            int y = makeRandomNum(sizeOfMatrixForMakeVisibleNode);
            int c = makeRandomNum(numColor) + 1;
            if (check[y][x]) {
                continue;
            }
            check[y][x] = true;
            temp = new Couple<Couple<Integer, Integer>, Color>(
                    new Couple<Integer, Integer>(y, x),
                    Color.intToColor(c));
            res.add(temp);
            i++;
        }
        //}
        return res;
    }
    //0:false, 1:true, 2:loser

    public int play(Couple<Integer, Integer> source,
            Couple<Integer, Integer> d, boolean check,
            ArrayList<Couple<Couple<Integer, Integer>, Color>> hides) throws Exception {

        int res = move(source, d, check);
        //displayTable();
        //Thread.sleep(200);
        if (res >= 5) {
            return res;
        } else {
            return (unhide(hides));
        }
        //return ((res) ? 1 : 0);
    }

    private void displayHides(ArrayList<Couple<Couple<Integer, Integer>, Color>> f) {
        System.out.println("===hides===");
        for (Couple<Couple<Integer, Integer>, Color> c : f) {
            System.out.println("y:" + c.e1.e1 + ",x:" + c.e1.e2 + "-" + c.e2.toString());
        }
    }

    private void displayTable() {
        System.out.println("===Table===");
        for (int i = 0; i < sizeOfMatrix; i++) {
            for (int j = 0; j < sizeOfMatrix; j++) {
                if (detail[i][j] != Color.Nil) {
                    System.out.println("y:" + i + ",x:" + j + "-" + detail[i][j].toString());
                }
            }
        }
    }

    private ArrayList<Couple<Integer, Integer>> ngang(int x, int y, Color c) {
        ArrayList<Couple<Integer, Integer>> res = new ArrayList<Couple<Integer, Integer>>();
        for (int i = x - 1; i >= 0; i--) {
            if (detail[y][i] == c) {
                res.add(new Couple<Integer, Integer>(y, i));
            } else {
                break;
            }
        }
        for (int i = x + 1; i < sizeOfMatrix; i++) {
            if (detail[y][i] == c) {
                res.add(new Couple<Integer, Integer>(y, i));
            } else {
                break;
            }
        }
        return res;
    }

    private ArrayList<Couple<Integer, Integer>> doc(int x, int y, Color c) {
        ArrayList<Couple<Integer, Integer>> res = new ArrayList<Couple<Integer, Integer>>();
        for (int i = y - 1; i >= 0; i--) {
            if (detail[i][x] == c) {
                res.add(new Couple<Integer, Integer>(i, x));
            } else {
                break;
            }
        }
        for (int i = y + 1; i < sizeOfMatrix; i++) {
            if (detail[i][x] == c) {
                res.add(new Couple<Integer, Integer>(i, x));
            } else {
                break;
            }
        }
        return res;
    }

    private ArrayList<Couple<Integer, Integer>> cheoTrai(int x, int y, Color c) {
        ArrayList<Couple<Integer, Integer>> res = new ArrayList<Couple<Integer, Integer>>();
        int i = 1;
        while ((x - i >= 0) && (y - i >= 0)) {
            if (detail[y - i][x - i] == c) {
                res.add(new Couple<Integer, Integer>(y - i, x - i));
                i++;
            } else {
                break;
            }
        }
        i = 1;
        while ((x + i < sizeOfMatrix) && (y + i < sizeOfMatrix)) {
            if (detail[y + i][x + i] == c) {
                res.add(new Couple<Integer, Integer>(y + i, x + i));
                i++;
            } else {
                break;
            }
        }
        return res;
    }

    private ArrayList<Couple<Integer, Integer>> cheoPhai(int x, int y, Color c) {
        ArrayList<Couple<Integer, Integer>> res = new ArrayList<Couple<Integer, Integer>>();
        int i = 1;
        while ((y - i >= 0) && (x + i < sizeOfMatrix)) {
            if (detail[y - i][x + i] == c) {
                res.add(new Couple<Integer, Integer>(y - i, x + i));
                i++;
            } else {
                break;
            }
        }
        i = 1;
        while ((y + i < sizeOfMatrix) && (x - i >= 0)) {
            if (detail[y + i][x - i] == c) {
                res.add(new Couple<Integer, Integer>(y + i, x - i));
                i++;
            } else {
                break;
            }
        }
        return res;
    }

    private int check5(int x, int y, Color c) {
        ArrayList<Couple<Integer, Integer>> res = new ArrayList<Couple<Integer, Integer>>();
        ArrayList<Couple<Integer, Integer>> doc = doc(x, y, c);
        ArrayList<Couple<Integer, Integer>> ngang = ngang(x, y, c);
        ArrayList<Couple<Integer, Integer>> cheoT = cheoTrai(x, y, c);
        ArrayList<Couple<Integer, Integer>> cheoP = cheoPhai(x, y, c);
        boolean ret = false;
        if (doc.size() >= 4) {
            ret = true;
            res.addAll(doc);
        }
        if (ngang.size() >= 4) {
            ret = true;
            res.addAll(ngang);
        }
        if (cheoP.size() >= 4) {
            ret = true;
            res.addAll(cheoP);
        }
        if (cheoT.size() >= 4) {
            ret = true;
            res.addAll(cheoT);
        }
        if (ret) {
            res.add(new Couple<Integer, Integer>(y, x));
            for (Couple<Integer, Integer> temp : res) {
                int x1 = temp.e2;
                int y1 = temp.e1;
                detail[y1][x1] = Color.Nil;
            }
            return res.size();
        } else {
            return 0;
        }
    }

    private int move(Couple<Integer, Integer> s, Couple<Integer, Integer> d, boolean check) throws Exception {
        int sX = s.e2;
        int sY = s.e1;
        int dX = d.e2;
        int dY = d.e1;
        int c = detail[sY][sX].toInt();
        detail[sY][sX] = Color.Nil;
        detail[dY][dX] = Color.intToColor(c);
        //detail[dY][dX] = temp;
        if (check) {
            int index = check5(dX, dY, detail[dY][dX]);
            if (index >= 5) {
                return index;
            } else {
                throw new Exception("Ăn sai rồi bạn ơi!");
            }
        } else {
            return 0;
        }
//        } else {
//            throw new Exception("Không thể di chuyển như thế được!");
//        }
    }

    private boolean check(Couple<Integer, Integer> s, Couple<Integer, Integer> d) {
        Stack<Couple<Integer, Integer>> stack = new Stack<Couple<Integer, Integer>>();
        stack.push(s);
        boolean[][] ch = new boolean[sizeOfMatrix][sizeOfMatrix];
        for (int i = 0; i < sizeOfMatrix; i++) {
            for (int j = 0; j < sizeOfMatrix; j++) {
                ch[i][j] = false;
            }
        }
        int xT = s.e2;
        int yT = s.e1;
        ch[yT][xT] = true;
        while (true) {
            if (stack.isEmpty()) {
                return false;
            }
            Couple<Integer, Integer> temp = stack.pop();
            int x = temp.e2;
            int y = temp.e1;
            ch[y][x] = true;
            if (x == d.e2 && y == d.e1) {
                return true;
            }
            if ((y < sizeOfMatrix - 1) && (detail[y + 1][x] == Color.Nil) && (!ch[y + 1][x])) {
                stack.push(new Couple(y + 1, x));
            }
            if ((y > 0) && (detail[y - 1][x] == Color.Nil) && (!ch[y - 1][x])) {
                stack.push(new Couple(y - 1, x));
            }
            if ((x < sizeOfMatrix - 1) && (detail[y][x + 1] == Color.Nil) && (!ch[y][x + 1])) {
                stack.push(new Couple(y, x + 1));
            }
            if ((x > 0) && (detail[y][x - 1] == Color.Nil) && (!ch[y][x - 1])) {
                stack.push(new Couple(y, x - 1));
            }
        }
    }
    /*
     * 0:false 1:true 2: loser >5: number of eat
     */

    public int unhide(ArrayList<Couple<Couple<Integer, Integer>, Color>> f) throws Exception {
        if (careauxInRest() < 3) {
            return 2;
        }
        for (Couple<Couple<Integer, Integer>, Color> t : f) {
            int x = t.e1.e2;
            int y = t.e1.e1;
            if (detail[y][x] == Color.Nil) {
                detail[y][x] = t.e2;
                if (check5(x, y, t.e2) >= 5) {
                    return 1;
                }
            } else {
                throw new Exception("Ô x=" + x + ",y=" + y + "đã có màu:" + detail[y][x].toString()
                        + "; không thể gán màu" + t.e2.toString() + "được nữa");
            }
        }
        return 0;
    }
}
