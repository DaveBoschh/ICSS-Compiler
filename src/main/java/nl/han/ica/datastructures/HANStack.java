package nl.han.ica.datastructures;

import nl.han.ica.icss.ast.ASTNode;

import java.util.ArrayList;

public class HANStack<T> implements IHANStack<T> {

    IHANLinkedList<T> list = new HANLinkedList<>();

    @Override
    public void push(T value) {
        list.insert(list.getSize(), value);
    }

    @Override
    public T pop() {
        T result = list.get(list.getSize() - 1);
        list.delete(list.getSize() - 1);

        return result;
    }

    @Override
    public T peek() {
        return list.get(list.getSize() - 1);

    }
}

