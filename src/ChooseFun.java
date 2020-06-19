import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ChooseFun implements Runnable {
    public Map<Integer, PCB> allProcess = new HashMap<Integer, PCB>();
    public int processNum = 0;     // 表示现有进程数
    public Address addressHead = new Address(0, 199);

    @Override
    public void run() {
        try {
            while (true) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                String string = bufferedReader.readLine();

                switch (string) {
                    case "create":
                        if (!create()) {
                            System.out.println("进程创建失败");
                        }
                        break;
                    case "block":
                        if (!block()) {
                            System.out.println("阻塞进程失败失败");
                        }
                        break;
                    case "wake":
                        if (!wake()) {
                            System.out.println("唤醒进程失败失败");
                        }
                        break;
                    case "stop":
                        if (!stop()) {
                            System.out.println("终止进程失败失败");
                        }
                        break;
                    case "show":
                        show();
                        break;
                    default:
                        System.out.println("输入错误，请从新输入！！！");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean create(){
        int[] pcbNum = new int[3];
        BufferedReader bufferedReader;
        try{
            System.out.print("id: ");
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            pcbNum[0] = Integer.parseInt(bufferedReader.readLine());
            System.out.print("maxRam: ");
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            pcbNum[1] = Integer.parseInt(bufferedReader.readLine());
            System.out.print("time: ");
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            pcbNum[2] = Integer.parseInt(bufferedReader.readLine());
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }

        Address address = getAddress(pcbNum[1]);

        if(address.start == -1 && address.end == -1) return false;

        if (processNum == 0){
            // 若现在没有进程，则刚创建的进程开始运行
            System.out.println("id: "
                    + pcbNum[0]
                    + "\tstatus: Running"
                    + "\tmaxRam: "
                    + pcbNum[1]
                    + "\taddress: "
                    + address.start
                    + "--"
                    + address.end
                    + "\ttime: "
                    + pcbNum[2]);
            allProcess.put(0, new PCB(pcbNum[0], 1, pcbNum[1], pcbNum[2], address));
        } else {
            // 若存在进程，则创建进程变为ready状态
            System.out.println("id: " + pcbNum[0] + "\tstatus: Ready" + "\tmaxRam: " + pcbNum[1] + "\taddress: " + address.start + "--" + address.end + "\ttime: " + pcbNum[2]);
            allProcess.put(getMaxKey() + 1, new PCB(pcbNum[0], 0, pcbNum[1], pcbNum[2], address));
        }
        processNum += 1;
        return true;
    }

    private boolean block() {
        int flag = 0;
        for(Integer key: allProcess.keySet()){
            if(flag == 1){
                allProcess.get(key).setStatus(1);
                break;
            }
            if (allProcess.get(key).getStatus() == 1){
                allProcess.get(key).setStatus(2);
                flag = 1;
            }
        }
        return true;
    }

    private boolean wake() {
        System.out.print("id: ");
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String string = bufferedReader.readLine();
            for(Integer key: allProcess.keySet()){
                if(allProcess.get(key).getStatus() == 2 && allProcess.get(key).getId() == Integer.parseInt(string)){
                    // 找到输入id对应的key值
                    if(allProcess.size() != 1){
                        // 如果不是最后一个，则将其变为ready状态
                        PCB pcb = new PCB(allProcess.get(key).getId(), 0, allProcess.get(key).getMaxRam(), allProcess.get(key).getTime(), allProcess.get(key).getAddressPCB());

                        allProcess.put(getMaxKey() + 1, pcb);
                        allProcess.get(key).setStatus(-1);

                        break;
                    } else {
                        // 若是最后一个，则进程开始运行
                        allProcess.get(key).setStatus(1);
                        break;
                    }

                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return true;
    }

    private boolean stop() {
        System.out.print("id: ");
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String string = bufferedReader.readLine();
            for(Integer key: allProcess.keySet()){
                if(allProcess.get(key).getStatus() == 1 && allProcess.get(key).getId() == Integer.parseInt(string)){
                    // 找到输入id对应的key值
                    if(processNum != 1){ allProcess.get(getNextKey(key)).setStatus(1); } // 若还存在进程，则将其变成running状态
                    allProcess.get(key).setStatus(-1);
                    processNum -= 1;
                    delAddress(allProcess.get(key).getAddressPCB());
                    break;
                }
            }
        } catch (IOException e){ e.printStackTrace(); }
        return true;
    }

    public int getMaxKey(){
        int maxKey = 0;
        for(Integer key: allProcess.keySet()){ maxKey = key; }
        return maxKey;
    }

    public int getNextKey(int tmp){
        int nextKey = 0, flag = 0;
        for(Integer key: allProcess.keySet()){
            if(flag == 1){
                nextKey = key;
                break;
            }
            if (key == tmp){ flag = 1; }
        }
        return nextKey;
    }

    public Address getAddress(int maxLength){
        Address addressPCB = new Address(-1, -1);
        Address addressTmp = addressHead;
        while(true){
            if (addressTmp.length >= maxLength){
                addressPCB = new Address(addressTmp.start, addressTmp.start + maxLength - 1);
                addressTmp.start += maxLength;
                addressTmp.length -= maxLength;
                break;
            }
            if(addressTmp.next != null){
                addressTmp = addressTmp.next;
            } else {
                System.out.println("内存不足，创建进程失败！！！");
                break;
            }
        }
        return addressPCB;
    }

    public void delAddress(Address address){
        if (addressHead.start > address.end){ // 归还内存在最前面
            address.next = addressHead;
            addressHead = address;
            updateAddress();
            return;
        } else {
            Address addressTmp = addressHead;
            while(addressTmp != null){
                if (addressTmp.end < address.start && addressTmp.next != null && addressTmp.next.start > address.end){ // 归还内存在中间
                    address.next = addressTmp.next;
                    addressTmp.next = address;
                    updateAddress();
                    return;
                } else if (addressTmp.next == null && addressTmp.end < address.start){ // 归还内存在最后
                    addressTmp.next = address;
                    updateAddress();
                    return;
                }
                addressTmp = addressTmp.next;
            }
        }
    }

    public void updateAddress(){
        Address addressTmp = addressHead;
        while(addressTmp.next != null){
            if (addressTmp.end == addressTmp.next.start - 1){
                addressTmp.end = addressTmp.next.end;
                addressTmp.length += addressTmp.next.length;
                addressTmp.next = addressTmp.next.next;
            } else {
                addressTmp = addressTmp.next;
            }
        }
    }

    public void show(){
        Address addressTmp = addressHead;
        while(addressTmp != null){
            System.out.println(addressTmp.start + "  " + addressTmp.end + "  " + addressTmp.length);
            addressTmp = addressTmp.next;
        }
    }
}