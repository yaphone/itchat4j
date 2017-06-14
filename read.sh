rm temp
touch temp
java -cp JavaToolKit-1.0-SNAPSHOT.jar com.myth.qrcode.ReadQRCode /home/kcp/Code/wechat/itchat4j/QR.jpg>>temp

infile=temp
while read xcoord; do
    qrcode-terminal-py -d $xcoord
done < $infile
rm temp
