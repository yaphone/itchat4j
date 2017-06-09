basepath=$(cd `dirname $0`; pwd)
cd $basepath/target
java -cp itchat4j-1.1.0.jar cn.zhouyafeng.itchat4j.main.TulingRobot /home/kcp/Code/wechat/
