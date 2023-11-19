import java.util.*;

public class CodeSnippet {
    public static class TreeNode {
        public int val;
        public List<TreeNode> children;
    }

    public boolean getPath(TreeNode root, TreeNode toFind, Deque<TreeNode> path) {
        path.add(root);

        if (root == toFind) {
            return true;
        }

        boolean found = false;

        for (TreeNode child : root.children) {
            found = getPath(child, toFind, path);

            if (found)
                break;
        }

        if (!found) {
            path.removeLast();
        }

        return found;
    }
}


/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
class Solution {
    // Method 1
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || p == root || q == root)
            return root;

        List<TreeNode> p1 = new LinkedList<>();
        List<TreeNode> p2 = new LinkedList<>();

        getPathFromRoot(root, p, p1);
        getPathFromRoot(root, q, p2);

        return findDiffPoint(p1, p2);
    }

    boolean getPathFromRoot(TreeNode root, TreeNode p, List<TreeNode> path) {
        if (root == null)
            return false;

        path.add(root);
        
        if (root.val == p.val)
            return true;


        if (getPathFromRoot(root.left, p, path))
            return true;
        if (getPathFromRoot(root.right, p, path))
            return true;

        path.remove(path.size() - 1);

        return false;
    }

    TreeNode findDiffPoint(List<TreeNode> l1, List<TreeNode> l2) {
        int len = Math.min(l1.size(), l2.size());
        TreeNode p = null;
        for (int i = 0; i < len; i++) {
            if (l1.get(i) == l2.get(i)) {
                p = l1.get(i);
            } else {
                break;
            }
        }
        return p;
    }


    // Method 2
    public TreeNode lowestCommonAncestor2(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q)
            return root;
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        return left == null ? right : (right == null ? left : root);
    }
}


class TreeToGraph {
    Map<TreeNode, List<TreeNode>> map = new HashMap();

    private void buildMap(TreeNode node, TreeNode parent) {
        if (node == null) return;
        if (!map.containsKey(node)) {
            map.put(node, new ArrayList<TreeNode>());

            if (parent != null)  {
                map.get(node).add(parent);
                map.get(parent).add(node);
            }

            buildMap(node.left, node);
            buildMap(node.right, node);
        }
    }

    public void main() {
        buildMap(root, null); 
    }
}
