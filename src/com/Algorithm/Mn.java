package com.Algorithm;

import java.util.LinkedList;
import java.util.List;

/*
 * ���紫��Ϊ1,2,3,4
 * �����ȡ3����
 * ���շ���List<String>���:[1 2 3],[2 3 4],[1 3 4]
 */

public class Mn {
	
	private List<String> temp=new LinkedList<String>();

	public   List<String> getTemp() {
		return temp;
	}



	public void Combine(String str[],int a,int p,String key){
		if(0==a){
			temp.add(key);
			return ;
		}
		if(a+p>str.length)return;
		for(int j=p;j<str.length;j++){
			p=j;
			Combine(str,a-1,p+1,key+str[j]+" ");
		}
		return;
	}
 
	
}
