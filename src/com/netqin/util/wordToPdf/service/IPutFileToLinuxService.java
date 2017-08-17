package com.netqin.util.wordToPdf.service;

public interface IPutFileToLinuxService {

	public void putFileToLinux();
	
	public void NewFileArrival(String fileName,Long length,String compKey);
	
	public void putFileToLinuxAgain(String fileName,String compKey);
}
