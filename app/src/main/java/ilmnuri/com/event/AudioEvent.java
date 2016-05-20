package ilmnuri.com.event;

/**
 * Created by User on 19.05.2016.
 */
public class AudioEvent {
    private Type type;
    private int current_size;
    private int total_size;
    private int id;

    public static AudioEvent resume(int id, int progress, int total_size) {
        AudioEvent e = new AudioEvent();
        e.type = Type.DOWNLOAD;
        e.id = id;
        e.current_size = progress;
        e.total_size = total_size;
        return e;
    }

    public static AudioEvent stop(int id) {
        AudioEvent e = new AudioEvent();
        e.type = Type.STOP;
        e.id = id;
        return e;
    }

    public enum Type {
        DOWNLOAD,
        STOP;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public int getTotal_size() {
        return total_size;
    }

    public int getCurrent_size() {
        return current_size;
    }
}
