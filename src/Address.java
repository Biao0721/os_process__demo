public class Address {
    public int start;
    public int end;
    public int length;
    public Address next = null;

    Address(int _start, int _end){
        this.start = _start;
        this.end = _end;
        this.length = end - start + 1;
    }
}
