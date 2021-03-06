package thesisServer;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.VideoAttributes;
import it.sauronsoftware.jave.VideoSize;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	// If OPTION == 1 we assume file is on Server
	// If OPTION == 2 we assume file will be sent by the Client
	// Check Client app for consistency purposes
	private static final int OPTION = 2;
	
	static long beginning;
	static long end;

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

				switch (OPTION) {
				case 1:
					fileOnServer(socket, dataInputStream, dataOutputStream);
					break;
				case 2:
					fileOnClient(serverSocket, socket, dataInputStream, dataOutputStream);
					//fileOnServer(socket, dataInputStream, dataOutputStream);
					break;
				default:
					break;
				}

				/*
				 * dataInputStream = new
				 * DataInputStream(socket.getInputStream()); dataOutputStream =
				 * new DataOutputStream( socket.getOutputStream());
				 * 
				 * //System.out.println("ip: " + socket.getInetAddress()); long
				 * base = Long.parseLong(dataInputStream.readUTF()); int power =
				 * Integer.parseInt(dataInputStream.readUTF());
				 * //System.out.println("Base: " + base + " Power: " + power);
				 * 
				 * dataOutputStream.writeUTF(bigPower(base, power).toString());
				 */

				System.out.println("Time elapsed: " + (end - beginning) + "ns\n");

				try (PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter("Server.txt", true)))) {
					out.println((end - beginning));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
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

	public static void fileOnServer(Socket socket,
			DataInputStream dataInputStream, DataOutputStream dataOutputStream)
			throws Exception {
		File source = new File("source.mp4");
		File target = new File("target.avi");

		transcode(source, target);

		byte[] buffer = new byte[8192];
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				target));
		dataInputStream = new DataInputStream(bis);

		try {
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataOutputStream.writeUTF(target.getName());
			int count;
			while ((count = dataInputStream.read(buffer)) > 0) {
				dataOutputStream.write(buffer, 0, count);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void fileOnClient(ServerSocket serverSocket, Socket socket,
			DataInputStream dataInputStream, DataOutputStream dataOutputStream)
			throws Exception {
		File target = new File("target.avi");

		int bufferSize = 0;
		
		System.out.println("Receiving file...");

		bufferSize = socket.getReceiveBufferSize();
		dataInputStream = new DataInputStream(socket.getInputStream());
		String fileName = dataInputStream.readUTF();
		System.out.println(fileName);
		OutputStream output = new FileOutputStream(fileName);
		byte[] bufferReceive = new byte[bufferSize];		
		int count;
		while ((count = dataInputStream.read(bufferReceive)) > 0) {
			//System.out.println(count);
			output.write(bufferReceive, 0, count);
		}
		output.close();

		System.out.println("Received!");
		
		socket = serverSocket.accept();
		
		File source = new File(fileName);
		
		System.out.println("Transcoding...");
		
		transcode(source, target);
		
		System.out.println("Transcoded");
		System.out.println("Sending the result back");

		byte[] buffer = new byte[8192];
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				target));
		dataInputStream = new DataInputStream(bis);

		try {
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataOutputStream.writeUTF(target.getName());
			// int count;
			while ((count = dataInputStream.read(buffer)) > 0) {
				dataOutputStream.write(buffer, 0, count);
			}
			
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("I love it when a plan comes together");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static BigInteger bigPower(long base, int power) {

		int n = power;

		BigInteger resul = new BigInteger("1");

		while (n > 0) {
			resul = resul.multiply(BigInteger.valueOf(base));
			--n;
		}

		System.out.println(/* "Computed\n" + */"Result = " + resul);

		return resul;
	}

	public static void transcode(File source, File target) {
		beginning = System.nanoTime();
		
		AudioAttributes audioAttributes = new AudioAttributes();
		audioAttributes.setCodec("libmp3lame");
		audioAttributes.setBitRate(new Integer(96000/* 128000 */));
		audioAttributes.setChannels(new Integer(2));
		audioAttributes.setSamplingRate(new Integer(48000/* 44100 */));
		VideoAttributes videoAttributes = new VideoAttributes();
		videoAttributes.setCodec("mpeg4");
		videoAttributes.setBitRate(new Integer(160000));
		videoAttributes.setFrameRate(new Integer(30/* 15 */));
		videoAttributes.setSize(new VideoSize(1920, 1080/* 400, 300 */));
		EncodingAttributes attributes = new EncodingAttributes();
		attributes.setFormat("avi");
		attributes.setAudioAttributes(audioAttributes);
		attributes.setVideoAttributes(videoAttributes);
		Encoder encoder = new Encoder();

		try {
			encoder.encode(source, target, attributes);
		} catch (IllegalArgumentException | EncoderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		end = System.nanoTime();
	}

}
