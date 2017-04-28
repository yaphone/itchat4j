# itchat4j -- 让个人微信号拥有公众号的能力


### 项目地址：[itchat4j](https://github.com/yaphone/itchat4j)，该项目长期维护更新，欢迎start, fork, pull/request, issue。

## 来源

itchat是一个非常优秀的开源微信个人号接口，使用Python语言开发，提供了简单易用的API，可以很方便地对个人微信号进行扩展，实现自动回复，微信挂机机器人等，一直在关注这个项目，基于itchat开发过[一个小项目](https://github.com/yaphone/RasWxNeteaseMusic)，用来控制我的树莓派来播放音乐，效果还不错。

一直想实现一个java版本的itchat，由于工作太忙导致一拖再拖，这段时间稍微空闲了一些，仔细阅读了itchat的源码，终于完成了一个基础版本，由于主要灵感来源于原项目，所以这个项目的就暂时定名为[itchat4j](https://github.com/yaphone/itchat4j)吧。



## 功能介绍

玩法很多，请打开你的脑洞，比如这些：

- Just for fun，把个人微信号扩展为"公众号"，在朋友面前装个X吧。
- 嵌入你的个人应用（SpringMVC、Serverlet、GUI），为应用提供更强的能力。
- 部署在你的服务器上，将监控信息、日志发送到你的微信号。
- 微信机器人，专业陪聊工具
- 控制路由器、智能家居等具有开放接口的玩意儿
- Anything you want ...



## 如何使用

itchat4j是一个Maven项目，下载源码后，可以以Maven项目的形式导入，导入后的项目结构如下图，



【图一】



src/main/java是itchat4j的项目源码，在src/test/java目录下有两个小Demo：一个是基本功能的小示例，当前，itchat4j可以处理四类基本信息，文本、语音、图片和小视频，该示例在收到文本信息后自动回复，回复内容为收到的文本，当收到图片、语音、小视频时可以保存到指定的目录；一个是微信接入图灵机器人的小例子，让你的个人微信号摇身一变，成为一个小小的公众号，百闻不如一见，我把这个图灵机器人的小Demo部署到了我的阿里云服务器上，现在就扫码体验一下吧。

【二维码】



## 详细教程

接下来，让我来一步步教你如何使用itchat4j来扩展你的个人微信号。

我们首先在Eclipse中新建一个项目，首先我们导入itchat4j的jar包，项目的结构如图所示：



图三：项目结构



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

