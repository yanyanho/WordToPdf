package com.netqin.util.wordToPdf.service.impl;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.netqin.util.wordToPdf.WTopTools;
import com.netqin.util.wordToPdf.service.IPutFileToLinuxService;
import com.netqin.util.wordToPdf.service.IPutFileToWindowsService;

public class PutFileToLinuxServiceImpl implements IPutFileToLinuxService {


	private  final Logger logger = Logger
			.getLogger(PutFileToLinuxServiceImpl.class);
	
	@Autowired
	private IPutFileToWindowsService putFileToWindowsService;
	
	
	@Override
	public void putFileToLinux() {

	}

	@Override
	public void NewFileArrival(String fileName,Long length,String compKey) {
		File f = new File(WTopTools.NFS_PATH+compKey+"/word/"+fileName);
		logger.info("收到需要转换的word文件:"+fileName);
		if(f.exists()){
			if(f.length() == length){
				WTopTools.receiveFile(fileName,putFileToWindowsService,compKey);
			}else{
				logger.error("收到的文件:"+fileName+",windows大小:"+f.length()+",服务器大小:"+length+",大小不一致.");
				putFileToWindowsService.handleReceiveFailed(fileName,compKey);
			}
		}else{
			logger.error("windows服务器上没有找到文件:"+fileName);
			putFileToWindowsService.handleReceiveFailed(fileName,compKey);
		}
	}

	@Override
	public void putFileToLinuxAgain(String fileName,String compKey) {
		WTopTools.receiveFile(fileName,putFileToWindowsService,compKey);
	}

}
