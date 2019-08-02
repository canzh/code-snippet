package com.can.zhang;

class CodeSnippet {
    static class TreeNode {
        public int value;
        public TreeNode left;
        public TreeNode right;

        TreeNode(int v) {
            value = v;
        }
    }

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        return helper(preorder, 0, inorder, 0, inorder.length - 1);
    }

    TreeNode helper(int[] preorder, int start, int[] inorder, int inorderStart, int inorderEnd) {
        int rootIndex = -1;
        for (int i = inorderStart; i <= inorderEnd; i++) {
            if (inorder[i] == preorder[start]) {
                rootIndex = i;
                break;
            }
        }

        if (rootIndex == -1)
            return null;

        TreeNode root = new TreeNode(inorder[rootIndex]);
        root.left = helper(preorder, start + 1, inorder, inorderStart, rootIndex - 1);
        root.right = helper(preorder, start + rootIndex - inorderStart + 1, inorder, rootIndex + 1, inorderEnd);

        return root;
    }
}