package com.Algorithm;
/*
 * �ָ�ؼ���
 * ȥǰβ�ո�  �ٰ��м�ո�ȡ���ؼ���
 */
public class KeywordSplit {
	
	public String[]  getKeyWord(String str){
		str=str.trim();
		char kw[]=str.toCharArray();
		String wd="";
		String word[]=new String[100];
		int j=0;
		for(int i=0;i<kw.length;i++)
		{
			if(kw[i]!=' '){
				wd=wd+kw[i];
			}
			else if(!wd.equals("")){
				word[j]=""+wd;
				wd="";
				j++;
			}
		}
		if(!wd.equals(""))word[j]=wd;
		String word1[]=new String[j+1];
		System.arraycopy(word, 0, word1, 0, j+1);
		return word1;
	}
}
