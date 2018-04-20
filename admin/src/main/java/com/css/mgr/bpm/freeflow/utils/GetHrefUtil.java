package com.css.mgr.bpm.freeflow.utils;

	public class GetHrefUtil {

		/**
		 * 根据环节返回处理路径
		 * @param uuid 业务单id
		 * @param link 环节;对应NewUnionLinkConstant 类中
		 * @return 访问路径
		 */
		public static String getHref(Integer link){
			StringBuffer sb=new StringBuffer();
			switch (link) {
			case NewUnionLinkConstant.SQCS:
				sb.append("getUnionExamine.action");
				break;
			case NewUnionLinkConstant.SQFS:
				sb.append("getUnionExamine2.action");
				break;
			case NewUnionLinkConstant.TJHXR:
				sb.append("getCandidate.action");
				break;
			case NewUnionLinkConstant.HXRCS:
				sb.append("candidateExamine.action");
				break;
			case NewUnionLinkConstant.HXRFS:
				sb.append("candidateExamine2.action");
				break;
			case NewUnionLinkConstant.TJXJJG:
				sb.append("getElection.action");
				break;
			case NewUnionLinkConstant.XJJGCS:
				sb.append("getElectionExamine.action");
				break;
			case NewUnionLinkConstant.XJJGFS:
				sb.append("getElectionExamine2.action");
				break;
			case NewUnionLinkConstant.SHCL:
				sb.append("getPaperExamine.action");
				break;

			default:
				sb.append("getUnion.action");
				break;
			}
			return sb.toString();
		}
		/**
		 * 根据环节返回名称
		 * @param link 环节;对应NewUnionLinkConstant 类中
		 * @return 名称
		 */
		public static String getHrefName(Integer link){
			StringBuffer sb=new StringBuffer();
			switch (link) {
			case NewUnionLinkConstant.SQCS:
				sb.append("请示初审");
				break;
			case NewUnionLinkConstant.SQFS:
				sb.append("请示复审");
				break;
			case NewUnionLinkConstant.TJHXR:
				sb.append("提交候选人");
				break;
			case NewUnionLinkConstant.HXRCS:
				sb.append("候选人初审");
				break;
			case NewUnionLinkConstant.HXRFS:
				sb.append("候选人复审");
				break;
			case NewUnionLinkConstant.TJXJJG:
				sb.append("提交选举结果");
				break;
			case NewUnionLinkConstant.XJJGCS:
				sb.append("选举结果初审");
				break;
			case NewUnionLinkConstant.XJJGFS:
				sb.append("选举结果复审");
				break;
			case NewUnionLinkConstant.SHCL:
				sb.append("审核纸质材料");
				break;
			case NewUnionLinkConstant.END:
				sb.append("完成");
				break;

			default:
				sb.append("新建、换届申请");
				break;
			}
			return sb.toString();
		}
		/**
		 * 根据类型返回名称
		 * @param type 类型;对应CommonConstant 类中
		 * @return 名称
		 */
		public static String getTypeName(String type){
			StringBuffer sb=new StringBuffer();
			switch (type) {
			case CommonConstant.APPLICATION_TYPE_GENERAL:
				sb.append("换届申请");
				break;
			case CommonConstant.APPLICATION_TYPE_NEW:
				sb.append("建会申请");
				break;
			default:
				sb.append("新建、换届申请");
				break;
			}
			return sb.toString();
		}
	}
