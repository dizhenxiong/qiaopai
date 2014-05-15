package com.lenovo.vctl.apps.commons.utils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class IPUtil {
	public String getMacAddr() {
		String MacAddr = "";
		StringBuffer str = new StringBuffer();
		try {
			NetworkInterface NIC = NetworkInterface.getByName("eth0");
			byte[] buf = NIC.getHardwareAddress();
			for (int i = 0; i < buf.length; i++) {
				str.append(byteHEX(buf[i]));
			}
			MacAddr = str.toString().toUpperCase();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		return MacAddr;
	}

	/**
	 * 获取本地IP地址
	 * @return
	 */
	public static String getIP() {
		String ip = "";

		try {
			InetAddress addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress().toString();
			if (!ip.equals("127.0.0.1")) {
				return ip;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		try {
			Enumeration<?> e1 = (Enumeration<?>) NetworkInterface
					.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				if (!ni.getName().equals("eth0")) {
					continue;
				} else {
					Enumeration<?> e2 = ni.getInetAddresses();
					while (e2.hasMoreElements()) {
						InetAddress ia = (InetAddress) e2.nextElement();
						if (ia instanceof Inet6Address)
							continue;
						ip = ia.getHostAddress();
					}
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		return ip;
	}

	/* 一个将字节转化为十六进制ASSIC码的函数 */
	public static String byteHEX(byte ib) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
				'b', 'c', 'd', 'e', 'f' };
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F];
		ob[1] = Digit[ib & 0X0F];
		String s = new String(ob);
		return s;
	}

	public static String getIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		// 解析IP, 第一个为真ip
		String[] ips = null;
		if (ip != null) {
			ips = ip.split(",");
			ip = ips[0];
		}
		return ip;
	}
	
	
	// ip 192.1.16.100 转换成 int
	public static int ipString2Int(String ip) throws RuntimeException{
		if(ip == null || ip.split("\\.").length != 4){
			return 0;
		}
		String ips[] = ip.split("\\.");
		int tmp1 = Integer.parseInt(ips[0].trim());
		if(tmp1 > 255 || tmp1 < 0)throw new RuntimeException("ip is invalid");
		int tmp2 = Integer.parseInt(ips[1].trim());
		if(tmp2 > 255 || tmp2 < 0)throw new RuntimeException("ip is invalid");
		int tmp3 = Integer.parseInt(ips[2].trim());
		if(tmp3 > 255 || tmp3 < 0)throw new RuntimeException("ip is invalid");
		int tmp4 = Integer.parseInt(ips[3].trim());
		if(tmp4 > 255 || tmp4 < 0)throw new RuntimeException("ip is invalid");
		int ipInt = ((tmp1) << 24) + ((tmp2) << 16) + ((tmp3) << 8) + ((tmp4) << 0);
		return ipInt;

	}
	
	// ipString2Int 反转化
	public static String ipInt2String(int ipInt) throws RuntimeException{
		int ip1 = (ipInt >>> 24) & 0xFF;
		if(ip1 > 255 | ip1 < 0) throw new RuntimeException("ip is invalid");
		int ip2 = (ipInt >>> 16) & 0x00FF;
		if(ip2 > 255 | ip2 < 0) throw new RuntimeException("ip is invalid");
		int ip3 = (ipInt >>> 8) & 0x0000FF;
		if(ip3 > 255 | ip3 < 0) throw new RuntimeException("ip is invalid");
		int ip4 = (ipInt >>> 0) & 0x000000FF;
		if(ip4 > 255 | ip4 < 0) throw new RuntimeException("ip is invalid");
		
		return ip1 + "." + ip2 + "." + ip3 + "." + ip4;
				
	}
	
	
	public static void main(String[] args) {
		String ip = "172.16.1.192";
		int ipInt = IPUtil.ipString2Int(ip);
		System.out.println(ipInt);
		System.out.println(IPUtil.ipInt2String(ipInt));
	}
}
