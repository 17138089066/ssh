package com.Algorithm;

public class PagerService 
{
	public Pager getPager(String currentPage,String pagerMethod,int totalRows,int pageSize) 
	{
		/*
		 * 1.��һ�ν���ҳ��  currentPageΪ��
		 * 2.ͨ����ת��ʽ����action
		 * 3.ͨ�������ҳ��ǰһҳ...��ʽ
		 */
		Pager pager = new Pager(totalRows,pageSize);
		
		if(currentPage != null){
			pager.refresh(Integer.parseInt(currentPage));
		}
		
		if(pagerMethod != null) 
		{
			if (pagerMethod.equals("first")) 
			{
				pager.first();
			} 
			else if (pagerMethod.equals("previous")) 
			{
				pager.previous();
			} 
			else if (pagerMethod.equals("next")) 
			{
				pager.next();
			} 
			else if (pagerMethod.equals("last")) 
			{
				pager.last();
			}
		}
		//����һ�ε������  ֱ�ӷ���
		return pager;
	}
}
