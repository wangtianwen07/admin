package com.css.mgr.admin.freeflow;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;
//import com.css.mgr.bpm.freeflow.srv.GhZzApplyTradeService;
//import com.css.mgr.bpm.freeflow.dao.pojo.GhZzApplyTrade;
import com.css.mgr.bpm.freeflow.handler.IUserProcessHandler;
import com.css.mgr.bpm.freeflow.handler.Nextor;

@Component
@Transactional(readOnly=true)
public class TestUserHandler implements IUserProcessHandler {

	@Override
	public Nextor excutor(FfTaskUser ftu, String title, String id) throws Exception {
		return null;
	}
}
	/*  
	@Autowired
	private GhZzApplyTradeService ghZzApplyTradeService;
	@Override
	public Nextor excutor(FfTaskUser ftu, String title, String id) throws Exception {
		Nextor nextor=new Nextor();
		GhZzApplyTrade at=ghZzApplyTradeService.get(id);
		System.out.println("================当前环节："+GetHrefUtil.getHrefName(at.getLink()));
		if(ftu != null){
			if(NewUnionLinkConstant.SHCL>at.getLink() && Result.RESULT_PASS.equals(ftu.getResult())){
				setResult(at,nextor);
				at.setLink(at.getLink()+1);
				//tc.update(at);
				System.out.println("================下一环节："+GetHrefUtil.getHrefName(at.getLink()));
				nextor.setNextAble(true);
				//设置下一环节任务名称
				nextor.setNextUserProcessName(GetHrefUtil.getHrefName(at.getLink()));

				return nextor;
			}
			if(NewUnionLinkConstant.SQCS<=at.getLink() && (Result.RESULT_RETURN.equals(ftu.getResult())
					|| Result.RESULT_RETRIEVE.equals(ftu.getResult()) )){
				at.setLink(at.getLink()-1);
				//tc.update(at);
				System.out.println("================下一环节："+GetHrefUtil.getHrefName(at.getLink()));
				nextor.setNextAble(true);
				//退回不用设
				//nextor.setNextUserProcessName(GetHrefUtil.getHrefName(at.getLink()));
				return nextor;
			}
			if(NewUnionLinkConstant.SQCS<=at.getLink() && Result.RESULT_END.equals(ftu.getResult())){
				at.setLink(NewUnionLinkConstant.END);
				tc.update(at);
				nextor.setNextAble(false);
				throw new Exception("建会流程不能进行办结操作");


			}
			//最后一个环节；审核通过
			if(NewUnionLinkConstant.SHCL == at.getLink() && Result.RESULT_PASS.equals(ftu.getResult())){
				//设置为完结
				at.setLink(NewUnionLinkConstant.END);
				nextor.setNextAble(false);
				//结束不用设
				//nextor.setNextUserProcessName(GetHrefUtil.getHrefName(at.getLink()));
				return nextor;//结束流程
			}
	}
		return nextor;
	}*/
	
//	 提交环节；结果信息为 提交成功
//	 @param at
//	 @param nextor
/*	 
	private void setResult(GhZzApplyTrade at, Nextor nextor) {
		//提交环节；结果信息为 提交成功
		if(NewUnionLinkConstant.TJSQ == at.getLink() || NewUnionLinkConstant.TJHXR == at.getLink()
				|| NewUnionLinkConstant.TJXJJG == at.getLink()){
			nextor.setCurrentResultText("提交成功");
		}


	}
}
*/