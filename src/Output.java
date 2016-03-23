import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Frost
 */
public class Output extends Thread {

    private Socket socket;
    private JTextArea txt;
    private ServerSocket serv;

    public Output(Socket socket, JTextArea txt, ServerSocket serv) {
        this.socket = socket;
        this.txt = txt;
        this.serv = serv;
    }

    @Override
    public void run() {
        InputStream in = null;
        int bytesRead;
        int current = 0;

        try {
            in = socket.getInputStream();
            DataInputStream ins = new DataInputStream(in);

            String fileName = ins.readUTF();
            long size = ins.readLong();

            JFileChooser j = new JFileChooser();
            File f = new File("C:\\" + fileName);
            j.setCurrentDirectory(f);
            j.showSaveDialog(null);
            System.out.println(j.getSelectedFile().getPath());
            OutputStream output = new FileOutputStream(j.getSelectedFile().getPath());
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();
            socket.close();
            serv.close();
            
            txt.append("File Received\n");
            new Run(txt).start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
