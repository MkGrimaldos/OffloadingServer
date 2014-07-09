package thesisServer;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		DataInputStream dataInputStream = null;
		DataOutputStream dataOutputStream = null;

		try {
			serverSocket = new ServerSocket(8888);
			System.out.println("Listening: 8888");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (true) {
			try {
				socket = serverSocket.accept();
				
				long beginning = System.nanoTime();
				
				dataInputStream = new DataInputStream(socket.getInputStream());
				dataOutputStream = new DataOutputStream(
						socket.getOutputStream());
				
				//System.out.println("ip: " + socket.getInetAddress());
				long base = Long.parseLong(dataInputStream.readUTF());
				int power = Integer.parseInt(dataInputStream.readUTF());
				//System.out.println("Base: " + base + " Power: " + power);
				
				int n = power;
				
				BigInteger resul = new BigInteger("1");

				//System.out.println("Computing...");
				
				while (n > 0) {
					resul = resul.multiply(BigInteger.valueOf(base));
					--n;
				}
				
				System.out.println(/*"Computed\n" + */"Result = " + resul);
				
				dataOutputStream.writeUTF(resul.toString());
				
				long end = System.nanoTime();
				
				System.out.println("Time elapsed: " + (end - beginning) + "ns");
				
				try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Server.txt", true)))) {
				    out.println((end - beginning));
				}catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (dataInputStream != null) {
					try {
						dataInputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (dataOutputStream != null) {
					try {
						dataOutputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

}
