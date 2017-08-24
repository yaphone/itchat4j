package com.yachat.wechat.http;

public class Response<T> {

	private boolean success;
	private T data;
	private Exception ex;

	public Response(boolean success, T data) {
		super();
		this.success = success;
		this.data = data;
	}

	public Exception getEx() {
		return ex;
	}

	private void setEx(Exception ex) {
		this.ex = ex;
	}

	public boolean isSuccess() {
		return success;
	}

	public T getData() {
		return data;
	}

	public static <T> Response<T> success() {
		return success(null);
	}

	public static <T> Response<T> success(T data) {
		return new Response<T>(true, data);
	}

	public static <T> Response<T> error() {
		return error(null);
	}

	public static <T> Response<T> error(T data) {
		return new Response<T>(false, data);
	}

	public static <T> Response<T> error(Exception exception) {
		Response<T> response = error(null);
		response.setEx(exception);
		return response;
	}

}
