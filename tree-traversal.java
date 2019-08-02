import java.util.*;

public class CodeSnippet {
    public static class TreeNode {
        public int val;
        public TreeNode left;
        public TreeNode right;
    }

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
}