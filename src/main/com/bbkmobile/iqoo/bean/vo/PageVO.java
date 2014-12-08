/**
 * 
 */
package com.bbkmobile.iqoo.bean.vo;


/**
 * @author wangbo
 * @version 1.0.0.0/2011-7-28
 */
public class PageVO {

	public final static Integer[] NUMPERPAGE_CONFIG_DEF=new Integer[]{10,20,50,100};;//默认可以选择的每页条数
	public final static Integer MAX_NUMPERPAGE_CONFIG=500;//每页支持最大条数
	private Integer pageCount; // 页数
	private Integer recordCount; // 记录数
	private Integer currentPageNum=1; // 当前页码
	private Integer numPerPage; // 每页的记录数
	/**
	 * @return the pageCount
	 */
	public Integer getPageCount() {
		return pageCount;
	}
	/**
	 * @param pageCount the pageCount to set
	 */
	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
		if(currentPageNum>pageCount){
		    setCurrentPageNum(pageCount);
		}
	}
	/**
	 * @return the recordCount
	 */
	public Integer getRecordCount() {
		return recordCount;
	}
	/**
	 * @param recordCount the recordCount to set
	 */
	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
		setPageCount((recordCount+getNumPerPage()-1)/getNumPerPage());
		
	}
	/**
	 * @return the currentPageNum
	 */
	public Integer getCurrentPageNum() {
		return currentPageNum;
	}
	/**
	 * @param currentPageNum the currentPageNum to set
	 */
	public void setCurrentPageNum(Integer currentPageNum) {
		this.currentPageNum = currentPageNum;
	}
	/**
	 * @return the numPerPage
	 */
	public Integer getNumPerPage() {
	 setNumPerPageDef(10);
		return numPerPage;
	}
	/**
	 * @param numPerPage the numPerPage to set
	 */
	public void setNumPerPage(Integer numPerPage) {
	    if(numPerPage>MAX_NUMPERPAGE_CONFIG){
	        numPerPage=MAX_NUMPERPAGE_CONFIG;
	    }
		this.numPerPage = numPerPage;
	}
	
	public Integer getStartIndex(){
	    return (getCurrentPageNum()-1)*getNumPerPage();
	}
	
	public void setNumPerPageDef(Integer numPerPageDef){
	    if(this.numPerPage==null){
	        this.numPerPage=numPerPageDef;
	    }
	}
	public boolean hasNumPerPageConfig(){
	    Integer myNumPerPage = getNumPerPage();
	    for(Integer numPerPageConfig:NUMPERPAGE_CONFIG_DEF){
	        if(myNumPerPage.intValue() ==numPerPageConfig.intValue()){
	            return true;
	        }
	    }
	    return false;
	}
}
