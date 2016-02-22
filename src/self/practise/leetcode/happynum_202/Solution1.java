package self.practise.leetcode.happynum_202;

import java.util.Arrays;
import java.util.List;

public class Solution1 {
	public boolean isHappy(int n) {
		//all not happy num , the sum of digit square will goto below results
		List<Integer> notHappyResult = Arrays.asList(new Integer[]{4,16,37,58,89,145,42,20});
		int result = 0;
    	while(n>0){
    		if(n<10){
    			result+= n*n;
    		}else{
    			int tmp = n%10;
    			result += tmp*tmp;
    		}
    		n /= 10;
    	}
    	if(result == 1){
    		return true;
    	}else if(notHappyResult.contains(result)){
    		return false;
    	}else{
    		return isHappy(result);
    	}
	}
}
