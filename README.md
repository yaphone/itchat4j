# itchat4j -- 扩展个人微信号的能力

 

### 项目地址：[itchat4j](https://github.com/yaphone/itchat4j)，该项目长期维护更新，欢迎star、fork、 pull requests、 issue。

### 来源

[itchat](https://github.com/littlecodersh/ItChat)是一个非常优秀的开源微信个人号接口，使用Python语言开发，提供了简单易用的API，可以很方便地对个人微信号进行扩展，实现自动回复，微信挂机机器人等，一直在关注这个项目，基于itchat开发过[一个小项目](https://github.com/yaphone/RasWxNeteaseMusic)，用来控制我的树莓派来播放音乐，效果还不错。

一直想实现一个java版本的itchat，由于工作太忙导致一拖再拖，这段时间稍微空闲了一些，仔细阅读了itchat的源码，终于完成了一个基础版本，由于主要灵感来源于原项目，所以这个项目的就暂时定名为[itchat4j](https://github.com/yaphone/itchat4j)吧。



## 项目介绍

> [itchat4j](https://github.com/yaphone/itchat4j)是一个开源的微信个人号接口，使用Java调用微信从未如此简单。使用短短的几十行代码，你就可以完成一个能够处理所有信息的微信机器人。当然，[itchat4j](https://github.com/yaphone/itchat4j)的使用远不止一个机器人，更多的功能等着你来发现，如今微信已经成为了个人社交的很大一部分，希望这个项目能够帮助你扩展你的个人的微信号、方便自己的生活。(引自[itchat](https://github.com/littlecodersh/ItChat)项目)

你可以轻松将其集成在你个人的Java应用中，无论是SpringMVC、桌面程序还是嵌入式程序，只要使用的JDK是1.5以上的版本，都可以轻松接入。玩法很多，请打开你的脑洞，比如这些：

- Just for fun，把个人微信号扩展为"公众号"，在朋友面前装个X吧。
- 集成在你的个人应用（SpringMVC、Serverlet、GUI）中，为应用提供更强的能力。
- 部署在你的服务器上，将监控信息、日志发送到你的微信号。
- 微信机器人，专业陪聊工具
- 控制智能家居、智能硬件等具有开放接口的设备
- Anything you want ...



## 如何使用

itchat4j是一个Maven项目，下载源码后，可以以Maven项目的形式导入，导入后的项目结构如下图:

![itchat4j项目结构](http://oj5vdtyuu.bkt.clouddn.com/itchat4j%E9%A1%B9%E7%9B%AE.png)

src/main/java是itchat4j的项目源码，在src/test/java目录下有两个小Demo：一个是基本功能的小示例，当前，itchat4j可以处理四类基本信息，文本、语音、图片和小视频，该示例在收到文本信息后自动回复，回复内容为收到的文本，当收到图片、语音、小视频时可以保存到指定的目录；一个是微信接入图灵机器人的小例子，让你的个人微信号摇身一变，成为一个小小的公众号，百闻不如一见，我把这个图灵机器人的小Demo部署到了我的阿里云服务器上，现在就扫码体验一下吧。

![二维码](http://oj5vdtyuu.bkt.clouddn.com/%E5%8A%A0%E6%88%91%E5%A5%BD%E5%8F%8B.png)



### 微信机器人使用截图

![微信机器人](http://oj5vdtyuu.bkt.clouddn.com/%E5%BE%AE%E4%BF%A1%E6%9C%BA%E5%99%A8%E4%BA%BA.jpg)

![控制台收到的消息](http://oj5vdtyuu.bkt.clouddn.com/%E6%8E%A7%E5%88%B6%E5%8F%B0%E6%94%B6%E5%88%B0%E7%9A%84%E6%B6%88%E6%81%AF.png)



## 简单入门教程

接下来，通过两个小Demo来演示一下如何使用itchat4j来扩展你的个人微信号，入门教程的项目源码可以从[此处下载](https://github.com/yaphone/itchat4jdemo)。

### Demo1: SimpleDemo

这个小Demo将会将收到的文本消息发送给发件人，如果是图片、语音或者小视频消息，将会保存在我们指定的路径下。

然后我们需要新建一个类来实现`IMsgHandlerFace`这个接口，该接口有四个方法需要实现，`textMsgHandle`用于处理文本信息，`picMsgHandle`用于处理图片信息，`viedoMsgHandle`用于处理小视频信息，`voiceMsgHandle`用于处理语音信息，代码如下：

```java
public class MsgHandler implements IMsgHandlerFace {

	@Override
	public String picMsgHandle(JSONObject arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String textMsgHandle(JSONObject arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String viedoMsgHandle(JSONObject arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String voiceMsgHandle(JSONObject arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
```

由于没有关联源码，所以接口中的参数都变成了`arg0`这种，建议关联一下源码，源码可在release中下载，当然不关联也不会有啥影响，`arg0`其实是我们需要处理的消息，为了更直观，我们把`arg0`修改为`msg`，msg是fastjson的JSONObject类型，这个其实我们不用关心。在我们这个类中，需要实现我们处理消息的逻辑。

在`textMsgHandler`中，我们通过`msg.getString("Text")`就可以获取收到的文本信息，然后作进一步处理，比如接入图灵机器人、消息自动回复等，在这个方法中，我们需要返回一个字符串，即是需要回复的消息，在SimpleDemo这个示例中，我们直接回复收到的原消息。

在`picMsgHandle`、`voiceMsgHandle`、`viedoMsgHandle`这三个方法中，我们需要将这些消息下载下来，然后再作进一步处理，所以需要为每种类型的消息提供一个保存路径，然后调用`DownloadTools.getDownloadFn`方法可以将这三种类型的消息下载下来。

就不多说了，让代码和注释君自述吧，有不明白的地方，可以在Issue中提出来。

```java
/**
 * 简单示例程序，收到文本信息自动回复原信息，收到图片、语音、小视频后根据路径自动保存
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月28日 下午10:50:36
 * @version 1.0
 *
 */
public class SimpleDemo implements IMsgHandlerFace {

	@Override
	public String textMsgHandle(JSONObject msg) {
		String text = msg.getString("Text");
		return text;
	}

	@Override
	public String picMsgHandle(JSONObject msg) {
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String picPath = "D://itchat4j/pic" + File.separator + fileName + ".jpg";
		DownloadTools.getDownloadFn(msg, MsgType.PIC, picPath);
		return "图片保存成功";
	}

	@Override
	public String voiceMsgHandle(JSONObject msg) {
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String voicePath = "D://itchat4j/voice" + File.separator + fileName + ".mp3";
		DownloadTools.getDownloadFn(msg, MsgType.VOICE, voicePath);
		return "声音保存成功";
	}

	@Override
	public String viedoMsgHandle(JSONObject msg) {
		System.out.println(msg);
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String viedoPath = "D://itchat4j/viedo" + File.separator + fileName + ".mp4";
		DownloadTools.getDownloadFn(msg, MsgType.VIEDO, viedoPath);
		return "视频保存成功";
	}

}

```

实现这个消息处理的Handle之后，我们需要将其注入到`Wechat`中来启动服务，`Wechat`是服务的主入口，其构造函数接受两个参数，一个是我们刚才实现的`MsgHandler`类，另一个是保存登陆二维码的路径。然后在`Wechat`对象上调用`start()`方法来启动服务，之后会在我们刚才传入的路径下生成一个`QR.jpg`文件，即是我们的登陆二维码，通过手机微信扫描后即可登陆，实现`IMsgHandlerFace`的类即会实现我们的逻辑，额，说的有点乱，还是让代码君自述吧。

```Java
/**
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月28日 上午12:44:10
 * @version 1.0
 *
 */
public class Mytest {
	public static void main(String[] args) {
		String qrPath = "E://itchat4j";
		IMsgHandlerFace msgHandler = new SimpleDemo();
		Wechat wechat = new Wechat(msgHandler, qrPath);
		wechat.start();
	}

}
```

### Demo2 图灵机器人

> 图灵机器人大脑具备强大的中文语义分析能力，可准确理解中文含义并作出回应，是最擅长聊中文的机器人大脑，赋予软硬件产品自然流畅的人机对话能力。(引自百度百科)

这个示例中我们接入图灵机器人的API，将收到的好友的文本信息发送给图灵机器人，并将机器人回复的消息发送给好友，接下来，让代码和注释君自述吧。

```Java
/**
 * 图灵机器人示例
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月24日 上午12:13:26
 * @version 1.0
 *
 */
public class TulingRobot implements IMsgHandlerFace {

	MyHttpClient myHttpClient = new MyHttpClient();
	String apiKey = "597b34bea4ec4c85a775c469c84b6817"; // 这里是我申请的图灵机器人API接口，每天只能5000次调用，建议自己去申请一个，免费的:)
	Logger logger = Logger.getLogger("TulingRobot");

	@Override
	public String textMsgHandle(JSONObject msg) {
		String result = "";
		String text = msg.getString("Text");
		String url = "http://www.tuling123.com/openapi/api";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("key", apiKey);
		paramMap.put("info", text);
		paramMap.put("userid", "123456");
		String paramStr = JSON.toJSONString(paramMap);
		try {
			HttpEntity entity = myHttpClient.doPost(url, paramStr);
			result = EntityUtils.toString(entity, "UTF-8");
			JSONObject obj = JSON.parseObject(result);
			if (obj.getString("code").equals("100000")) {
				result = obj.getString("text");
			} else {
				result = "处理有误";
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return result;
	}

	@Override
	public String picMsgHandle(JSONObject msg) {

		return "收到图片";
	}

	@Override
	public String voiceMsgHandle(JSONObject msg) {

		return "收到语音";
	}

	@Override
	public String viedoMsgHandle(JSONObject msg) {

		return "收到视频";
	}

	public static void main(String[] args) {
		IMsgHandlerFace msgHandler = new TulingRobot();
		Wechat wechat = new Wechat(msgHandler, "/home/itchat4j/demo/itchat4j/login");
		wechat.start();
	}

}
```

### Demo3 集成在SpringMVC应用中

这个示例要讲起来就比较困难了，因为SpringMVC本身就是一个复杂的东西，先在这里挖个坑吧。其实在SpringMVC中集成与上面两个示例并没有太大的不同，我的个人博客是基于SpringMVC的，我已经将集成在这个项目里了，这样我就可以通过微信来更新我的博客了。详细的就不多说了，大家先看看这个项目结构吧。

![itchat4j集成在Blog项目中](http://oj5vdtyuu.bkt.clouddn.com/itchat4j%E9%9B%86%E6%88%90%E5%9C%A8blog%E9%A1%B9%E7%9B%AE%E4%B8%AD%E4%BF%AE%E6%94%B9.jpg)

其中`MsgHandler`就是我处理微信消息的逻辑，略复杂，就不贴代码了。`WechatService`就是将`MsgHandler`“注入”到`Wechat`类中，与上面两个示例的作用是一样的，贴一下`WechatService`的代码：

```Java
/**
 * Wechat服务实现类
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月29日 下午7:44:01
 * @version 1.0
 *
 */
@Service("wechatService")
public class WechatService implements IWechatServiceFace {
	@Value("${qrPath}")
	private String qrPath;

	@Override
	public void login() {
		MsgHandler msgHandler = new MsgHandler();
		Wechat wechat = new Wechat(msgHandler, qrPath);
		wechat.start();
	}
}

```



## 类似项目

[itchat](https://github.com/littlecodersh/ItChat)优秀的、基于Python的微信个人号API，同时也是本项目的灵感之源。



## 问题和建议

本项目长期更新、维护，功能不断扩展与完善中，欢迎star。

如果有什么问题或者建议都可以在Issue中提出来，也可以加入QQ群讨论：636365179

