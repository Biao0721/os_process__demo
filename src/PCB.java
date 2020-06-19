public class PCB{
    public int id;                // 进程id
    public int status;            // 进程状态
    public int maxRam;            // 所需资源
    public int time;              // 运行时间
    public Address addressPCB;    // 具体存储位置

    /*0-ready
    * 1-running
    * 2-waiting*/

    PCB(int _id, int _status, int _maxRam, int _time, Address _addressPCB){
        this.id = _id;
        this.status = _status;
        this.maxRam = _maxRam;
        this.time = _time;
        this.addressPCB = _addressPCB;
    }

    public int getId(){
        return this.id;
    }

    public int getStatus(){
        return this.status;
    }

    public int getMaxRam(){
        return this.maxRam;
    }

    public int getTime(){
        return this.time;
    }

    public Address getAddressPCB() { return this.addressPCB; }

    public void setStatus(int _status){
        this.status = _status;
    }

    public void setTime(int _time){
        this.time = _time;
    }
}
