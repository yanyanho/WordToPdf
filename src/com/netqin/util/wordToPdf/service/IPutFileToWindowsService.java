package com.netqin.util.wordToPdf.service;



public interface IPutFileToWindowsService {

	public void handlePutFileToWindows(String fileName,String compKey);
	
	public void handleWordToPdf(String contractId,String compKey);
	
	public void handleConvertFinished(String fileName,Long length,String compKey);
	
	/**
	 * 
	* 描述 : <linux发送文件到windows时 windows接受文件失败时,调用的函数>. <br>  
	*<p>                                                   
	                                                                                                                                                                                                        
	* @param fileName
	 */
	public void handleReceiveFailed(String fileName,String compKey);
	
	/**
	 * 
	* 描述 : <windows发送文件到linux时,发送失败,调用的函数>. <br>  
	*<p>                                                   
	                                                                                                                                                                                                        
	* @param fileName
	 */
	public void sendFailed(String fileName);
}
