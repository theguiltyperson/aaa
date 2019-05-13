import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
public class ChatServer extends JFrame {
    private ObjectInputStream m_input;
    private ObjectOutputStream m_output;
    private JTextField m_enter;
    private JTextArea m_display;
    private int m_clientNumber=0;
    public ChatServer(){
        super("聊天程序服务器端");
        Container c=getContentPane();
        m_enter=new JTextField();
        m_enter.setEnabled(false);
        m_enter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try{
                    String s=event.getActionCommand();
                    m_output.writeObject(s);
                    m_output.flush();
                    mb_displayAppend("服务器端:"+s);
                    m_enter.setText("");
                }
                catch (Exception e){
                    System.err.println("发生异常:"+e);
                    e.printStackTrace();
                }
            }
        });
        c.add(m_enter,BorderLayout.NORTH);
        m_display=new JTextArea();
        c.add(new JScrollPane(m_display),BorderLayout.CENTER);
    }
    public void mb_displayAppend(String s){
        m_display.append(s+"\n");
        m_display.setCaretPosition(m_display.getText().length());
        m_enter.requestFocusInWindow();
    }
    public boolean mb_isEndSession(String m){
        if(m.equalsIgnoreCase("q")) return(true);
        if(m.equalsIgnoreCase("quit")) return(true);
        if(m.equalsIgnoreCase("exit")) return(true);
        if(m.equalsIgnoreCase("end")) return(true);
        if(m.equalsIgnoreCase("结束")) return(true);
        return false;
    }
    public void mb_run(){
        try{
            ServerSocket server=new ServerSocket(5000);
            String m;
            while(true){
                m_clientNumber++;
                mb_displayAppend("等待连接["+m_clientNumber+"]");
                Socket s=server.accept();
                mb_displayAppend("接受到客户端连接["+m_clientNumber+"]");
                m_output=new ObjectOutputStream(s.getOutputStream());
                m_input=new ObjectInputStream(s.getInputStream());
                m_output.writeObject("连接成功");
                m_output.flush();
                m_enter.setEnabled(true);
                do{
                    m=(String)m_input.readObject();
                    mb_displayAppend("客户端:"+m);
                }while(!mb_isEndSession(m));
                m_output.writeObject("q");
                m_output.flush();
                m_enter.setEnabled(false);
                m_output.close();
                m_input.close();
                s.close();
                mb_displayAppend("连接["+m_clientNumber+"]结束");
            }
        }
        catch (Exception e){
            System.err.println("发生异常:"+e);
            e.printStackTrace();
            mb_displayAppend("连接["+m_clientNumber+"]发生异常");
        }
    }
    public static void main(String args[]){
        ChatServer app=new ChatServer();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setSize(350,150);
        app.setVisible(true);
        app.mb_run();
    }



}
