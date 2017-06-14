rm QR.jpg
basepath=$(cd `dirname $0`; pwd)
cd $basepath/target
java -cp itchat4j-1.1.0.jar cn.zhouyafeng.itchat4j.main.TulingRobot $basepath 
cd ..
for ((i=0;i<3;i++));do
{
    sleep 45;
    java -cp JavaToolKit-1.0-SNAPSHOT.jar com.myth.qrcode.ReadQRCode $basepath/QR.jpg
}&
done
wait

#infile=temp
#while read xcoord
#do
#    echo $xcoord
#    qrcode-terminal-py -d $xcoord
#done < $infile
#rm temp


