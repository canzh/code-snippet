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

        while(!stack.isEmpty() || p != null) {
            while(p != null) {
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

        while(!stack.isEmpty() || p != null) {
            while(p != null) {
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
}