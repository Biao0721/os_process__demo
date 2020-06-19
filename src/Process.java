import javax.swing.*;
import java.awt.*;

public class Process extends JFrame implements Runnable{
    private JTextArea jTextArea = new JTextArea();
    private ChooseFun chooseFun = new ChooseFun();

    Process(){
        this.setTitle("进程管理器");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(800, 600);
        this.add(jTextArea);
        this.setLocationRelativeTo(null);
        jTextArea.setFont(new Font("Serif", 0, 15));

        new Thread(chooseFun).start();
        new Thread(this).start();
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000);
                jTextArea.setText("");
                for(Integer key : chooseFun.allProcess.keySet()){
                    // 若为-1，则表示进程已经完成，或进程已经被stop掉了
                    if(chooseFun.allProcess.get(key).getStatus() != -1){
                        jTextArea.append(" id: "
                                + chooseFun.allProcess.get(key).getId()
                                + " \t| status: ");
                        /*输出各个状态下数字对应的进程状态
                        * 0 - ready
                        * 1- running
                        * 2 - waiting*/
                        if(chooseFun.allProcess.get(key).getStatus() == 0){
                            jTextArea.append("Ready  ");
                        } else if (chooseFun.allProcess.get(key).getStatus() == 1){
                            jTextArea.append("Running");
                        } else {
                            jTextArea.append("Waiting");
                        }
                        jTextArea.append("\t| maxRam: "
                                + chooseFun.allProcess.get(key).getMaxRam()
                                + "\t| time: "
                                + chooseFun.allProcess.get(key).getTime()
                                + "\t| address: "
                                + chooseFun.allProcess.get(key).addressPCB.start
                                + "--"
                                + chooseFun.allProcess.get(key).addressPCB.end
                                + "\n");

                        // 对正在运行的进程时间减一
                        if(chooseFun.allProcess.get(key).getStatus() == 1){
                            chooseFun.allProcess.get(key).setTime(
                                    chooseFun.allProcess.get(key).getTime() - 1
                            );

                            // 如果时间小于0，则表示进程已经结束
                            if (chooseFun.allProcess.get(key).getTime() <= 0){
                                if(chooseFun.processNum != 1){
                                    chooseFun.allProcess.get(
                                            chooseFun.getNextKey(key)
                                    ).setStatus(1);
                                }

                                chooseFun.allProcess.get(key).setStatus(-1);
                                chooseFun.processNum -= 1;
                                chooseFun.delAddress(
                                        chooseFun.allProcess.get(key)
                                                .getAddressPCB()
                                );
                            }
                        }
                    }
                }
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}