package self.practise.leetcode.happynum_202;

import java.util.ArrayList;
import java.util.List;

public class Solution {
	//a more quick solution is in discussion https://leetcode.com/discuss/74503/1ms-java-solution 
    public boolean isHappy(int n) {
    	//2 is not happy number
    	//the endless loop for example is x*x+y*y = xy, or a*a = a , 
    	//the key point is to find out the break point of recursion and how to avoid endless recursion(for example number 2)
    	List<Integer> tmp = new ArrayList<Integer>();
    	tmp.add(n);
    	return isHappy(n,tmp);
    }
    
    public boolean isHappy(int n, List<Integer> tempResult){
    	long result = 0L;
    	int origin = n;
    	while(n>0){
    		if(n<10){
    			result+= n*n;
    		}else{
    			int tmp = n%10;
    			result += tmp*tmp;
    		}
    		n /= 10;
    	}
    	if(result == 1||result == origin){
    		return true;
        //it's the key point , we should end the loop if the count result have reached the max int value
    	//also we have to keep all result to avoid endless loop
    	}else if(result > Integer.MAX_VALUE || tempResult.contains((int)result)){
    		return false;
    	}else{
    		tempResult.add((int) result);
    		return isHappy((int)result, tempResult);
    	}
    }
    
    public static void main(String[] args){
    	Solution s = new Solution();
    	System.out.println(s.isHappy(2));
    }
}
