package com.Algorithm;
/*
 * ����ȫ����
 */
public class Arrange {
	
	private String hql="'%";
 
    public String getHql() {
		return hql;
	}
    

	public void arrange (String a1,String[] str, int st, int len)//a1�������ݿ��ֶ� content����content��ƥ�� % %�� 
    {  
        if (st == len - 1)  
        {  
            for (int i = 0; i < len; i ++)  
            {   
                hql=hql+str[i]+"%";
            }  
            hql=hql+"' or "+a1+" like '%";
        }  
        else  
        {  
            for (int i = st; i < len; i ++)  
            {  
                swap(str, st, i);  
                arrange(a1, str, st + 1, len);  
                swap(str, st, i);  
            }  
        }     
    }
    public   void swap(String[] str, int i, int j)  
    {  
        String temp = new String();  
        temp = str[i];  
        str[i] = str[j];  
        str[j] = temp;  
    }  
}
