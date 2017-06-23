# itchat4j -- 用Java扩展个人微信号的能力

 

### 项目地址：[itchat4j](https://github.com/yaphone/itchat4j)，该项目长期维护更新，欢迎star、fork、 pull requests、 issue。

## 示例项目程序[点击此处下载](https://github.com/yaphone/itchat4jdemo)。

### 来源

[itchat](https://github.com/littlecodersh/ItChat)是一个非常优秀的开源微信个人号接口，使用Python语言开发，提供了简单易用的API，可以很方便地对个人微信号进行扩展，实现自动回复，微信挂机机器人等，一直在关注这个项目，基于itchat开发过[一个小项目](https://github.com/yaphone/RasWxNeteaseMusic)，用来控制我的树莓派来播放音乐，效果还不错。

一直想实现一个java版本的itchat，由于工作太忙导致一拖再拖，这段时间稍微空闲了一些，仔细阅读了itchat的源码，终于完成了一个基础版本，由于主要灵感来源于itchat项目，所以这个项目的就暂时定名为[itchat4j](https://github.com/yaphone/itchat4j)吧。



## 项目介绍

> itchat是一个开源的微信个人号接口，使用Python调用微信从未如此简单。使用短短的几十行代码，你就可以完成一个能够处理所有信息的微信机器人。当然，itchat的使用远不止一个机器人，更多的功能等着你来发现，如今微信已经成为了个人社交的很大一部分，希望这个项目能够帮助你扩展你的个人的微信号、方便自己的生活。(引自itchat项目)

你可以轻松将[itchat4j](https://github.com/yaphone/itchat4j)其集成在你个人的Java应用中，无论是SpringMVC、桌面程序还是嵌入式程序，只要使用的JDK是1.6以上的版本，都可以轻松接入。玩法很多，请打开你的脑洞，比如这些：

- Just for fun，把个人微信号扩展为"公众号"，在朋友面前装个X吧。
- 集成在你的个人应用（SpringMVC、Servlet、GUI）中，为应用提供更强的服务能力。
- 部署在你的服务器上，将监控信息、日志发送到你的微信号。
- 微信机器人，专业陪聊工具
- 控制树莓派、智能家居、智能硬件等具有开放接口的设备
- Anything you want ...




## 更新日志

- 2017-6-23：增加获取群好友昵称功能，修复已知问题。

- 2017-6-16：增加微信状态维护

- 2017-6-13：修复获取群列表为空问题，增加根据群ID获取群成员列表方法

  ​

## API说明

*项目在不断更新中，API会有变动，请以具体代码为准*

目前在`package cn.zhouyafeng.itchat4j.api`包中有两个静态类，即`MessageTools`和`WechatTools`，目前对外暴露的方法有：

####  1.获取好友列表  WechatTools.getContactList()

此方法会返回好友昵称列表，其函数声明为：

```
public static List<String> getContactList()
```

#### 2.获取群列表 WechatTools.getGroupIdList()

群列表与好友列表不同，在登陆后群列表其实是空的，只有主动发送消息或者收到一条群消息时，才能获取到这个群的信息，群列表会记录这个群的id，其格式为`@@d052d34b9c9228830363013ee53deb461404f80ea353dbdd8fc9391cbf5f1c46`。调用此方法会返回已知的群列表。其声明函数为：

```
public static List<String> getGroupIdList()
```

#### 3.根据群ID获取群成员WechatTools.getMemberListByGroupId()

此方法根据群ID（格式为`@@d052d34b9c9228830363013ee53deb461404f80ea353dbdd8fc9391cbf5f1c46`）获取群成员列表。其函数声明为：

```java
public static JSONArray getMemberListByGroupId(String groupId)
```

#### 4.退出微信 WechatTools.logout()

退出itchat4j，不再处理消息，其函数声明为:

```
public static void logout()
```

#### 5.获取微信在线状态WechatTools.getWechatStatus()

查询微信在线状态，在线返回`true`，离线返回`false`，其函数声明为

```java
public static boolean getWechatStatus()
```

#### 6.获取群昵称列表WechatTools.getGroupNickNameList()

获取群昵称列表,函数声明为：

```java
public static List<String> getGroupNickNameList()
```

#### 7.根据用户昵称修改用户备注MessageTools.remarkNameByNickName(String nickName, String remName)

根据用户昵称修改用户备注名称，其函数声明为：

```
public static void remarkNameByNickName(String nickName, String remName)
```

#### 8. 根据好友昵称发送文本消息，MessageTools.sendMsgByNickName(String text, String nickName)

此方法根据用户昵称发送文本消息，注意，用户需在你的好友列表里，否则发送失败，如果你的好友列表里有存在昵称一样的多个用户，则只会给第一个匹配的好友发送消息。方法接受两个参数，`text`为要发送的文本消息，`nickName`为要发送消息的好友昵称，成功发送时返回true，失败返回false。其函数声明为：

```
public static boolean sendMsgByNickName(String text, String nickName)
```

#### 9.根据ID发送文本消息， MessageTools.sendMsgById(String text, String id)

根据ID发送文本消息，发送者ID可以从`msg`里通过`msg.getString("FromUserName")`获取，格式为`@@d052d34b9c9228830363013ee53deb461404f80ea353dbdd8fc9391cbf5f1c46`（群消息）或`@a257b99314d8313862cd44ab02fe0f81`（非群消息），调用此方法可向指定id发送消息。其函数声明为：

```
public static void sendMsgById(String text, String id)
```

#### 10.根据好友昵称发送图片消息，MessageTools.sendPicMsgByNickName(String nickName, String filePath)

此方法根据好友昵称发送图片消息，`filePath`为图片文件路径，如`D:/itchat4j/pic/test.jpg`，成功返回true，失败返回false。其函数声明为：

```
public static boolean sendPicMsgByNickName(String nickName, String filePath)
```

#### 11.根据ID发送图片消息，MessageTools.sendPicMsgByUserId(String userId, String filePath)

此方法根据好友ID发送图片消息，filePath`为图片文件路径，如`D:/itchat4j/pic/test.jpg`，成功返回true，失败返回false。其函数声明为：

```
public static boolean sendPicMsgByUserId(String userId, String filePath)
```

#### 12.根据好友昵称发送文件消息，MessageTools.sendFileMsgByNickName(String nickName, String filePath)

此方法根据好友昵称发送文件消息，文件可以为多种类型，如txt、PDF、小视频、语音、excel、docx等，发送时请保证文件后缀名正确。成功返回true，失败返回false。其函数声明为：

```
public static boolean sendPicFileByNickName(String nickName, String filePath)
```

#### 13.根据ID发送文件消息，MessageTools.sendFileMsgByNickName(String nickName, String filePath)

此方法根据好友昵称发送文件消息，成功返回true，失败返回false。其函数声明为：

```
public static boolean sendFileMsgByUserId(String userId, String filePath)
```

## TODO List 即将支持/正在开发

- 拉人进群功能


## 如何使用

*项目在不断更新中，导入后的项目结构会有变动*

itchat4j是一个Maven项目，下载源码后，可以以Maven项目的形式导入，导入后的项目结构如下图:

![itchat4j项目结构](http://oj5vdtyuu.bkt.clouddn.com/itchat4j%E9%A1%B9%E7%9B%AE%E7%BB%93%E6%9E%842.png)

src/main/java是itchat4j的项目源码，在src/test/java目录下有两个小Demo：一个是基本功能的小示例，当前，itchat4j可以处理四类基本信息，文本、语音、图片和小视频，该示例在收到文本信息后自动回复，回复内容为收到的文本，当收到图片、语音、小视频时可以保存到指定的目录；一个是微信接入图灵机器人的小例子，如下图。

### 微信机器人使用截图

![Windows控制台](http://oj5vdtyuu.bkt.clouddn.com/windows%E5%8F%AF%E8%BF%90%E8%A1%8C%E7%A8%8B%E5%BA%8F.png)

![微信机器人](http://oj5vdtyuu.bkt.clouddn.com/%E5%BE%AE%E4%BF%A1%E6%9C%BA%E5%99%A8%E4%BA%BA.jpg)

![控制台收到的消息](http://oj5vdtyuu.bkt.clouddn.com/%E6%8E%A7%E5%88%B6%E5%8F%B0%E6%94%B6%E5%88%B0%E7%9A%84%E6%B6%88%E6%81%AF.png)



## 消息格式

这里简要介绍一下`msg`各种消息，msg均为`json`格式的数据，可使用各自工具进行解析，在itchat4j中我通过alibaba的`fastjosn`工具库进行了解析，每种`msg`均为`fastjson`的标准`JSONObject`对象，后续处理起来非常方便，例如获取文本消息的消息内容：`msg.getString("Text")`，获取名片消息的被推荐人昵称：`msg.getJSONObject("RecommendInfo").getString("NickName")`。有时候可能不需要处理群消息，因此在构造`msg`消息体里我添加了一个判断是否群消息的字段`groupMsg`，可通过`msg.getBoolean("groupMsg")`获取字段的值，如果是群消息，返回true，如果非群消息，返回false。

### 1.文本消息

```Json
{
    "SubMsgType": 0,
    "VoiceLength": 0,
    "FileName": "",
    "ImgHeight": 0,
    "ToUserName": "@58b8651e056f8937f7a4eaa386be0c16d2583a0fdb5741b874cedffe3e13e723",
    "HasProductId": 0,
    "ImgStatus": 1,
    "Url": "",
    "ImgWidth": 0,
    "ForwardFlag": 0,
    "Status": 3,
    "Ticket": "",
    "RecommendInfo": {
        "Ticket": "",
        "UserName": "",
        "Sex": 0,
        "AttrStatus": 0,
        "City": "",
        "NickName": "",
        "Scene": 0,
        "Province": "",
        "Content": "",
        "Alias": "",
        "Signature": "",
        "OpCode": 0,
        "QQNum": 0,
        "VerifyFlag": 0
    },
    "CreateTime": 1494079411,
    "NewMsgId": 6942811558026846000,
    "Text": "你好",
    "MsgType": 1,
    "groupMsg": false,
    "MsgId": "6942811558026845859",
    "StatusNotifyCode": 0,
    "AppInfo": {
        "Type": 0,
        "AppID": ""
    },
    "AppMsgType": 0,
    "Type": "Text",
    "PlayLength": 0,
    "MediaId": "",
    "Content": "你好",
    "StatusNotifyUserName": "",
    "FromUserName": "@a257b99314d8313862cd44ab02fe0f81",
    "OriContent": "",
    "FileSize": ""
}
```

### 图片消息

```json
{
    "SubMsgType": 0,
    "VoiceLength": 0,
    "FileName": "",
    "ImgHeight": 120,
    "ToUserName": "@58b8651e056f8937f7a4eaa386be0c16d2583a0fdb5741b874cedffe3e13e723",
    "HasProductId": 0,
    "ImgStatus": 2,
    "Url": "",
    "ImgWidth": 90,
    "ForwardFlag": 0,
    "Status": 3,
    "Ticket": "",
    "RecommendInfo": {
        "Ticket": "",
        "UserName": "",
        "Sex": 0,
        "AttrStatus": 0,
        "City": "",
        "NickName": "",
        "Scene": 0,
        "Province": "",
        "Content": "",
        "Alias": "",
        "Signature": "",
        "OpCode": 0,
        "QQNum": 0,
        "VerifyFlag": 0
    },
    "CreateTime": 1494079495,
    "NewMsgId": 6081337643309445000,
    "MsgType": 3,
    "groupMsg": false,
    "MsgId": "6081337643309445027",
    "StatusNotifyCode": 0,
    "AppInfo": {
        "Type": 0,
        "AppID": ""
    },
    "AppMsgType": 0,
    "Type": "Pic",
    "PlayLength": 0,
    "MediaId": "",
    "Content": "&lt;?xml version=\"1.0\"?&gt;\n&lt;msg&gt;\n\t&lt;img aeskey=\"2384ec2f417e4066a23522635d76b86a\" encryver=\"0\" cdnthumbaeskey=\"2384ec2f417e4066a23522635d76b86a\" cdnthumburl=\"3050020100044930470201000204577f6a2c02030f48810204a5b88cb60204590cac9e0425617570696d675f633337313936633333656466343463635f313439343030323834363430340201000201000400\" cdnthumblength=\"12204\" cdnthumbheight=\"120\" cdnthumbwidth=\"90\" cdnmidheight=\"0\" cdnmidwidth=\"0\" cdnhdheight=\"0\" cdnhdwidth=\"0\" cdnmidimgurl=\"3050020100044930470201000204577f6a2c02030f48810204a5b88cb60204590cac9e0425617570696d675f633337313936633333656466343463635f313439343030323834363430340201000201000400\" length=\"139120\" md5=\"5a774ad813f40fb3ca81349d82101423\" /&gt;\n&lt;/msg&gt;\n",
    "StatusNotifyUserName": "",
    "FromUserName": "@a257b99314d8313862cd44ab02fe0f81",
    "OriContent": "",
    "FileSize": ""
}
```

### 语音消息

```Json
{
    "SubMsgType": 0,
    "VoiceLength": 2112,
    "FileName": "",
    "ImgHeight": 0,
    "ToUserName": "@58b8651e056f8937f7a4eaa386be0c16d2583a0fdb5741b874cedffe3e13e723",
    "HasProductId": 0,
    "ImgStatus": 1,
    "Url": "",
    "ImgWidth": 0,
    "ForwardFlag": 0,
    "Status": 3,
    "Ticket": "",
    "RecommendInfo": {
        "Ticket": "",
        "UserName": "",
        "Sex": 0,
        "AttrStatus": 0,
        "City": "",
        "NickName": "",
        "Scene": 0,
        "Province": "",
        "Content": "",
        "Alias": "",
        "Signature": "",
        "OpCode": 0,
        "QQNum": 0,
        "VerifyFlag": 0
    },
    "CreateTime": 1494079534,
    "NewMsgId": 1038534170192835800,
    "MsgType": 34,
    "groupMsg": false,
    "MsgId": "1038534170192835842",
    "StatusNotifyCode": 0,
    "AppInfo": {
        "Type": 0,
        "AppID": ""
    },
    "AppMsgType": 0,
    "Type": "Voice",
    "PlayLength": 0,
    "MediaId": "",
    "Content": "&lt;msg&gt;&lt;voicemsg endflag=\"1\" cancelflag=\"0\" forwardflag=\"0\" voiceformat=\"4\" voicelength=\"2112\" length=\"4051\" bufid=\"291965468715843925\" clientmsgid=\"41393631336234386239346262373200302205050617d9115824727105\" fromusername=\"zyfandlzz\" /&gt;&lt;/msg&gt;",
    "StatusNotifyUserName": "",
    "FromUserName": "@a257b99314d8313862cd44ab02fe0f81",
    "OriContent": "",
    "FileSize": ""
}
```

### 小视频消息

```Json
{
    "SubMsgType": 0,
    "VoiceLength": 0,
    "FileName": "",
    "ImgHeight": 540,
    "ToUserName": "@58b8651e056f8937f7a4eaa386be0c16d2583a0fdb5741b874cedffe3e13e723",
    "HasProductId": 0,
    "ImgStatus": 1,
    "Url": "",
    "ImgWidth": 960,
    "ForwardFlag": 0,
    "Status": 3,
    "Ticket": "",
    "RecommendInfo": {
        "Ticket": "",
        "UserName": "",
        "Sex": 0,
        "AttrStatus": 0,
        "City": "",
        "NickName": "",
        "Scene": 0,
        "Province": "",
        "Content": "",
        "Alias": "",
        "Signature": "",
        "OpCode": 0,
        "QQNum": 0,
        "VerifyFlag": 0
    },
    "CreateTime": 1494079644,
    "NewMsgId": 1478649195821152000,
    "MsgType": 43,
    "groupMsg": false,
    "MsgId": "1478649195821152019",
    "StatusNotifyCode": 0,
    "AppInfo": {
        "Type": 0,
        "AppID": ""
    },
    "AppMsgType": 0,
    "Type": "Video",
    "PlayLength": 2,
    "MediaId": "",
    "Content": "&lt;?xml version=\"1.0\"?&gt;\n&lt;msg&gt;\n\t&lt;videomsg aeskey=\"d9770f7f38f04888a96f95faa548dbd8\" cdnthumbaeskey=\"d9770f7f38f04888a96f95faa548dbd8\" cdnvideourl=\"30680201000461305f0201000204577f6a2c02032dcd0102041e0a96b60204590dd89c043d617570766964656f5f313530306338303339326430363161645f313439343037393634325f3232303731353036303531373331653863316435333233300201000201000400\" cdnthumburl=\"30680201000461305f0201000204577f6a2c02032dcd0102041e0a96b60204590dd89c043d617570766964656f5f313530306338303339326430363161645f313439343037393634325f3232303731353036303531373331653863316435333233300201000201000400\" length=\"328666\" playlength=\"2\" cdnthumblength=\"10398\" cdnthumbwidth=\"960\" cdnthumbheight=\"540\" fromusername=\"zyfandlzz\" md5=\"555c3efc0e065ba83c3fed942fea81b5\" newmd5=\"33389ec240de3e125f9f319c011781dd\" isad=\"0\" /&gt;\n&lt;/msg&gt;\n",
    "StatusNotifyUserName": "",
    "FromUserName": "@a257b99314d8313862cd44ab02fe0f81",
    "OriContent": "",
    "FileSize": ""
}
```

### 名片消息

```Json
{
    "SubMsgType": 0,
    "VoiceLength": 0,
    "FileName": "",
    "ImgHeight": 0,
    "ToUserName": "@58b8651e056f8937f7a4eaa386be0c16d2583a0fdb5741b874cedffe3e13e723",
    "HasProductId": 0,
    "ImgStatus": 1,
    "Url": "",
    "ImgWidth": 0,
    "ForwardFlag": 0,
    "Status": 3,
    "Ticket": "",
    "RecommendInfo": {
        "Ticket": "",
        "UserName": "@173bd4ce01b725f327c221a06017260734d4607001d1dc82ba6b99c1ef77fb92",
        "Sex": 0,
        "AttrStatus": 32,
        "City": "",
        "NickName": "LittleCoder机器人",
        "Scene": 17,
        "Province": "",
        "Content": "",
        "Alias": "",
        "Signature": "",
        "OpCode": 0,
        "QQNum": 0,
        "VerifyFlag": 0
    },
    "CreateTime": 1494079592,
    "NewMsgId": 6687290426846395000,
    "MsgType": 42,
    "groupMsg": false,
    "MsgId": "6687290426846395587",
    "StatusNotifyCode": 0,
    "AppInfo": {
        "Type": 0,
        "AppID": ""
    },
    "AppMsgType": 0,
    "Type": "NameCard",
    "PlayLength": 0,
    "MediaId": "",
    "Content": "&lt;?xml version=\"1.0\"?&gt;\n&lt;msg bigheadimgurl=\"http://wx.qlogo.cn/mmhead/ver_1/ajX7IquRvt16WXDJYrYsBuGy5HoicQ1ibNbLKKHu744ic2WnxSaRtEQCgibSP8S2MdyIsqTWsKUUEZydsias9UR55nSQE7n6ibXChx4DQZQf5xh0M/0\" smallheadimgurl=\"http://wx.qlogo.cn/mmhead/ver_1/ajX7IquRvt16WXDJYrYsBuGy5HoicQ1ibNbLKKHu744ic2WnxSaRtEQCgibSP8S2MdyIsqTWsKUUEZydsias9UR55nSQE7n6ibXChx4DQZQf5xh0M/132\" username=\"v1_183ca2ddb6369f35d74ea56046fcdf33d3a769352d9e125b44d26c18c0063ff537f6d66ea415db7648605aabf65b7b98@stranger\" nickname=\"LittleCoder机器人\"  shortpy=\"LITTLECODERJQR\" alias=\"\" imagestatus=\"3\" scene=\"17\" province=\"\" city=\"\" sign=\"\" sex=\"0\" certflag=\"0\" certinfo=\"\" brandIconUrl=\"\" brandHomeUrl=\"\" brandSubscriptConfigUrl=\"\" brandFlags=\"0\" regionCode=\"\" antispamticket=\"v2_6b780d55a1b949e161126df27729c85fd8b136d673ec7de475b0b5d811737502657129df8f19cd705cd90dc74bc12c09@stranger\" /&gt;\n",
    "StatusNotifyUserName": "",
    "FromUserName": "@a257b99314d8313862cd44ab02fe0f81",
    "OriContent": "",
    "FileSize": ""
}
```



## 如何使用

如果你想引入自己的项目，请切换到`pom.xml`目录，执行`mvn package`命令，将生成的`itchat4j`的jar包收入即可。



## 简单入门教程

*项目不断更新中，教程仅供参考*

接下来，通过两个小Demo来演示一下如何使用itchat4j来扩展你的个人微信号，入门教程的项目源码可以从[此处下载](https://github.com/yaphone/itchat4jdemo)。

### Demo1: SimpleDemo

这个小Demo将会将收到的文本消息发送给发件人，如果是图片、语音或者小视频消息，将会保存在我们指定的路径下。

首先需要新建一个类来实现`IMsgHandlerFace`这个接口，这个类要做的就是我们需要完成的逻辑，该接口有四个方法需要实现，`textMsgHandle`用于处理文本信息，`picMsgHandle`用于处理图片信息，`viedoMsgHandle`用于处理小视频信息，`voiceMsgHandle`用于处理语音信息，代码如下：

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

由于没有关联源码，所以接口中的参数都变成了`arg0`这种，建议关联一下源码，源码可在[release](https://github.com/yaphone/itchat4j/releases)中下载，当然不关联也不会有啥影响，`arg0`其实是我们需要处理的消息体，为了更直观，建议把`arg0`修改为`msg`，msg是fastjson的JSONObject类型，这个其实不用关心，我们只需要知道如何来获取需要的消息就可以了，下面的Demo中有示例。然后我们来写处理逻辑。

在`textMsgHandler`中，通过`msg.getString("Text")`就可以获取收到的文本信息，然后作进一步处理，比如接入图灵机器人、消息自动回复等，我们需要在这个方法中返回一个字符串，即是需要回复给好友的消息，在SimpleDemo这个示例中，我们直接回复收到的原文本消息。

在`picMsgHandle`、`voiceMsgHandle`、`viedoMsgHandle`这三个方法中，我们需要将这些消息下载下来，然后再作进一步处理，所以需要为每种类型的消息提供一个保存路径，然后调用`DownloadTools.getDownloadFn`方法可以将这三种类型的消息下载下来。`DownloadTools.getDownloadFn`方法提供下载图片、语音、小视频的功能，需要三个参数，第一个参数为我们收到的msg，第二个参数为`MsgType`，也就是消息类型，图片、语音、小视频分别对应`MsgTypeEnum.PIC.getType()`、`MsgTypeEnum.VOICE.getType()`、`MsgTypeEnum.VIEDO.getType()`，然后第三个参数就是保存这些消息的路径了。

就不多说了，让代码和注释君自述吧，有不明白的地方，可以在Issue中提出来。

```java
/**
 * 简单示例程序，收到文本信息自动回复原信息，收到图片、语音、小视频后根据路径自动保存
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月25日 上午12:18:09
 * @version 1.0
 *
 */
public class SimpleDemo implements IMsgHandlerFace {
	Logger LOG = Logger.getLogger(SimpleDemo.class);

	@Override
	public String textMsgHandle(JSONObject msg) {
		String docFilePath = "D:/itchat4j/pic/test.docx"; // 这里是需要发送的文件的路径
		if (!msg.getBoolean("groupMsg")) { // 群消息不处理
			String userId = msg.getString("FromUserName");
			MessageTools.sendFileMsgByUserId(userId, docFilePath); // 发送文件
			String text = msg.getString("Text"); // 发送文本消息，也可调用MessageTools.sendFileMsgByUserId(userId,text);
			return text;
		}
		return null;
	}

	@Override
	public String picMsgHandle(JSONObject msg) {
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());// 这里使用收到图片的时间作为文件名
		String picPath = "D://itchat4j/pic" + File.separator + fileName + ".jpg"; // 调用此方法来保存图片
		DownloadTools.getDownloadFn(msg, MsgTypeEnum.PIC.getType(), picPath); // 保存图片的路径
		return "图片保存成功";
	}

	@Override
	public String voiceMsgHandle(JSONObject msg) {
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String voicePath = "D://itchat4j/voice" + File.separator + fileName + ".mp3";
		DownloadTools.getDownloadFn(msg, MsgTypeEnum.VOICE.getType(), voicePath);
		return "声音保存成功";
	}

	@Override
	public String viedoMsgHandle(JSONObject msg) {
		System.out.println(msg);
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String viedoPath = "D://itchat4j/viedo" + File.separator + fileName + ".mp4";
		DownloadTools.getDownloadFn(msg, MsgTypeEnum.VIEDO.getType(), viedoPath);
		return "视频保存成功";
	}

	@Override
	public String nameCardMsgHandle(JSONObject msg) {
		return "收到名片消息";
	}

}
```

之后我们需要将实现`IMsgHandlerFace`接口的类【注入】到`Wechat`中来启动服务，`Wechat`是服务的主入口，其构造函数接受两个参数，一个是我们刚才实现`IMsgHandlerFace`接口的类，另一个是保存登陆二维码图片的路径。之后在`Wechat`对象上调用`start()`方法来启动服务，会在我们刚才传入的路径下生成一个`QR.jpg`文件，即是登陆二维码，通过手机微信扫描后即可登陆，服务启动，处理逻辑开始工作。这里有一点需要注意：*二维码图片如果超过一定时间未扫描会过期，过期时会自动更新，所以你可能需要重新打开图片*。

额，文字还是太苍白，让代码和注释君自述吧。

```Java
/**
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月28日 上午12:44:10
 * @version 1.0
 *
 */
public class MyTest {
	public static void main(String[] args) {
		String qrPath = "D://itchat4j//login"; // 保存登陆二维码图片的路径
		IMsgHandlerFace msgHandler = new SimpleDemo(); // 实现IMsgHandlerFace接口的类
		Wechat wechat = new Wechat(msgHandler, qrPath); // 【注入】
		wechat.start(); // 启动服务，会在qrPath下生成一张二维码图片，扫描即可登陆，注意，二维码图片如果超过一定时间未扫描会过期，过期时会自动更新，所以你可能需要重新打开图片
	}
}

```

### Demo2 图灵机器人

> 图灵机器人大脑具备强大的中文语义分析能力，可准确理解中文含义并作出回应，是最擅长聊中文的机器人大脑，赋予软硬件产品自然流畅的人机对话能力。(引自百度百科)

这个示例中我们接入图灵机器人的API，将收到的好友的文本信息发送给图灵机器人，并将机器人回复的消息发送给好友，接下来还是把舞台交代码和注释君吧。

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
	MyHttpClient myHttpClient = Core.getInstance().getMyHttpClient();
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
		String fileName = String.valueOf(new Date().getTime());
		String voicePath = "D://itchat4j/voice" + File.separator + fileName + ".mp3";
		DownloadTools.getDownloadFn(msg, MsgTypeEnum.VOICE.getType(), voicePath);
		return "收到语音";
	}

	@Override
	public String viedoMsgHandle(JSONObject msg) {
		String fileName = String.valueOf(new Date().getTime());
		String viedoPath = "D://itchat4j/viedo" + File.separator + fileName + ".mp4";
		DownloadTools.getDownloadFn(msg, MsgTypeEnum.VIEDO.getType(), viedoPath);
		return "收到视频";
	}

	public static void main(String[] args) {
		IMsgHandlerFace msgHandler = new TulingRobot();
		Wechat wechat = new Wechat(msgHandler, "D://itchat4j/login");
		wechat.start();
	}

	@Override
	public String nameCardMsgHandle(JSONObject msg) {
		// TODO Auto-generated method stub
		return null;
	}

}

```

### Demo3 itchat4j集成在SpringMVC应用中

这个示例要讲起来就比较困难了，因为SpringMVC本身就是一个复杂的东西，先在这里挖个坑吧。其实在SpringMVC中集成与上面两个示例并没有太大的不同，我的个人博客是基于SpringMVC的，我已经将集成在这个项目里了，这样我就可以通过微信来更新我的博客了。详细的就不多说了，大家先看看这个项目结构吧。

![itchat4j集成在Blog项目](http://oj5vdtyuu.bkt.clouddn.com/itchat4j%E9%9B%86%E6%88%90%E5%9C%A8blog%E9%A1%B9%E7%9B%AE%E4%B8%AD%E4%BF%AE%E6%94%B9%E5%90%8E.jpg)

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

[itchat](https://github.com/littlecodersh/ItChat) ：优秀的、基于Python的微信个人号API，同时也是本项目的灵感之源。

[WeixinBot](https://github.com/Urinx/WeixinBot): 网页版微信API，包含终端版微信及微信机器人

## 致谢：

itchat4j开源后，收到很多朋友的建议，对ithcat4j改进做出了很多帮助，在此表示感谢！

[@jasonTangxd](https://github.com/jasonTangxd?tab=overview&from=2017-05-15)，项目结构调整。

[@libre818](https://github.com/libre818)。

@QQ群好友（北极心 851668663）,增加修改好友备注名方法。

@QQ群好友（beyond_12345@126.com）

## 问题和建议

本项目长期更新、维护，功能不断扩展与完善中，欢迎star。

项目使用过程中遇到问题，或者有好的建议，欢迎随时反馈。

任何问题或者建议都可以在Issue中提出来，也可以加入QQ群讨论：636365179

