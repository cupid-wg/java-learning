package self.practise.leetcode.adddigits_258;

public class Solution {
    public int addDigits(int num) {
    	//there's an expression for it , detail is in https://en.wikipedia.org/wiki/Digital_root
    	//the expression 1 to 2 step is unclear so i'm using expression 1
    	//a simple infer is if there's a num abcd , then abcd = 1000a + 100b + 10c + d = (a+b+c+d)+999a+99b+9 c
    	// so abcd % 9 = (a+b+c+d) % 9, but if abcd % 9 = 0, the final digit should be 9 not 0
    	// another special num is 0
        return num == 0? 0 : (num%9 == 0? 9:num%9);
    }
}
