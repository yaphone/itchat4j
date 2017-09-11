package com.yachat.wechat.message;

public class TextMessageResponse implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private TextMessageBaseResponse BaseResponse;
	private String MsgID;
	private String LocalID;

	public TextMessageBaseResponse getBaseResponse() {
		return BaseResponse;
	}

	public void setBaseResponse(TextMessageBaseResponse baseResponse) {
		BaseResponse = baseResponse;
	}

	public String getMsgID() {
		return MsgID;
	}

	public void setMsgID(String msgID) {
		MsgID = msgID;
	}

	public String getLocalID() {
		return LocalID;
	}

	public void setLocalID(String localID) {
		LocalID = localID;
	}
	
	public boolean isSuccess() {
		return this.getBaseResponse() != null ? this.getBaseResponse().getRet() == 0 : false;
	}

	public class TextMessageBaseResponse {
		private int Ret;
		private String ErrMsg;

		public int getRet() {
			return Ret;
		}

		public void setRet(int ret) {
			Ret = ret;
		}

		public String getErrMsg() {
			return ErrMsg;
		}

		public void setErrMsg(String errMsg) {
			ErrMsg = errMsg;
		}
	}
}
