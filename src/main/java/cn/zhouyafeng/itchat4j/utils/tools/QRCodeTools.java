package cn.zhouyafeng.itchat4j.utils.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

public class QRCodeTools {

	private static Logger logger = LoggerFactory.getLogger(QRCodeTools.class);
	private static JFrame frameInstance;
	private static int FRAME_WIDTH = 300;
	private static int FRAME_HEIGHT = 400;

	/**
	 * 获取JFrame单例
	 *
	 * @return
	 */
	public static JFrame getFrameInstance() {
		synchronized (JFrame.class) {
			if ( frameInstance == null ) {
				frameInstance = new JFrame();
				frameInstance.getContentPane().setBackground(Color.WHITE);
				frameInstance.setResizable(false);
				frameInstance.setLayout(null);
				frameInstance.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				frameInstance.setSize(FRAME_WIDTH, FRAME_HEIGHT);
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				frameInstance.setLocation(
					dim.width / 2 - FRAME_WIDTH / 2, dim.height / 2 - FRAME_HEIGHT / 2);
				frameInstance.setVisible(true);
			}
		}
		return frameInstance;
	}

	/**
	 * 显示登录二维码
	 *
	 * @return
	 */
	public static boolean showLoginCode( byte[] bytes ) {

		int CODE_WIDTH = 190;
		int CODE_HEIGHT = 190;
		int CODE_MARGIN_TOP = 40;

		BufferedImage resizedImg;
		InputStream inputStream = new ByteArrayInputStream(bytes);
		try {
			resizedImg = new BufferedImage(CODE_WIDTH, CODE_HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = resizedImg.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.drawImage(ImageIO.read(inputStream), 0, 0, CODE_WIDTH, CODE_HEIGHT, null);
			graphics2D.dispose();
		} catch (IOException e) {
			logger.info(e.getMessage());
			return false;
		}
		JLabel codeLabel = new JLabel();
		codeLabel.setIcon(new ImageIcon(resizedImg));
		codeLabel.setSize(CODE_WIDTH, CODE_HEIGHT);
		codeLabel.setLocation(( FRAME_WIDTH - CODE_WIDTH ) / 2, CODE_MARGIN_TOP);

		JLabel tipsLabel = new JLabel("扫码登录微信");
		tipsLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
		tipsLabel.setSize(FRAME_WIDTH, 20);
		tipsLabel.setHorizontalAlignment(JLabel.CENTER);
		tipsLabel.setLocation(0, CODE_MARGIN_TOP * 2 + CODE_HEIGHT);

		JFrame frame = getFrameInstance();
		frame.getContentPane().removeAll();
		frame.getContentPane().repaint();

		frame.getContentPane().add(codeLabel);
		frame.getContentPane().add(tipsLabel);

		frame.validate();
		return true;
	}

	/**
	 * 显示登录头像
	 */
	public static void showLoginAvatar( String imageBase64Data ) {

		int AVATAR_WIDTH = 100;
		int AVATAR_HEIGHT = 100;
		int CODE_MARGIN_TOP = 78;

		BufferedImage resizedImg;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] imageByte = decoder.decodeBuffer(imageBase64Data);
			InputStream inputStream = new ByteArrayInputStream(imageByte);
			resizedImg = new BufferedImage(AVATAR_WIDTH, AVATAR_HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = resizedImg.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.drawImage(ImageIO.read(inputStream), 0, 0, AVATAR_WIDTH, AVATAR_HEIGHT, null);
			graphics2D.dispose();
		} catch (IOException e) {
			logger.info(e.getMessage());
			return;
		}
		JLabel codeLabel = new JLabel();
		codeLabel.setIcon(new ImageIcon(resizedImg));
		codeLabel.setSize(AVATAR_WIDTH, AVATAR_HEIGHT);
		codeLabel.setLocation(( FRAME_WIDTH - AVATAR_HEIGHT ) / 2, CODE_MARGIN_TOP);

		JLabel tipsLabel = new JLabel("扫描成功");
		tipsLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
		tipsLabel.setForeground(new Color(0x26AB28));
		tipsLabel.setSize(FRAME_WIDTH, 20);
		tipsLabel.setHorizontalAlignment(JLabel.CENTER);
		tipsLabel.setLocation(0, CODE_MARGIN_TOP + AVATAR_HEIGHT + 30);

		JLabel subTipsLabel = new JLabel("请在手机微信中点击登录");
		subTipsLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
		subTipsLabel.setForeground(new Color(0x888888));
		subTipsLabel.setSize(FRAME_WIDTH, 20);
		subTipsLabel.setHorizontalAlignment(JLabel.CENTER);
		subTipsLabel.setLocation(0, CODE_MARGIN_TOP + AVATAR_HEIGHT + 60);

		JFrame frame = getFrameInstance();
		frame.getContentPane().removeAll();
		frame.getContentPane().repaint();

		frame.getContentPane().add(codeLabel);
		frame.getContentPane().add(tipsLabel);
		frame.getContentPane().add(subTipsLabel);
	}

	/**
	 * 清除JFrame
	 */
	public static void dismissLoginCode() {
		if ( frameInstance != null ) {
			frameInstance.setVisible(false);
			frameInstance.dispose();
			frameInstance = null;
		}
	}
}
