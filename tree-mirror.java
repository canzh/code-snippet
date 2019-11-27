public class CodeSnippet {
    public void mirrorTree(TreeNode root) {
        if (root == null)
            return;

        // 交换该节点指向的左右节点。
        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;

        // 对其左右孩子进行镜像处理。
        mirrorTree(root.left);
        mirrorTree(root.right);
    }

    public TreeNode mirrorTree1(TreeNode root) {
        if (root == null)
            return null;

        // 对左右孩子镜像处理
        TreeNode left = mirrorTree1(root.left);
        TreeNode right = mirrorTree1(root.right);

        // 对当前节点进行镜像处理。
        root.left = right;
        root.right = left;
        return root;
    }

    public static void mirrorTreeWithQueue(TreeNode root) {
        if (root == null)
            return;

        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode t = queue.poll();

            Swap(t);
            if (t.right != null) {
                queue.add(t.right);
            }
            if (t.left != null) {
                queue.add(t.left);
            }
        }
    }

    public static void Swap(TreeNode root) {
        TreeNode temp = root.right;
        root.right = root.left;
        root.left = temp;
    }

    public static void mirrorTreeWithStack(TreeNode root) {
        if (root == null)
            return;
        Stack<TreeNode> stack = new Stack<TreeNode>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode t = stack.pop();

            Swap(t);
            if (t.right != null) {
                stack.push(t.right);
            }
            if (t.left != null) {
                stack.push(t.left);
            }
        }

    }
}