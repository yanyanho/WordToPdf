package com.netqin.util.wordToPdf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.netqin.util.wordToPdf.service.IPutFileToWindowsService;


public class WTopTools extends Thread {
	public WTopTools(){}
	private  static final Logger logger = Logger
			.getLogger(WTopTools.class);
	
	public static  String NFS_PATH;
	static {
		logger.info("初始化路径变量");
		Properties prop = new Properties();   
        InputStream in =  WTopTools.class.getClassLoader().getResourceAsStream("/config.properties");
        try {   
            prop.load(in);   
            NFS_PATH = prop.getProperty("nfsPath").trim();   
            logger.info("NFS_PATH:"+NFS_PATH);
        } catch (IOException e) {   
            e.printStackTrace();   
        }  
	}
	private String fileName;
	
	private IPutFileToWindowsService putFileToWindowsService;
	
	private String compKey;

	public IPutFileToWindowsService getPutFileToWindowsService() {
		return putFileToWindowsService;
	}

	public void setPutFileToWindowsService(
			IPutFileToWindowsService putFileToWindowsService) {
		this.putFileToWindowsService = putFileToWindowsService;
	}

	
	public static void receiveFile(String fileName,IPutFileToWindowsService putFileToWindowsService,String compKey){
		WTopTools w2p = new WTopTools(fileName,putFileToWindowsService,compKey);
		w2p.start();
	}
	public void init(String fileName,IPutFileToWindowsService putFileToWindowsService,String compKey) {
		this.fileName = fileName;
		this.putFileToWindowsService = putFileToWindowsService;
		this.compKey=compKey;
	}

	public WTopTools(String fileName,IPutFileToWindowsService putFileToWindowsService,String compKey) {
		this.init(fileName, putFileToWindowsService,compKey);
	}

	public void run() {
		try {
				logger.info("开始转换文件:"+fileName);
				 boolean  flag = word2PDF(NFS_PATH +compKey+"/word/"+ fileName, NFS_PATH +compKey+"/pdf1/"+ fileName);
//				sendToLinux(fileName);
				if(flag){
					 logger.info("文件:" + fileName + "转换完成.通知Linux服务器已完成转换并保存记录.");
						putFileToWindowsService.handleConvertFinished(fileName, new File(NFS_PATH +compKey+"/pdf1/"+ fileName).length(),compKey);

				}else{
					putFileToWindowsService.handleReceiveFailed(fileName, compKey);
				}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
/*
	public  void sendToLinux(String fileName) {
		try {
			logger.info("开始发送文件:" + fileName + "到Linux服务器.");
			CommandResult cr = CommandHelper.exec("cmd.exe /c  D:/bat/put.bat "
					+ fileName+" "+compKey);
			if (org.apache.commons.lang.StringUtils.isNotEmpty(cr.getError())) {
				throw new Exception(cr.getError());
			}
			logger.info("文件:" + fileName + "到Linux服务器.完成.通知Linux服务器已完成转换并保存记录.");
			putFileToWindowsService.handleConvertFinished(fileName, new File(NFS_PATH +compKey+"/pdf1/"+ fileName).length(),compKey);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("windows发送文件到linux失败:", e);
			putFileToWindowsService.sendFailed(fileName);
		}

	}*/

	private  synchronized boolean word2PDF(String docfile, String toFile) {
		logger.info("WTopTools word2PDF begin");
		ActiveXComponent app = new ActiveXComponent("Word.Application");
		try {
			String tempFile = "d:/pdf/"+UUID.randomUUID().toString();
			app.setProperty("Visible", new Variant(false));
			Dispatch docs = app.getProperty("Documents").toDispatch();
			Dispatch doc = Dispatch.invoke(
					docs,
					"Open",
					Dispatch.Method,
					new Object[] { docfile, new Variant(false),
							new Variant(true) }, new int[1]).toDispatch();
			File file1 = new File(toFile);
			File file2 = new File(toFile + ".pdf");
			if (file1.exists()) {
				file1.delete();
			}
			if (file2.exists()) {
				file2.delete();
			}

			Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] {
					tempFile, new Variant(17) }, new int[1]);
			Variant f = new Variant(false);
			Dispatch.call(doc, "Close", f);
			logger.info("转换成功,临时文件:" + tempFile);
			File tmpFile = new File(tempFile+".pdf");
			long tmpFileLength = tmpFile.length();
			tmpFile.renameTo(file1);
//			WTopTools.copyFile(tmpFile,file1);
			
			if(tmpFileLength==file1.length()){
				logger.info("转换成功:" + toFile);
				return true;
			}else{
				logger.info("转换失败:tmpFile和toFile文件大小不一致:tempFile size:" +tmpFile.length()+", toFile size:"+file1.length());
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("转换失败:" + docfile + ",  " + e.getMessage());
		} finally {
			app.invoke("Quit", new Variant[] {});
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		logger.info("WTopTools word2PDF end失败 异常");
		return false;
	}
	 // 复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

	/**                                                           
	 * 描述 : <描述函数实现的功能>. <br> 
	 *<p>                                                  
	                                                                                                                                                                                                       
	 * @param args                                                                                                   
	 */
	public static void main(String[] args) {
		try {
			
//			WTopTools.receiveFile("1.docx",null);
//			Thread.sleep(1000);
//			WTopTools.FILE_PATH="d:\\file\\waps\\";
//			WTopTools.PDF_PATH="d:\\pdf\\waps\\";
//			WTopTools.receiveFile("1.docx",null);
//			Thread.sleep(1000);
//			WTopTools.receiveFile("311ee33c-874c-4e4f-b400-01a296fd0d43",null);
//			Thread.sleep(1000);
//			WTopTools.receiveFile("311ee33c-874c-4e4f-b400-01a296fd0d44",null);
//			Thread.sleep(1000);
//			WTopTools.receiveFile("311ee33c-874c-4e4f-b400-01a296fd0d45",null);
//			Thread.sleep(1000);
//			WTopTools.receiveFile("311ee33c-874c-4e4f-b400-01a296fd0d46",null);
//			Thread.sleep(1000);
//			WTopTools.receiveFile("311ee33c-874c-4e4f-b400-01a296fd0d47",null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
