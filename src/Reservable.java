public interface Reservable {
    void reserve();
    void release();
    boolean isAvailable();
}