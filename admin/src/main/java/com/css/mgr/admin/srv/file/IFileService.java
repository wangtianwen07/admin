package com.css.mgr.admin.srv.file;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.css.mgr.admin.dao.pojo.SFile;

import net.sf.json.JSONObject;

public interface IFileService {

	public void saveDisk(MultipartFile file,String filePath,String fileName) throws IllegalStateException, IOException ;
	public String saveDB(SFile sFile);
	
	public JSONObject delFile(String sfileUuid);
	
	public JSONObject listFile(String bId,String fileBusinessType);
	
	public SFile findByUuid(String uuid);
	
	public JSONObject getFile(String uuid);
	public JSONObject syncBId(String bId,String[] sfileUuids); 
	
	public List<SFile> generateThumbnails(String fileUuids,String fileLocaldir,int height);
	
	public SFile findThumbnailByUuid(String uuid);
	
	//获取视频地址
	public String findVideoURL(String uuid);
}
