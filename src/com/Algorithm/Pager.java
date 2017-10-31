package com.Algorithm;


public class Pager //�ٷ�ҳ
{
	private int totalRows; //������
	private int pageSize ; //ÿҳ��ʾ������
	private int currentPage; //��ǰҳ��
	private int startRow; //��ǰҳ�����ݿ��е���ʼ��
	private int totalPages; //��ҳ��
 
	
	public void refresh(int _currentPage) 
	{
		if(1 <= _currentPage && _currentPage <= totalPages){
			currentPage = _currentPage;
			startRow = (currentPage - 1) * pageSize + 1;
		}
			
		else if(_currentPage < 0) {
			_currentPage = 1;
			startRow = 1;
		}
		else if(_currentPage > totalPages){
			currentPage = totalPages;
			startRow = (totalPages - 1) * pageSize + 1;
		}
	}
	public void first() 
	{
		currentPage = 1;
		startRow = 1;
	}
	public void previous() 
	{
		if (currentPage == 1) 
		{
			return;
		}
		else
		{
			currentPage--;
			startRow = (currentPage-1) * pageSize + 1;
		}
	}
	public void next() 
	{
		if (currentPage < totalPages) 
		{
			currentPage++;
			startRow = ((currentPage - 1) * pageSize) + 1;
		}
		else
		{
			return;
		}
	}
	public void last() 
	{
		currentPage = totalPages;
		startRow = ((currentPage - 1) * pageSize) + 1;
	}
	
	public Pager() 
	{
	}
	
	public Pager(int _totalRows,int _pageSize) //ȷ���ּ�ҳ
	{
		totalRows = _totalRows;
		pageSize=_pageSize;
		totalPages=totalRows/pageSize;
		int mod=totalRows%pageSize;
		if(mod>0)
		{
			totalPages++;
		}
		currentPage = 1;
		startRow = 1;
	}
	
	public int getStartRow() 
	{
		return startRow;
	}
	public int getTotalPages() 
	{
		return totalPages;
	}
	public int getCurrentPage()
	{
		return currentPage;
	}
	public int getPageSize() 
	{
		return pageSize;
	}
	public void setTotalRows(int totalRows)
	{
		this.totalRows = totalRows;
	}
	public void setStartRow(int startRow) 
	{
		this.startRow = startRow;
	}
	public void setTotalPages(int totalPages) 
	{
		this.totalPages = totalPages;
	}
	public void setCurrentPage(int currentPage) 
	{
		this.currentPage = currentPage;
	}
	public void setPageSize(int pageSize) 
	{
		this.pageSize = pageSize;
	}
	public int getTotalRows() 
	{
		return totalRows;
	}

	
}
