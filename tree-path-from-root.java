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