package com.css.mgr.bpm.freeflow.srv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.css.mgr.admin.common.IDUtil;
import com.css.mgr.admin.dao.repository.SOrgDao;
import com.css.mgr.admin.dao.repository.SUserDao;
import com.css.mgr.base.CssController;
import com.css.mgr.base.dao.pojo.IUser;
import com.css.mgr.base.dao.pojo.SOrg;
import com.css.mgr.base.dao.pojo.SUser;
import com.css.mgr.bpm.freeflow.adapter.FfUserService;
import com.css.mgr.bpm.freeflow.adapter.IFfUserService;
import com.css.mgr.bpm.freeflow.dao.pojo.AutoPass;
import com.css.mgr.bpm.freeflow.dao.pojo.CompletionStrategy;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTask;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;
import com.css.mgr.bpm.freeflow.dao.pojo.HandlerState;
import com.css.mgr.bpm.freeflow.dao.pojo.Result;
import com.css.mgr.bpm.freeflow.dao.pojo.TaskParam;
import com.css.mgr.bpm.freeflow.dao.repository.FfTaskDao;
import com.css.mgr.bpm.freeflow.dao.repository.FfTaskUserDao;
import com.css.mgr.bpm.freeflow.exception.FreeFlowException;
import com.css.mgr.bpm.freeflow.excutor.UserProcessExcutor;
import com.css.mgr.bpm.freeflow.handler.HandlerAdapter;
import com.css.mgr.bpm.freeflow.handler.Nextor;
import com.css.mgr.bpm.freeflow.utils.DateUtils;

//@Service("taskUserService")
@Service
@Transactional(readOnly=true)
public class TaskUserService {
	@Autowired
	private IFfUserService ffUserService;
	@Autowired
	private FfTaskUserDao ffTaskUserDao;
	@Autowired
	private FfTaskDao ffTaskDao;
	@Autowired
	private SUserDao sUserDao;
	@Autowired
	private UserProcessExcutor userProcessExcutor;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HandlerAdapter handlerAdapter;
	@Autowired
	private SOrgDao sOrgDao;

	/**
	 * 五、审批通过。.并持久化
	 *
	 *
	 *
	 * @param ffTaskUserUuid
	 *            待办ID
	 * @param param
	 *            处理参数
	 * @param user
	 *            处理用户
	 * @param touser
	 *            下一岗位处理人
	 * @param tc
	 *            事务
	 * @param limitedTime
	 *            限办时间
	 */
	@Transactional
	public void pass(String ffTaskUserUuid, IUser user, List<IUser> touser, TaskParam param) {
		//
		FfTaskUser taskuser = ffTaskUserDao.findOne(ffTaskUserUuid);
		//
		if (!taskuser.getHandlerState().equals(HandlerState.HANDLERSTATE_PROCESS)) {
			throw new FreeFlowException("当前任务不处于活动状态不能通过！");
		}
		// 完成当前用户
		FfTaskUser tfu = getPass(ffTaskUserUuid, param.getOpinion(), user);
		// 当前环节处理结果名称
		if (StringUtils.isNotEmpty(param.getResultText())) {
			tfu.setResultText(param.getResultText());
		}
		ffTaskUserDao.save(tfu);
		// 处理当前其他环节待办、睡眠任务
		userProcessExcutor.run(tfu, touser);
		// 执行事件
		Nextor contin = handlerAdapter.run(ffTaskUserUuid);
		// 当前环节处理结果名称
		if (StringUtils.isNotEmpty(contin.getCurrentResultText())) {
			tfu.setResultText(contin.getCurrentResultText());
			ffTaskUserDao.save(tfu);
		}
		// 办结
		if (!contin.isNextAble()) {
			// 办结处理
			taskService.end(ffTaskUserUuid, param.getOpinion(), user);
			return;
		} else {
			// 下一环节名称
			if (StringUtils.isNotEmpty(contin.getNextUserProcessName())) {
				param.setUserProcessName(contin.getNextUserProcessName());
			}
			// 下一环节完成策略
			if (StringUtils.isNotEmpty(contin.getNextCompletionStrategy())) {
				param.setCompletionStrategy(contin.getNextCompletionStrategy());
			}
			// 自动完成
			if (contin.isNextAutoPass()) {
				param.setAutoPass(AutoPass.AUTO_PASS_Y);
			}
			if (contin.getNextLimitedTime() != null) {
				param.setLimitedTime(contin.getNextLimitedTime());
			}

		}
		// 如未设置
		if (StringUtils.isEmpty(param.getCompletionStrategy())) {
			param.setCompletionStrategy(tfu.getCompletionStrategy());
		}
		// 是否继续执行、进行下一步
		if (userProcessExcutor.nextAble(tfu)) {
			// 分发下一任务
			String userProcess = IDUtil.getId();
			//
			toProcessTaskUser(touser, tfu.getFfTaskUuid(), param, userProcess, tfu.getUserProcess(), tfu);
		}
	}

	/**
	 * 审批通过 不指定下一环节审批人，用于办结
	 * 
	 * @param ffTaskUserUuid
	 *            待办ID
	 * @param user
	 *            处理用户
	 * @param opinion
	 *            处理意见
	 * @param tc
	 *            事务
	 * @param limitedTime
	 *            处理时限
	 */
	@Transactional
	public void end(String ffTaskUserUuid, String opinion, IUser user) {
		FfTaskUser taskuser = ffTaskUserDao.findOne(ffTaskUserUuid);
		FfTaskUser tfu = null;
		//
		if (taskuser.getHandlerState().equals(HandlerState.HANDLERSTATE_PROCESS)) {
			// throw new FreeFlowException("当前任务不处于活动状态不能办结！");
			// 未结束
			tfu = getPass(ffTaskUserUuid, opinion, user);
			tfu.setResult(Result.RESULT_END);
			ffTaskUserDao.save(tfu);
			// 执行事件
			handlerAdapter.run(ffTaskUserUuid);
		} else {
			// 已结束
			if (taskuser.getResult() != null && taskuser.getResult().equals(Result.RESULT_PASS)) {
				tfu = getPass(ffTaskUserUuid, opinion, user);
				tfu.setResult(Result.RESULT_END);
				ffTaskUserDao.save(tfu);
			} else {
				// 已结束
				// log.debug("任务已经束，无须再作处理!");
				throw new FreeFlowException("任务已结束，不能再作处理!");
			}
		}
		//
		// 处理当前其他环节待办、睡眠任务
		userProcessExcutor.run(tfu, null);
	}

	/**
	 * 根据用户生成待办 .并持久化
	 *
	 * @param user
	 *            待办任务用户
	 * @param ffTaskUuid
	 *            任务ID
	 * @param userProcess
	 *            环节进程
	 * @param param
	 *            任务设置
	 * @param tc
	 *            事务
	 * @param prevUserProcess
	 * @param order
	 *            处理顺序
	 * @return
	 */
	@Transactional
	public FfTaskUser toProcessTaskUser(IUser user, String ffTaskUuid, TaskParam param, String userProcess,
			String prevUserProcess, int order, FfTaskUser pretfu) {
		// 如果存在就不再分配
		if (existsProcessTask(user, ffTaskUuid, userProcess, param, pretfu)) {
			return null;
		}
		// 验证
		FfTaskUser.validateSrategy(param.getCompletionStrategy());
		//
		FfTaskUser tfu = new FfTaskUser();
		tfu.setFfTaskUuid(ffTaskUuid);
		if (ffUserService.isSUser(user)) {
			SOrg org = sOrgDao.findOne(user.getOrgId());
			tfu.setOrgName(org.getOrgName());
			tfu.setOrgUuid(user.getOrgId());
		}
		tfu.setHandlerUserId(user.getUuid());
		tfu.setHandlerUserName(user.getRealName());
		tfu.setPrevUserProcess(prevUserProcess);
		tfu.setUserProcess(userProcess);
		if (pretfu != null) {
			tfu.setParentUserProcess(pretfu.getUserProcess());// 父节点，默认为上一节点环节
		} else {
			tfu.setParentUserProcess(null);
		}
		tfu.setUserProcessName(param.getUserProcessName());
		tfu.setCompletionStrategy(param.getCompletionStrategy());
		if (param.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_SERIAL)) {
			if (order == 1) {
				tfu.setHandlerState(HandlerState.HANDLERSTATE_PROCESS);
			} else {
				// 按顺序激活
				tfu.setHandlerState(HandlerState.HANDLERSTATE_SLEEP);
			}
		}
		tfu.setUserBusiType(ffUserService.getCurUserType(user));
		tfu.setUserOrder(order);
		tfu.setLimitedTime(param.getLimitedTime());
		// 自动完成
		tfu.setAutoPass(param.getAutoPass());
		//
		ffTaskUserDao.save(tfu);
		return tfu;
	}

	/**
	 *
	 * 当前环节，当前用户，当前任务不存在重复
	 *
	 */
	private boolean existsProcessTask(IUser user, String ffTaskUuid, String userProcess, TaskParam param,
			FfTaskUser retfu) {
		List<String> stae = new ArrayList<String>();
		stae.add(HandlerState.HANDLERSTATE_PROCESS);
		stae.add(HandlerState.HANDLERSTATE_SLEEP);
		// 下一环节处理个数
		long process = ffTaskUserDao.count(new Specification<FfTaskUser>() {
			@Override
			public Predicate toPredicate(Root<FfTaskUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				list.add(cb.equal(root.get("ffTaskUuid").as(String.class), ffTaskUuid));
				list.add(cb.equal(root.get("handlerUserId").as(String.class), user.getUuid()));
				list.add(cb.in(root.get("handlerState").in(stae)));
				//
				if (!param.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_ALONE)) {
					list.add(cb.equal(root.get("userProcess").as(String.class), userProcess));
				}
				if (retfu != null) {
					list.add(cb.notEqual(root.get("ffTaskUserUuid").as(String.class), retfu.getFfTaskUserUuid()));
				}
				Predicate[] p = new Predicate[list.size()];
				return cb.and(list.toArray(p));
			}
			
		});
		System.out.println(process);
		return process > 0;
	}

	/**
	 * 根据用户生成待办 .并持久化，默认独立处理
	 *
	 * @param users
	 *            用户组
	 * @param ffTaskUuid
	 *            任务ID
	 * @param userProcess
	 *            环节进程
	 * @param tc
	 *            事务
	 * @param limitedTime
	 *            限办时间
	 * @param completionStrategy
	 *            完成策略
	 * @return
	 */
	public List<FfTaskUser> toProcessTaskUser(List<IUser> users, String ffTaskUuid, TaskParam param, String userProcess,
			String prevUserProcess, FfTaskUser pretfu) {
		List<FfTaskUser> ls = new ArrayList<FfTaskUser>();
		int i = 0;
		for (IUser u : users) {
			if (param.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_SERIAL)) {
				i++;
			} else {
				i = 1;
			}
			ls.add(toProcessTaskUser(u, ffTaskUuid, param, userProcess, prevUserProcess, i, pretfu));
		}
		return ls;
	}

	/**
	 * 将待办改成审批通过任务，并填写意见,不持久化
	 * 
	 * @param ffTaskUserUuid
	 *            待办ID
	 * @param opinion
	 *            意见
	 * @param user
	 *            处理用户
	 * @return
	 */
	public FfTaskUser getPass(String ffTaskUserUuid, String opinion, IUser user) {
		FfTaskUser tfu = getPass(ffTaskUserUuid, user);
		tfu.setOpinion(opinion);
		return tfu;
	}

	/**
	 * 将待办改成审批通过任务，但不填写意见,不持久化
	 * 
	 * @param ffTaskUserUuid
	 *            待办ID
	 * @param user
	 *            处理用户
	 * @return
	 */
	public FfTaskUser getPass(String ffTaskUserUuid, IUser user) {
		FfTaskUser tfu = ffTaskUserDao.findOne(ffTaskUserUuid);
		if (tfu == null) {
			throw new RuntimeException("未找到待办流程");
		}
		tfu.setHandlerState(HandlerState.HANDLERSTATE_COMPLETE);
		tfu.setEndTime(new Date());
		tfu.setResult(Result.RESULT_PASS);
		return tfu;
	}

	/**
	 * 六、退回。
	 * 
	 * @param ffTaskUserUuid
	 *            当前环节任务ID
	 * @param opinion
	 *            处理意见
	 * @param user
	 *            当前处理人
	 * @param tc
	 *            数据事务
	 */
	@Transactional
	public void reback(String ffTaskUserUuid, String opinion, IUser user) {
		FfTaskUser taskuser = ffTaskUserDao.findOne(ffTaskUserUuid);
		if (!taskuser.getHandlerState().equals(HandlerState.HANDLERSTATE_PROCESS)) {
			throw new FreeFlowException("当前任务不处于活动状态不能退回！");
		}
		if (taskuser.getResult().equals(Result.RESULT_RETURN)) {
			throw new FreeFlowException("退回任务不能退回！");
		}
		// 完成当前用户
		FfTaskUser tfu = getPass(ffTaskUserUuid, opinion, user);
		tfu.setResult(Result.RESULT_RETURN);
		ffTaskUserDao.save(tfu);
		// 执行事件
		handlerAdapter.run(ffTaskUserUuid);
		// 处理当前其他环节待办、睡眠任务
		userProcessExcutor.run(tfu, null);
		// 分配下一待办
		List<IUser> touser = getPreviousUser(tfu);
		String userProcess = IDUtil.getId();

		TaskParam param = new TaskParam(opinion);
		// 上一环节
		List<FfTaskUser> ls = getPreviousTaskUser(tfu);
		/*
		 * 退回时，为后退一环节的prevUserProcess 连续后退时为上一岗的上一岗
		 */
		String prevUserProcess = ls.get(0).getPrevUserProcess();
		// 完成策略
		param.setCompletionStrategy(ls.get(0).getCompletionStrategy());
		param.setUserProcessName(ls.get(0).getUserProcessName());
		// 生成待办
		toProcessTaskUser(touser, tfu.getFfTaskUuid(), param, userProcess, prevUserProcess, tfu);
	}

	/**
	 * 得到上一环节处理人
	 * 
	 * @param taskuser
	 *            当前环节待办
	 * @return
	 */
	private List<FfTaskUser> getPreviousTaskUser(FfTaskUser taskuser) {

		if (StringUtils.isEmpty(taskuser.getPrevUserProcess())) {
			// 没有上一步
			throw new RuntimeException("无上一环节,不允许退回!");
		}
		// 上一环节处理人
		return ffTaskUserDao.findByUserProcessAndFfTaskUuid(taskuser.getPrevUserProcess(), taskuser.getFfTaskUuid());
	}
	public List<IUser> getPreviousUser(FfTaskUser taskuser) {
		List<IUser> users = new ArrayList<IUser>();
		List<FfTaskUser> ls = getPreviousTaskUser(taskuser);
		for (FfTaskUser ftu : ls) {
			users.add(getUserByTaskUser(ftu));
		}
		return users;
	}
	private IUser getUserByTaskUser(FfTaskUser ftu) {
		IUser user = ffUserService.getIUser(ftu.getHandlerUserId(), ftu.getUserBusiType());
		if (user == null) {
			throw new FreeFlowException("未找到可解析的用户[" + ftu.getUserBusiType() + ftu.getHandlerUserId() + "]");
		}
		return user;
	}

	//
	/**
	 * 七、取回。 规则：未处理不能取回
	 *
	 *
	 */
	@Transactional
	public void retrieve(String ffTaskUserUuid, String opinion, IUser user) {

		FfTaskUser tfu = ffTaskUserDao.findOne(ffTaskUserUuid);
		//
		if (!tfu.getHandlerState().equals(HandlerState.HANDLERSTATE_COMPLETE)) {
			throw new FreeFlowException("当前任务处于活动状态不需取回！");
		}
		if (tfu.getResult().equals(Result.RESULT_RETRIEVE)) {
			throw new FreeFlowException("取回任务不能取回！");
		}
		if (tfu.getResult().equals(Result.RESULT_END)) {
			throw new FreeFlowException("已完成任务不能取回！");
		}
		// 当前用户生成取回任务待办
		String userProcess = tfu.getUserProcess();
		TaskParam param = new TaskParam(opinion);
		// 完成策略
		param.setCompletionStrategy(tfu.getCompletionStrategy());
		param.setUserProcessName(tfu.getUserProcessName());
		// 生成(取回)待办
		FfTaskUser mytfu = toProcessTaskUser(user, tfu.getFfTaskUuid(), param, userProcess, 
				tfu.getPrevUserProcess(), 1, tfu);
		// 完成(取回)待办
		FfTaskUser complate = getPass(mytfu.getFfTaskUserUuid(), user);
		complate.setResult(Result.RESULT_RETRIEVE);
		ffTaskUserDao.save(complate);
		// 处理当前其他环节待办、睡眠任务
		UserProcessExcutor upe = new UserProcessExcutor();
		upe.run(complate, null);
		// 执行事件
		handlerAdapter.run(complate.getFfTaskUserUuid());
		// 生成新的待办
		// userProcess= UuidUtil.getUuid();
		toProcessTaskUser(user, tfu.getFfTaskUuid(), param, userProcess,tfu.getPrevUserProcess(), 1, mytfu);
	}

	/**
	 * 八、查看本任务历史处理意见。
	 *
	 *
	 *
	 */
	public List<FfTaskUser> getOpinionByTaskUser(FfTaskUser ftu) {
		return getOpinionByTaskUser(ftu, HandlerState.HANDLERSTATE_COMPLETE);
	}

	/**
	 *
	 * 查询当前任务处理人
	 */
	public List<FfTaskUser> getCurrUserByTaskUser(FfTaskUser ftu) {
		return getOpinionByTaskUser(ftu, HandlerState.HANDLERSTATE_PROCESS);
	}
	private List<FfTaskUser> getOpinionByTaskUser(FfTaskUser ftu, String handlerstate) {
		
		return ffTaskUserDao.findByHandlerStateAndFfTaskUuid(handlerstate, ftu.getFfTaskUuid());
	}

	/**
	 * 十、查看待办任务。
	 * 
	 * @param user
	 *            当前用户
	 *
	 *
	 */
	public List<FfTaskUser> getProcessTaskUser(IUser user) {
		return getProcessTaskUser(user, HandlerState.HANDLERSTATE_PROCESS, ffUserService.getCurUserType());
	}

	public List<FfTaskUser> getProcessTaskUser(IUser user, FfTask task) {
		List<FfTaskUser> ls = getProcessTaskUserQc(user, HandlerState.HANDLERSTATE_PROCESS, ffUserService.getCurUserType(), task);
		return ls;
	}

	public List<FfTaskUser> getProcessTaskUserQc(IUser user) {
		return getProcessTaskUserQc(user, HandlerState.HANDLERSTATE_PROCESS, ffUserService.getCurUserType(), null);
	}

	public List<FfTaskUser> getProcessTaskUser(IUser user, String handlerState) {
		return getProcessTaskUser(user, handlerState, ffUserService.getCurUserType());
	}

	public List<FfTaskUser> getAutoPassProcessTaskUserQc(Date limitedTime) {
		//
		return ffTaskUserDao.findByHandlerStateAndAutoPassAndLimitedTime(HandlerState.HANDLERSTATE_PROCESS, AutoPass.AUTO_PASS_Y, limitedTime);
		//
	}

	public List<FfTaskUser> getProcessTaskUserQc(IUser user, String handlerState, String userBusiType, FfTask task) {
		List<FfTaskUser> fls=ffTaskUserDao.findAll(new Specification<FfTaskUser>() {
			@Override
			public Predicate toPredicate(Root<FfTaskUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				if (task != null) {
					list.add(cb.equal(root.get("ffTaskUuid").as(String.class), task.getFfTaskUuid()));
				}
				list.add(cb.equal(root.get("handlerUserId").as(String.class), user.getUuid()));
				list.add(cb.equal(root.get("handlerState").as(String.class), handlerState));
				//list.add(cb.equal(root.get("userBusiType").as(String.class), userBusiType));
				if (handlerState.equals(HandlerState.HANDLERSTATE_COMPLETE)) {
					Order o=cb.desc(root.get("endTime"));
					query.orderBy(o);
				}else{
					Order o=cb.desc(root.get("createTime"));
					query.orderBy(o);
				}
				Predicate[] p = new Predicate[list.size()];
				query.where(list.toArray(p));
				return query.getRestriction();
			}
		});
		//
		return fls;
	}

	public List<FfTaskUser> getProcessTaskUser(IUser user, String handlerState, String userBusiType) {
		//
		List<FfTaskUser> qc = getProcessTaskUserQc(user, handlerState, userBusiType, null);
		return qc;
	}

	/**
	 * 十、查看已办任务。
	 *
	 * @param user
	 *            当前用户
	 *
	 */
	public List<FfTaskUser> getCompleteTaskUser(IUser user) {
		return getProcessTaskUser(user, HandlerState.HANDLERSTATE_COMPLETE, ffUserService.getCurUserType());
	}

	public List<FfTaskUser> getCompleteTaskUserQc(IUser user) {
		return getProcessTaskUserQc(user, HandlerState.HANDLERSTATE_COMPLETE, ffUserService.getCurUserType(), null);
	}

	/**
	 * 查看处理意见
	 * 
	 * @param ffTaskUuid
	 * @param ffTaskUserUuid
	 * @return
	 */
	public List<FfTaskUser> getOpinion(String ffTaskUserUuid) {
		HashMap<String, List<FfTaskUser>> map = getOpinionByTaskUser(ffTaskUserUuid);
		if (map != null && map.size() > 0) {
			return map.get("manOpinion");
		}
		return null;
	}

	/**
	 * 查看处理意见
	 * 
	 * @param ffTaskUuid
	 * @param ffTaskUserUuid
	 * @return
	 */
	public HashMap<String, List<FfTaskUser>> getOpinionByTaskUser(String ffTaskUserUuid) {
		HashMap<String, List<FfTaskUser>> map = new HashMap<String, List<FfTaskUser>>();
		FfTaskUser tfu = ffTaskUserDao.findOne(ffTaskUserUuid);
		FfTask tf = ffTaskDao.findOne(tfu.getFfTaskUuid());
		// 主任务意 见
		List<FfTaskUser> manOpinion = getOpinionByTaskUser(tfu);
		for (FfTaskUser u : manOpinion) {
			u.setEndTimeStr(DateUtils.toStrings(u.getEndTime()));
		}
		map.put("manOpinion", manOpinion);
		// 子任务意见
		// 协办意见
		List<FfTaskUser> subOpinion = taskService.getSubOpinionByTaskUser(tf);
		for (FfTaskUser u : subOpinion) {
			u.setEndTimeStr(DateUtils.toStrings(u.getEndTime()));
		}
		map.put("subOpinion", subOpinion);
		return map;
	}

	/**
	 * 判断该流程的上一步是"退回"还是"通过"，用于"取回"操作判断
	 * 
	 * @param ffTaskUuid
	 * @return
	 */
	@SuppressWarnings("unlikely-arg-type")
	public boolean lastResultIsPass(String ffTaskUuid) {
		List<FfTaskUser> fft=ffTaskUserDao.findByHandlerStateAndFfTaskUuidAndEndTime(ffTaskUuid,HandlerState.HANDLERSTATE_COMPLETE);
		if (fft != null && fft.size() > 0) {
			if (Result.RESULT_PASS.equals(fft.get(0))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 根据审核人返回处理人联系方式
	 * 
	 * @param handlerUserId
	 *            用户ID
	 * @param UserType
	 *            用户类型
	 * @return
	 */
	public String getHandlerUserPhone(String handlerUserId, String UserType) {
		String phone = "";
		if (FfUserService.USERDATATYPE_SUSER.equals(UserType)) {
			SUser user = sUserDao.findOne(handlerUserId);
			if (user != null) {
				phone = user.getPhone();
			}
		}
		return phone;
	}

	/**
	 * 我的待办数量
	 * 
	 * @return
	 */
	public Integer getTaskUserNum() {
		IUser sUser = CssController.getUser();
		List<FfTaskUser> qc = getProcessTaskUserQc(sUser);
		if (qc != null && qc.size() > 0) {
			return qc.size();
		}
		return 0;
	}

	/**
	 * 我的已办数量
	 * 
	 * @return
	 */
	public static Integer getCompleteTaskNum() {
		TaskUserService tus = new TaskUserService();
		IUser sUser =CssController.getUser();;
		List<FfTaskUser> qc = tus.getCompleteTaskUserQc(sUser);
		if (qc != null && qc.size() > 0) {
			return qc.size();
		}
		return 0;
	}
	public FfTaskUser get(String taskUserUuid){
		return ffTaskUserDao.findOne(taskUserUuid);
	}

}
