import java.util.*;

public class CodeSnippet {
    public static class TreeNode {
        public int val;
        public List<TreeNode> children;
    }

    public boolean getPath(TreeNode root, TreeNode toFind, Deque<TreeNode> path) {
        if (root == toFind) {
            return true;
        }

        path.add(root);
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