/*
 * 二分探索木
 */
public class BinarySearchTree
{
    private Node  root; // 二分探索木の根

    /**
     * 二分探索木を生成する
     */
    public BinarySearchTree()
    {

          
    	
        root = null;
    }

    //---------------------------------------------
    // ここでメソッドを定義する
    //---------------------------------------------

// list 9.3  searchメソッド
    /**
     * 二分探索木からキーkeyをもつ節を探索する
     *
     * @param key  探し出すキー
     * @return  キーkeyをもった節が見つかればそれを返し，
     *          見つからなれけばnullを返す
     */
    public Node search(Integer key)
    {
        Node p = root;                  // まず根に注目する
        while (p != null) {             // 注目している節がある限り続ける
                                        // 注目している節とキーを比較する
            int result = key.compareTo(p.data);
            if (result == 0) {          // 節とキーが等しければ，
                return p;               //      見つかった
            } else if (result < 0) {    // キーのほうが小さければ，
                p = p.left;             //      左部分木に進む
            } else {                    // キーのほうが大きければ，
                p = p.right;            //      右部分木に進む
            }
        }
        return null;                    // 探索に失敗した
    }

// list 9.4  insertメソッド
    /**
     * 二分探索木にキーkeyをもつ節を挿入する
     *
     * @param key  挿入するキー
     * @return  挿入したキーをもつ節へのリンクを返す
     *          すでに登録されていればnullを返す
     */
    public Node insert(Integer key)
    {
        Node p = root;                  // まず根に注目する
        Node parent = null;             // parentは現在注目している節の親を指す
        boolean  isLeftChild = false;   // pがparentの左の子ならtrue，
                                        // 右の子ならfalse

        while (p != null) {             // 注目している節がある限り続ける
                                        // 注目している節とキーを比較する
            int result = key.compareTo(p.data);
            if (result == 0) {          // 節とキーが等しければ，
                return null;            //      すでに登録されている
            } else if (result < 0) {    // キーのほうが小さければ，
                parent = p;             //      左部分木に進む
                isLeftChild = true;
                p = p.left;
            } else {                    // キーのほうが大きければ，
                parent = p;             //      右部分木に進む
                isLeftChild = false;
                p = p.right;
            }
        }

        // 新しい節を適切な場所に挿入する
        Node newNode = new Node(key);   // 新しい節を割り当てる
        if (parent == null) {
            root = newNode;             // 新しい節は根になる
        } else if (isLeftChild) {
            parent.left = newNode;      // 節parentの左の子になる
        } else {
            parent.right = newNode;     // 節parentの右の子になる
        }

        return newNode;
    }

// list 9.5  deleteメソッド
    /**
     * 二分探索木からキーkeyをもつ節を削除する
     *
     * @param key  削除するキー
     * @return  削除に成功すればtrue，要素が存在しなければfalseを返す
     */
    public boolean delete(Integer key)
    {
        Node p = root;                  // まず根に注目する
        Node parent = null;             // parentは現在注目している節の親を指す
        boolean  isLeftChild = false;   // pがparentの左の子ならtrue，
                                        // 右の子ならfalse

        while (p != null) {             // 削除対象を探す
            // 注目している節とキーを比較する
            int result = key.compareTo(p.data);
            if (result == 0) {          // 見つかった
                if (p.left == null && p.right == null) { // 葉である
                    // 葉を削除する
                    if (parent == null) {
                        root = null;
                    } else if (isLeftChild) {
                        parent.left = null;
                    } else {
                        parent.right = null;
                    }
                } else if (p.left == null) {            // 右の子のみをもつ
                    // 右の子を親の子にする
                    if (parent == null) {
                        root = p.right;
                    } else if (isLeftChild) {
                        parent.left = p.right;
                    } else {
                        parent.right = p.right;
                    }
                } else if (p.right == null) {           // 左の子のみをもつ
                    // 左の子を親の子にする
                    if (parent == null) {
                        root = p.left;
                    } else if (isLeftChild) {
                        parent.left = p.left;
                    } else {
                        parent.right = p.left;
                    }
                } else {                                // 左右2つの子をもつ
                    // 右部分木の最小の要素を取り除いて変数xに入れる
                    Node x = deleteMin(p, p.right);

                    // 着目している節に最小の要素xを入れる
                    if (parent == null) {
                        root = x;               // (1)
                    } else if (isLeftChild) {
                        parent.left = x;        // (1)
                    } else {
                        parent.right = x;       // (1)
                    }
                    x.right = p.right;          // (2)
                    x.left  = p.left;           // (3)
                }
                return true;            // 削除が成功した

            } else if (result < 0) {    // キーのほうが小さければ，
                parent = p;             //      左部分木に進む
                isLeftChild = true;
                p = p.left;
            } else {                    // キーのほうが大きければ，
                parent = p;             //      右部分木に進む
                isLeftChild = false;
                p = p.right;
            }
        }

        // 削除対象が見つからなかった
        return false;
    }

// list 9.6  deleteminメソッド
    /**
     * 二分探索木から最小の要素を削除する
     *
     * @param  p  二分探索木
     * @param  parent  pの親の節
     * @return    削除した節
     */
    private static Node deleteMin(Node parent, Node p)
    {
        boolean  isLeftChild = false;   // pがparentの左の子ならtrue，
                                        // 右の子ならfalse。
                                        // メソッドが呼び出された時点では，
                                        // pはparentの右の子なので，
                                        // 初期化はfalseである。
        while (p.left != null) {        // 最小の要素を見つける
            parent = p;
            isLeftChild = true;
            p = p.left;
        }

        if (isLeftChild) {              // 最小の要素を取り除く
            parent.left = p.right;
        } else {
            parent.right = p.right;
        }
        return p;                       // 最小の要素を返す
    }

// list 9.7  showAllメソッド
    /**
     * 二分探索木を通りがけ順になぞる
     */
    private static void inorder(Node p)
    {
        if (p == null) {
            return;
        } else {
            inorder(p.left);
            System.out.println(p.data);
            inorder(p.right);
        }
    }

    /**
     * 二分探索木の全要素を昇順に表示する
     */
    public void showAll()
    {
        inorder(root);
    }

// その他:  toStringメソッド
    private static String toStringAux(Node p)
    {
        if (p == null)
            return "-";

        return "(" + p.data + " " + toStringAux(p.left) + " "
            + toStringAux(p.right) + ")";
    }

    public String toString()
    {
        return toStringAux(root);
    }

}