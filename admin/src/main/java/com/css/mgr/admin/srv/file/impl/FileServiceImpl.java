package com.css.mgr.admin.srv.file.impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.css.mgr.admin.common.JsonUtil;
import com.css.mgr.admin.dao.pojo.SFile;
import com.css.mgr.admin.dao.repository.SFileDao;
import com.css.mgr.admin.srv.file.IFileService;

import net.coobird.thumbnailator.Thumbnails;
import net.sf.json.JSONObject;

/**
 * 
 * @author wangtianwen
 * 2018年3月2日
 */
@Service("fileService")
@Transactional
public class FileServiceImpl implements IFileService {
     
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private SFileDao sFileDao;
	
	@Value("${file.tomcat.dir}")
	private String fileTomcatdir;

	@Override
	public String saveDB(SFile sFile) {
		logger.info("sFile:",sFile);
		sFile = sFileDao.save(sFile);
		return sFile.getUuid();
	}

	@Override
	public JSONObject delFile(String sfileUuid) {
		if(StringUtils.isEmpty(sfileUuid)) {
			JsonUtil.error("删除失败!");
		}
		sFileDao.delete(sfileUuid);
		return JsonUtil.success("成功", null);
	}

	@Override
	public JSONObject listFile(String bId, String fileBusinessType) {
		if(StringUtils.isEmpty(bId) || StringUtils.isEmpty(fileBusinessType)) {
			JsonUtil.error("参数错误无法获取上传文件，更新失败！");
		}
		List<SFile> files = sFileDao.findByBIdAndFileBusinessType(bId,fileBusinessType);
		return JsonUtil.success("成功", files);
	}

	@Override
	public SFile findByUuid(String uuid) {
		return sFileDao.findOne(uuid);
	}

	@Override
	public JSONObject syncBId(String bId, String[] sfileUuids) {
		if(StringUtils.isEmpty(bId)) {
			JsonUtil.error("参数错误无法同步文件！");
		}
		if(sfileUuids == null || sfileUuids.length < 1) {
			JsonUtil.error("参数错误无法同步文件！");
		}
		List<String> fIds = Arrays.asList(sfileUuids);
		List<SFile> files = sFileDao.findAll(fIds);
		for(SFile file:files) {
			file.setbId(bId);
		}
		files = sFileDao.save(files);
		return JsonUtil.success("成功");
	}

	@Override
	public void saveDisk(MultipartFile file,String filePath,String fileName) throws IllegalStateException, IOException {
		File f = new File(filePath, fileName);
		if (f.exists()) {
			// 转存文件
			file.transferTo(f);
		} else {
			f.getParentFile().mkdirs();
			f.createNewFile();
			file.transferTo(f);
		}

	}

	@Override
	public List<SFile> generateThumbnails(String fileUuids,String fileLocaldir,int height) {
		List<SFile> orginalPics = sFileDao.findAll(Arrays.asList(fileUuids.split(",")));
		try {
			for(SFile orginalPic : orginalPics) {
				File file = new File(fileLocaldir+File.separator+orginalPic.getFileBusinessType()+ File.separator + "Thumbnails", orginalPic.getMd5()+orginalPic.getExtName());
				if(!file.exists()) file.getParentFile().mkdirs();
				Thumbnails.of(new File(fileLocaldir+File.separator+orginalPic.getUrl()))
					.height(height).keepAspectRatio(true)
						.toFile(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return orginalPics;
	}

	@Override
	public SFile findThumbnailByUuid(String uuid) {
		return sFileDao.findThumbnailByUuid(uuid);
	}

	@Override
	public String findVideoURL(String uuid) {
		try {
			return  File.separator + fileTomcatdir + File.separator + sFileDao.findOne(uuid).getUrl();
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public JSONObject getFile(String uuid) {
		return JsonUtil.success("获取文件成功", sFileDao.findOne(uuid));
	}


	
}