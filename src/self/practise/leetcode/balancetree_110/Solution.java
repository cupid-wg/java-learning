package self.practise.leetcode.balancetree_110;

public class Solution {
	public boolean isBalanced(TreeNode root) {
		//the key point to the problem is you have to count the length of your left and right subtree and compare it
		return getLength(root) == -1 ? false : true;
	}

	public int getLength(TreeNode root) {
		if (root == null)
			return 0;
		if (root.left == null && root.right == null) {
			return 1;
		} else if (root.left == null) {
			if (root.right.left != null || root.right.right != null) {
				return -1;
			} else {
				return 2;
			}
		} else if (root.right == null) {
			if (root.left.left != null || root.left.right != null) {
				return -1;
			} else {
				return 2;
			}
		} else {
			int leftLength = getLength(root.left);
			int rightLength = getLength(root.right);
            
			//have ever debugged into here because forgot to increase the length
			return (leftLength == -1 || rightLength == -1 || Math
					.abs(leftLength - rightLength) > 1) ? -1 : (leftLength
					+ rightLength + 1) / 2 + 1;
		}
	}

	public static void main(String[] args) {
		TreeNode root = new TreeNode(1);
		root.left = new TreeNode(2);
		root.right = new TreeNode(2);
		root.right.left = null;
		root.right.right = null;
		root.left.left = new TreeNode(3);
		root.left.right = new TreeNode(3);
		root.left.left.left = new TreeNode(4);
		root.left.left.right = new TreeNode(4);

		System.out.println((new Solution()).isBalanced(root));

	}
}
//a better way of the solution is to keep the tree length during recurse , the sample is as below:
//private int helper(TreeNode root, int height)
//{
//    if (root == null)
//    {
//        return height;
//    }
//
//    int leftTree = helper(root.left, height + 1);
//    int rightTree = helper(root.right, height + 1);
//    if (leftTree < 0 || rightTree < 0 || Math.abs(leftTree - rightTree) > 1)
//    {
//        return -1;
//    }
//
//    return Math.max(leftTree, rightTree);
//}
//
//public boolean isBalanced(TreeNode root) {
//    return helper(root, 0) >= 0;
//}
