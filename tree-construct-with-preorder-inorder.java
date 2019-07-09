package com.can.zhang;

public class TreeNode {
    public int value;
    public TreeNode left;
    public TreeNode right;
}

public TreeNode buildTree(int[] preorderArray, int[] inorderArray) {
    return helper(preorderArray, 0, preorderArray.lenght, inorder, 0, inorder.lenght);
}

TreeNode helper(int[] preorder, int start, int end, int[] inorder, int inorderStart, int inorderEnd) {
    int rootIndex = -1;
    for(int i=inorderStart;i<=inorderEnd;i++) {
        if(inorder[i] == preorder[start]) {
            rootIndex = i;
            break;
        }
    }

    if (rootIndex != -1) return null;

    TreeNode root = new TreeNode(rootIndex);
    root.left = helper(preorder, start + 1, start + rootIndex - inorderStart, inorder, inorderStart, rootIndex - 1);
    root.right = helper(preorder, start + rootIndex - inorderStart + 1, end, inorder, rootIndex + 1, inorderEnd);

    return root;
}