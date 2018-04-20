package com.css.mgr.bpm.freeflow.srv;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.css.mgr.admin.common.IDUtil;
import com.css.mgr.admin.dao.repository.SOrgDao;
import com.css.mgr.base.dao.pojo.IEntity;
import com.css.mgr.base.dao.pojo.IUser;
import com.css.mgr.base.dao.pojo.SOrg;
import com.css.mgr.bpm.freeflow.adapter.IFfUserService;
import com.css.mgr.bpm.freeflow.dao.pojo.CompletionStrategy;
import com.css.mgr.bpm.freeflow.dao.pojo.FfDef;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTask;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;
import com.css.mgr.bpm.freeflow.dao.pojo.HandlerState;
import com.css.mgr.bpm.freeflow.dao.pojo.Result;
import com.css.mgr.bpm.freeflow.dao.pojo.TaskParam;
import com.css.mgr.bpm.freeflow.dao.pojo.TaskState;
import com.css.mgr.bpm.freeflow.dao.repository.FfTaskDao;
import com.css.mgr.bpm.freeflow.dao.repository.FfTaskUserDao;
import com.css.mgr.bpm.freeflow.exception.FreeFlowException;
import com.css.mgr.bpm.freeflow.exception.NoUserException;

//@Service("taskService")
@Service
@Transactional
public class TaskService {
	// private final String table="FFTASK_BUSI_OBJ";
	/**
	 *
	 * 一、开启流程。并持久化 当前用户生成待办任务。
	 * 
	 * @param entity
	 * @param handleStateName
	 * @param limitedTime
	 * @param emergencyLevel
	 * @param user
	 * @param tc
	 * @return
	 */
	@Autowired
	private IFfUserService ffUserService;
	@Autowired
	private SOrgDao sOrgDao;
	@Autowired
	private FfTaskDao ffTaskDao;
	@Autowired
	private FlowDefService flowDefService;
	@Autowired
	private TaskUserService taskUserService;
	@Autowired
	private FfTaskUserDao ffTaskUserDao;

	public FfTaskUser start(IEntity entity, TaskParam param, IUser user, String defid) {
		List<FfTask> hav = queryFfTaskBybusiObj(entity);
		if (hav != null && hav.size() > 0) {
			throw new FreeFlowException("不允许重复开启任务！");
		}
		// System.out.println(entity.getClass().getSimpleName());
		// 任务信息
		String busiObj = entity.getClass().getSimpleName();
		FfDef def = flowDefService.get(defid);// DictMan.getUuid(table,
												// busiObj);
		String url = def.getUrl();
		if (StringUtils.isEmpty(url)) {
			throw new FreeFlowException("未配置" + "[" + defid + "]");
		}
		FfTask ft = new FfTask();
		ft.setBusiObj(busiObj);
		ft.setFfDefUuid(def.getFfDefUuid());
		ft.setHandleStateName(def.getName());
		ft.setBusiObjUuid(entity.getEntityId());
		ft.setTaskName(entity.getEntityName());
		if (url.indexOf("?") > 0) {
			url = url + "&ffTaskUuid=" + ft.getFfTaskUuid() + "&uuid=" + ft.getBusiObjUuid();
		} else {
			url = url + "?ffTaskUuid=" + ft.getFfTaskUuid() + "&uuid=" + ft.getBusiObjUuid();
		}
		ft.setTaskUrl(url);
		ft.setLimitedTime(param.getLimitedTime());
		ft.setEmergencyLevel(param.getEmergencyLevel());
		// 用户信息
		if (ffUserService.isSUser()) {
			ft.setCreateOrgId(user.getOrgId());
			SOrg org = sOrgDao.findOne(user.getOrgId());
			ft.setCreateOrgName(org.getOrgName());
		}
		ft.setUserBusiType(ffUserService.getCurUserType(user));
		//
		ft.setUserId(user.getUuid());
		ft.setUserName(user.getRealName());
		ffTaskDao.save(ft);
		// 当前用户增加待办处理记录，无须限办时间限
		FfTaskUser ftu = taskUserService.toProcessTaskUser(user, ft.getFfTaskUuid(), param, IDUtil.getId(), null, 1,
				null);
		//
		return ftu;

	}

	/**
	 * 二、办结流程。并持久化 不填写意见
	 * 
	 * @param ffTaskUserUuid
	 *            待办任务ID
	 * @param user
	 *            用户
	 * @param tc
	 *            事务
	 */
	public void end(String ffTaskUserUuid, String opinion, IUser user) {
		FfTaskUser tfu = ffTaskUserDao.findOne(ffTaskUserUuid);
		// 未办结就直接办结，已办结不做处理
		FfTask ft = ffTaskDao.findOne(tfu.getFfTaskUuid());
		if (ft.getTaskState().equals(TaskState.TASK_STATE_ACTIVE)) {
			ft.setTaskState(TaskState.TASK_STATE_END);
			ft.setEndTime(tfu.getEndTime());
			ffTaskDao.save(ft);
		}
		// 添加通过意见
		taskUserService.end(ffTaskUserUuid, opinion, user);

	}

	/**
	 *
	 *
	 * 三、激活流程。并为当前用户生成待办。
	 * 
	 * @param ffTaskUserUuid
	 *            已办结用户任务ID
	 * @param user
	 *            用户
	 * @param tc
	 *            事务
	 */
	public void activation(String ffTaskUserUuid, IUser user) {
		FfTaskUser tfu = ffTaskUserDao.findOne(ffTaskUserUuid);
		FfTask tf = ffTaskDao.findOne(tfu.getFfTaskUuid());
		if (tf == null) {
			throw new FreeFlowException("未找到任务");
		}
		if (tf.getTaskState().equals(TaskState.TASK_STATE_ACTIVE)) {
			throw new FreeFlowException("流程处于激活状态！");
		}
		tf.setTaskState(TaskState.TASK_STATE_ACTIVE);
		tf.setEndTime(null);
		//
		ffTaskDao.save(tf);
		TaskParam param = new TaskParam();
		param.setCompletionStrategy(tfu.getCompletionStrategy());
		param.setUserProcessName(tfu.getUserProcessName());
		// 给当前用户创建一条待办任务,上一用户进程=当前用户任务的上一用户进程
		FfTaskUser ffTaskUser = taskUserService.toProcessTaskUser(user, tf.getFfTaskUuid(), param, tfu.getUserProcess(),
				tfu.getPrevUserProcess(), 1, tfu);
		// 激活待办留痕
		FfTaskUser complate = taskUserService.getPass(ffTaskUser.getFfTaskUserUuid(), "激活流程", user);
		complate.setResult(Result.RESULT_ACTIVE);
		ffTaskUserDao.save(complate);
		// 给当前用户创建一条待办任务
		taskUserService.toProcessTaskUser(user, tf.getFfTaskUuid(), param, IDUtil.getId(), tfu.getPrevUserProcess(), 1,
				complate);
	}

	/**
	 *
	 *
	 * 四、协办子流程分发。 根据主任务、部门创建多个子任务。默认子任务为部门签收岗位待办
	 * 
	 * @param ffTaskUserUuid
	 *            用户待办任务ID
	 * @param orgs
	 *            部门
	 * @param user
	 *            当前用户
	 * @param tc
	 * @param limitedTime
	 *            部门限办时限
	 */
	public void manToSub(String ffTaskUserUuid, List<SOrg> orgs, IUser user, Date limitedTime) {
		FfTaskUser tfu = ffTaskUserDao.findOne(ffTaskUserUuid);
		FfTask tf = ffTaskDao.findOne(tfu.getFfTaskUuid());
		for (SOrg org : orgs) {
			// 创建子任务
			FfTask sub = new FfTask();
			try {
				BeanUtils.copyProperties(sub, tf);
			} catch (Exception e) {
				throw new FreeFlowException(e);
			}
			//
			sub.setFfTaskUuid(IDUtil.getId());
			sub.setEndTime(null);
			sub.setLimitedTime(limitedTime);
			sub.setCreateTime(new Date());
			sub.setMainTaskUuid(tf.getFfTaskUuid());// 主流程ID
			ffTaskDao.save(sub);
			// 部门签收岗
			List<IUser> users = getUserByOrg(org.getOrgId());
			if (users == null || users.size() == 0) {
				throw new NoUserException("部门[" + org.getOrgName() + "]签收岗没有用户!");
			}
			String userProcess = IDUtil.getId();// 环节进程
			// 为部门签收岗创建待办,无办理时限
			TaskParam param = new TaskParam();
			param.setCompletionStrategy(CompletionStrategy.COMPLETION_STRATEGY_ANY);
			taskUserService.toProcessTaskUser(users, sub.getFfTaskUuid(), param, userProcess, null, tfu);
		}
	}

	/**
	 * 得到部门签收岗用户
	 * 
	 * @param dp
	 *            部门ID
	 * @return
	 */
	@Transactional(readOnly=true)
	private List<IUser> getUserByOrg(String dp) {
		List<IUser> is = new ArrayList<IUser>();
		// 部门签收岗
		String postid = ffUserService.getDefaultPost();
		// 查询默认岗位（签收岗）人员
		List<IUser> ls = ffUserService.getUserIdByOrg(postid, dp);
		//
		is.addAll(ls);
		//
		return is;
	}

	/**
	 *
	 *
	 * 十一、根据业务对像查看所有任务。
	 *
	 * @param entity
	 *            业务对像
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<FfTask> queryFfTaskBybusiObj(IEntity entity) {
		return ffTaskDao.findByBusiObjAndBusiObjUuid(entity.getClass().getSimpleName(), entity.getEntityId());
	}

	/**
	 *
	 *
	 * 十一、根据任务查找所有子任务
	 *
	 * @param task
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<FfTask> querySubFfTask(FfTask task) {
		//
		if (task.isMan()) {
			return ffTaskDao.findByMainTaskUuidAndTaskProcess(task.getFfTaskUuid(), task.getTaskProcess());
		} else {
			return ffTaskDao.findByMainTaskUuidAndTaskProcess(task.getMainTaskUuid(), task.getTaskProcess());
		}
		//
	}

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException {
		FfTask src = new FfTask();
		src.setEndTime(new Date());
		FfTask opt = new FfTask();
		BeanUtils.copyProperties(opt, src);
		System.out.println(opt.getEndTime());
	}

	/**
	 * 九、查看协办历史处理意见。
	 *
	 *
	 *
	 */
	@Transactional(readOnly=true)
	public List<FfTaskUser> getSubOpinionByTaskUser(FfTask task) {
		return getSubOpinionByTaskUser(task, HandlerState.HANDLERSTATE_COMPLETE);
	}

	/**
	 * 查看协办当前处理人。
	 * 
	 */
	@Transactional(readOnly=true)
	public List<FfTaskUser> getSubCurrUserByTaskUser(FfTask task) {
		return getSubOpinionByTaskUser(task, HandlerState.HANDLERSTATE_PROCESS);
	}

	/**
	 * 九、查看协办历史处理意见。
	 *
	 *
	 *
	 */
	@Transactional(readOnly=true)
	private List<FfTaskUser> getSubOpinionByTaskUser(FfTask task, String handlerstate) {
		List<FfTask> subTask = querySubFfTask(task);
		if (subTask == null || subTask.size() == 0) {
			return new ArrayList<FfTaskUser>();
		}
		//
		List<String> subTaskIds = new ArrayList<String>();
		for (FfTask ft : subTask) {
			subTaskIds.add(ft.getFfTaskUuid());
		}
		//
		return ffTaskUserDao.findByHandlerStateAndFfTaskUuidIn(handlerstate, subTaskIds);
	}

	/**
	 * 
	 * 查询当前具体任务的用户待办
	 * 
	 */
	@Transactional(readOnly=true)
	public FfTaskUser getFfTaskUser(FfTask task, IUser user) {
		TaskUserService tus = new TaskUserService();
		List<FfTaskUser> ls = tus.getProcessTaskUser(user, task);
		if (ls.size() == 0)
			return null;
		return ls.get(0);
	}

	/**
	 * 
	 * 查询当前具体任务
	 * 
	 */
	@Transactional(readOnly=true)
	public FfTask getFfTask(IEntity entity) {
		List<FfTask> ls = queryFfTaskBybusiObj(entity);
		if (ls.size() == 0)
			return null;
		return ls.get(0);
	}
	@Transactional(readOnly=true)
	public FfTask get(String uuid) {
		return ffTaskDao.findOne(uuid);
	}
}
