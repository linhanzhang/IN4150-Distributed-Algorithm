// package Lab2;
public enum MessageType {

    MESSAGE(1),
    MARKER(2);

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
