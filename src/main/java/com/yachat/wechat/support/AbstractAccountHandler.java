package com.yachat.wechat.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yachat.wechat.Account;
import com.yachat.wechat.http.RetryHandler;

public abstract class AbstractAccountHandler<OUT> implements RetryHandler<Account, OUT> {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

}
