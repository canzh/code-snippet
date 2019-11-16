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
        // 如果树为 null 直接返回。否则将根节点入队列。
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.add(root);
        while (!queue.isEmpty()) {
            // 队列不为空时，节点出队，交换该节点的左右子树。
            TreeNode root1 = queue.poll();

            Swap(root1);
            if (root1.right != null) {
                queue.add(root1.right);
                // 如果左子树不为 null 入队
            }
            if (root1.left != null) {
                queue.add(root1.left);
                // 如果右子树不为 null 入队。
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
            // 当栈不为 null 时出栈，交换左右子树。
            TreeNode root1 = stack.pop();

            Swap(root1);
            if (root1.right != null) {
                // 右子树不为 null 入栈
                stack.push(root1.right);
            }
            if (root1.left != null) {
                // 左子树不为 null 入栈
                stack.push(root1.left);
            }
        }

    }
}