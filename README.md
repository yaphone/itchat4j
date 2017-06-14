# 个人修改
> [原项目说明](https://github.com/Kuangcp/itchat4j/blob/master/yaphone.md)
### 修改运行方式 2017-06-06 12:27:47
- 运行 mvn package 命令 进行打包
- 指定使用图灵机器人的类来运行
    - `java -cp jar包 cn.zhouyafeng.itchat4j.main.TulingRobot 目录`

- 可能遇到的bug:
    - Exception in thread "main" java.lang.UnsatisfiedLinkError: /home/java/jdk1.8.0_102/jre/lib/amd64/libawt_xawt.so: libXrender.so.1: cannot open shared object file: No such file or directory
    - [FixPage](http://ju.outofmemory.cn/entry/296572)
- linux下：
    - `./run.sh` 生成图片之后直接扫描图片或者如下：
    - `./read.sh` 终端中输出二维码扫描，登录即可
    - `注意`：前提是安装了python和  [qrcode-terminal](https://github.com/alishtory/qrcode-terminal)
