/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.css.mgr.admin.dao.pojo.SFile;

/**
 * 
 * @author wangtianwen
 * 2018年3月2日
 */
public interface SFileDao extends JpaRepository<SFile, String>,JpaSpecificationExecutor<SFile> {

	List<SFile> findByBIdAndFileBusinessType(String bId,String fileBusinessType);
	@Query("select f from SFile f,SFile t where t.uuid=?1 and f.md5=t.md5 and f.uuid != t.uuid")
	SFile findThumbnailByUuid(String imgUuid);
}
