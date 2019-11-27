import java.util.*;

public class CodeSnippet {
    public static class TreeNode {
        public int val;
        public TreeNode left;
        public TreeNode right;
    }

    // level order
    public void level(TreeNode t) {
        if (t == null)
            return;
        List<TreeNode> list = new ArrayList<>();
        Queue<TreeNode> q = new Queue<>();
        q.offer(t);
        while (!q.isEmpty()) {
            TreeNode tmp = q.poll();
            if (tmp.left != null)
                q.offer(tmp.left);
            if (tmp.right != null)
                q.offer(tmp.right);

            list.add(tmp);
        }
    }

    // vertical order
    public List<List<Integer>> verticalTraversal(TreeNode root) {
        TreeMap<Integer, TreeMap<Integer, PriorityQueue<Integer>>> map = new TreeMap<>();
        dfs(root, 0, 0, map);
        List<List<Integer>> list = new ArrayList<>();
        for (TreeMap<Integer, PriorityQueue<Integer>> ys : map.values()) {
            list.add(new ArrayList<>());
            for (PriorityQueue<Integer> nodes : ys.values()) {
                while (!nodes.isEmpty()) {
                    list.get(list.size() - 1).add(nodes.poll());
                }
            }
        }
        return list;
    }

    private void dfs(TreeNode root, int x, int y, TreeMap<Integer, TreeMap<Integer, PriorityQueue<Integer>>> map) {
        if (root == null) {
            return;
        }
        if (!map.containsKey(x)) {
            map.put(x, new TreeMap<>());
        }
        if (!map.get(x).containsKey(y)) {
            map.get(x).put(y, new PriorityQueue<>());
        }
        map.get(x).get(y).offer(root.val);
        dfs(root.left, x - 1, y + 1, map);
        dfs(root.right, x + 1, y + 1, map);
    }

    // iterative traversal
    public void postOrderTraversal(TreeNode root) {

    }

    public void preOrder(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode p = root;

        while (!stack.isEmpty() || p != null) {
            while (p != null) {
                stack.push(p);

                // visit p for logic

                p = p.left;
            }

            p = stack.pop();
            p = p.right;
        }
    }

    public void inOrder(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode p = root;

        while (!stack.isEmpty() || p != null) {
            while (p != null) {
                stack.push(p);
                p = p.left;
            }

            p = stack.pop();

            // visit p for logic

            p = p.right;
        }
    }

    // recursive
    public void preOrderRecursive(TreeNode root) {
        if (root == null) {
            return;
        }

        // visit logic

        preOrderRecursive(root.left);
        preOrderRecursive(root.right);

        // logic before return back to parent node
    }

    // leetcode 114, used to flatten b-tree to linked list
    public void postOrderRightToLeft(TreeNode root) {
        if (root == null)
            return;

        postOrderRightToLeft(root.right);
        postOrderRightToLeft(root.left);

        // visit root
    }

    // morris traversal is in-order
    public void morrisTraversal(TreeNode root) {
        if (root == null)
            return;

        TreeNode cur = root, pre;

        while (cur != null) {
            if (cur.left == null) {
                System.out.print(cur.val + " ");
                cur = cur.right;
            } else {
                /* Find the inorder predecessor of current */
                pre = cur.left;
                while (pre.right != null && pre.right != cur) {
                    pre = pre.right;
                }

                /* Make current as right child of its inorder predecessor */
                if (pre.right == null) {
                    pre.right = cur;
                    cur = cur.left;
                }
                /*
                 * Revert the changes made in the 'if' part to restore the original tree i.e.,
                 * fix the right child of predecessor
                 */
                else {
                    pre.right = null;
                    System.out.print(cur.val + " ");
                    cur = cur.right;
                }
            }
        }
    }

    // right - root - left in-order morris traverse

    /* Get the node with the smallest value greater than this one. */
    private TreeNode getSuccessor(TreeNode node) {
        TreeNode succ = node.right;
        while (succ.left != null && succ.left != node) {
            succ = succ.left;
        }
        return succ;
    }

    public TreeNode convertBST(TreeNode root) {
        int sum = 0;
        TreeNode node = root;

        while (node != null) {
            /*
             * If there is no right subtree, then we can visit this node and continue
             * traversing left.
             */
            if (node.right == null) {
                sum += node.val;
                node.val = sum;
                node = node.left;
            }
            /*
             * If there is a right subtree, then there is at least one node that has a
             * greater value than the current one. therefore, we must traverse that subtree
             * first.
             */
            else {
                TreeNode succ = getSuccessor(node);
                /*
                 * If the left subtree is null, then we have never been here before.
                 */
                if (succ.left == null) {
                    succ.left = node;
                    node = node.right;
                }
                /*
                 * If there is a left subtree, it is a link that we created on a previous pass,
                 * so we should unlink it and visit this node.
                 */
                else {
                    succ.left = null;
                    sum += node.val;
                    node.val = sum;
                    node = node.left;
                }
            }
        }

        return root;
    }
}