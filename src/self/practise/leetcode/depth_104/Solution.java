package self.practise.leetcode.depth_104;

public class Solution {
	public int maxDepth(TreeNode root) {
		//a key point is cover the condition that input is null
		int leftLength = 0;
		int rightLength = 0;
		if(root == null){
			return 0;
		}
		if (root.left == null && root.right == null) {
			return 1;
		}
		if (root.left != null) {
			leftLength = maxDepth(root.left);
		}
		if (root.right != null) {
			rightLength = maxDepth(root.right);
		}
		return leftLength > rightLength ? leftLength + 1 : rightLength + 1;
	}

}
//a better way found
//public class Solution {
//public int maxDepth(TreeNode root) {
//    if (root == null)
//        return 0;
//    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
//}
