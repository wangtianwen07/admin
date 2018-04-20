package com.css.mgr.bpm.freeflow.excutor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.css.mgr.base.dao.pojo.IUser;
import com.css.mgr.bpm.freeflow.dao.pojo.CompletionStrategy;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTask;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;
import com.css.mgr.bpm.freeflow.dao.pojo.HandlerState;
import com.css.mgr.bpm.freeflow.dao.pojo.Result;
import com.css.mgr.bpm.freeflow.dao.pojo.TaskState;
import com.css.mgr.bpm.freeflow.dao.repository.FfTaskDao;
import com.css.mgr.bpm.freeflow.dao.repository.FfTaskUserDao;
import com.css.mgr.bpm.freeflow.exception.NoUserException;

/**
 * @author pc
 * 环节处理器，用于处理当前环节通过、退回、取回需要做的事情
 */
@Component
@Transactional
public class UserProcessExcutor {
	@Autowired
	private FfTaskUserDao ffTaskUserDao;
	@Autowired
	private FfTaskDao ffTaskDao;
	/**
	 * @param tfu
	 * @param tc
	 * @param touser 下一环节处理用户
	 */
	public void run(FfTaskUser tfu,List<IUser> touser){
		if(tfu.getResult().equals(Result.RESULT_RETURN)){
			//退回
			reback(tfu);
		}else if(tfu.getResult().equals(Result.RESULT_RETRIEVE)){
			//取回
			retrieve(tfu);
		}else if(tfu.getResult().equals(Result.RESULT_PASS)){
			//通过
			pass(tfu,touser);
		}else if(tfu.getResult().equals(Result.RESULT_END)){
			//通过
			end(tfu);
		}else{
			throw new RuntimeException("未识别的处理结果");
		}
	}
	/**
	 * 环节处理，用于审批通过
	 * @param tfu
	 * @param tc
	 * @param touser 下一环节处理用户
	 */
	public void pass(FfTaskUser tfu,List<IUser> touser){
		if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_ALONE)){
			//各自独立,什么也不做
			if((touser==null || touser.size()<1) && taskIsActive(tfu)){
				throw new NoUserException("当前环节已完成，未选择下一环节处理人！");
			}
			return;
		}else if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_ANY)){
			//删除其他待办
			deleteProcess(tfu);
			if((touser==null || touser.size()<1)  && taskIsActive(tfu)){
				throw new NoUserException("当前环节已完成，未选择下一环节处理人！");
			}
		}else if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_PARALLELL)){
			//并行会签,什么也不做。处理完成下一岗位没有选人
			if(!curProcessAble(tfu) &&(touser==null || touser.size()<1)  && taskIsActive(tfu)){
				throw new NoUserException("当前环节已完成，未选择下一环节处理人！");
			}
		}else if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_SERIAL)){
			//顺序会签,唤醒下一个
			wakeUpProcess(tfu,touser);
		}else{
			throw new RuntimeException("未找到完成策略");
		}
	}
	/**
	 * 环节处理，用于退回
	 * @param tfu
	 */
	public void reback(FfTaskUser tfu){
		if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_ALONE)){
			//什么也不做,各自独立
			return;
		}else if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_ANY)){
			//删除其他待办
			deleteProcess(tfu);
		}else if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_PARALLELL) || tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_SERIAL)){
			//会签为一票否决制，删除其他待办
			deleteProcess(tfu);
		}else{
			throw new RuntimeException("未找到完成策略");
		}
	}
	/**
	 * 环节处理，用于取回
	 * @param tfu 当前用户任务
	 */
	public void retrieve(FfTaskUser tfu){
		if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_ALONE)){
			if(nextComplateAble(tfu)){
				throw new NoUserException("下一环节已处理，不能取回！");
			}
			//删除下一环节其他待办
			deleteNextProcess(tfu);
			return;
		}else if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_ANY)){
			if(nextComplateAble(tfu)){
				throw new NoUserException("下一环节已处理，不能取回！");
			}
			//删除下一环节其他待办
			deleteNextProcess(tfu);
		}else if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_PARALLELL) || tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_SERIAL)){
			if(nextComplateAble(tfu)){
				throw new NoUserException("下一环节已处理，不能取回！");
			}
			//会签为一票否决制,删除下一环节其他待办
			deleteNextProcess(tfu);
		}else{
			throw new RuntimeException("未找到完成策略");
		}
	}
	/**
	 * 环节处理，用于办结
	 * @param tfu 当前用户任务
	 */
	public void end(FfTaskUser tfu){
		if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_ALONE)){
			//什么也不做,各自独立
			return;
		}else if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_ANY)){
			if(curProcessAble(tfu)){
				throw new NoUserException("当前环节还有未处理完成用户不能办结！");
			}
		}else if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_PARALLELL) || tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_SERIAL)){
			if(curProcessAble(tfu)){
				throw new NoUserException("当前环节还有未处理完成用户不能办结！");
			}
		}else{
			throw new RuntimeException("未找到完成策略");
		}
	}
	/**
	 * 下一环节是否已经有处理
	 * @param taskuser 当前环节待办
	 * @return
	 */
	@Transactional(readOnly=true)
	public boolean nextComplateAble(FfTaskUser taskuser){
		//下一环节处理个数
		Integer process=ffTaskUserDao.countByPrevUserProcess(taskuser.getUserProcess(), HandlerState.HANDLERSTATE_COMPLETE, taskuser.getFfTaskUuid());
		return process>0;
	}
	/**
	 * 当前环节是否有待处理
	 * @param taskuser 当前环节待办
	 * @return
	 */
	@Transactional(readOnly=true)
	public boolean curProcessAble(FfTaskUser taskuser){
		//当前环节处理个数
		Integer process=ffTaskUserDao.countByUserProcess(taskuser.getUserProcess(), HandlerState.HANDLERSTATE_PROCESS, taskuser.getFfTaskUuid());
		return process>0;
	}
	/**
	 * 删除非当前任务下一环节进程其他未处理的待办，用于取回
	 * @param tfu 当前用户任务
	 * @param tc
	 */
	private void deleteNextProcess(FfTaskUser tfu) {
		ffTaskUserDao.deleteParentUserProcess(tfu.getUserProcess(), HandlerState.HANDLERSTATE_PROCESS, tfu.getFfTaskUuid(), tfu.getFfTaskUserUuid());
	}
	/**
	 * 删除非当前任务当前环节进程其他未处理的待办,用于通过、退回
	 * @param tfu 当前用户任务
	 * @param tc
	 */
	private void deleteProcess(FfTaskUser tfu) {
		ffTaskUserDao.deleteUserProcess(tfu.getUserProcess(), HandlerState.HANDLERSTATE_PROCESS, tfu.getFfTaskUuid(), tfu.getFfTaskUserUuid());
	}
	
	/**
	 * 任务是否办结
	 * @param tfu
	 * @return 是：true
	 */
	@Transactional(readOnly=true)
	private boolean taskIsActive(FfTaskUser tfu){
		FfTask ft=ffTaskDao.findOne(tfu.getFfTaskUuid());
		return ft.getTaskState().equals(TaskState.TASK_STATE_ACTIVE);
	}
	/**
	 * 唤醒当前环节进程其他未处理的睡眠任务
	 * @param tfu 当前用户任务
	 * @param touser 下一环节处理用户
	 * @param tc
	 */
	private void wakeUpProcess(FfTaskUser tfu,List<IUser> touser) {
		
		List<FfTaskUser> sleep=ffTaskUserDao.findWakeUpProcess(tfu.getUserProcess(), HandlerState.HANDLERSTATE_SLEEP, tfu.getFfTaskUuid(), tfu.getFfTaskUserUuid());
		//
		if((sleep==null || sleep.size()<1)
				&& (touser==null || touser.size()<1)
				&& taskIsActive(tfu)) {
			throw new NoUserException("没有可唤醒的任务，需要选择下一环节用户!");
		}
		FfTaskUser sp=sleep.get(0);
		sp.setHandlerState(HandlerState.HANDLERSTATE_PROCESS);
		ffTaskUserDao.save(sp);
	}
	/**
	 * 环节处理，用于审批通过
	 * @param tfu
	 * @param tc
	 * @param touser 下一环节处理用户
	 */
	@Transactional(readOnly=true)
	public boolean nextAble(FfTaskUser tfu){
		if(tfu.getCompletionStrategy().equals(CompletionStrategy.COMPLETION_STRATEGY_ALONE)){
			//什么也不做,各自独立
			return true;
		}else{
			if(curProcessAble(tfu)){
				return false; 
			}else{
				return true;
			}
		}
	}
}
