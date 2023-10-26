package nl.han.ica.datastructures;

public class HANQueue<T> implements IHANQueue<T>{

    IHANLinkedList<T> list = new HANLinkedList<>();

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean isEmpty() {
        return (list.get(0) == null);
    }

    @Override
    public void enqueue(T value) {
        list.insert(list.getSize(), value);

    }

    @Override
    public T dequeue() {
        var result = list.getFirst();
        list.removeFirst();
        return result;
    }

    @Override
    public T peek() {
        return list.getFirst();
    }

    @Override
    public int getSize() {
        return list.getSize();
    }
}
