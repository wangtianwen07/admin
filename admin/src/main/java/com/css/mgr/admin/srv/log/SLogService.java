/**
 * Date:2018年1月17日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.srv.log;

import com.css.mgr.base.dao.pojo.SLog;
import com.css.mgr.base.dao.pojo.SSys;
import net.sf.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Date:2018年1月17日
 * Description:
 * Author:litao
 */
public interface SLogService {

    Page<SLog> findAll(JSONObject paramJson, Pageable pageable);

    //条件、分页查询
    Page<SLog> listSerach(JSONObject paramJson, String searchValue, Pageable pageable);

    //批量添加日志
    List<SLog> save(List<SLog> sLogList);

    //添加日志
    SLog save(SLog sLog);




}
